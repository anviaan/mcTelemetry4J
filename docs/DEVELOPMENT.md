# McTelemetry4J developer guide

## Requirements

- Docker Engine with Docker Compose v2 for the full stack
- Java 25 to run the application directly
- The included Maven Wrapper (`./mvnw`); a separate Maven installation is not required

## Configuration

Create a local environment file from the tracked template:

```bash
cp .env.example .env
```

Do not commit `.env`. Replace every placeholder before deploying.

| Variable                                  | Purpose                                                                                                                                                                                 |
|-------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `CF_TUNNEL_TOKEN`                         | Cloudflare Tunnel token used only by `compose.prod.yaml`. It is not needed for local development.                                                                                       |
| `DB_PASSWORD`                             | Password for PostgreSQL and the backend database connection.                                                                                                                            |
| `DB_BIND_HOST`                            | Host interface for PostgreSQL; keep `127.0.0.1` in production unless private database access is explicitly required.                                                                    |
| `API_USERNAME` / `API_PASSWORD`           | HTTP Basic credentials for mod management and telemetry exports.                                                                                                                        |
| `PROD_RATE_LIMIT_TRUSTED_PROXY_ADDRESSES` | Comma-separated exact IP addresses allowed to supply `X-Forwarded-For` when `CF-Connecting-IP` is unavailable. Production defaults to `10.250.0.2`, the fixed address of `cloudflared`. |
| `APP_BIND_HOST`                           | Development-only host interface for the backend port; keep `127.0.0.1` for local-only access.                                                                                           |
| `APP_IMAGE`                               | Backend image used by `compose.prod.yaml`.                                                                                                                                              |

The rate limiter uses the non-empty `CF-Connecting-IP` header first. Cloudflare sets this header to the original client
IP, and it remains available through Cloudflare Tunnel and an intermediate reverse proxy such as Traefik. If the header
is absent or blank, `PROD_RATE_LIMIT_TRUSTED_PROXY_ADDRESSES` is a security boundary: only a connection from an address
in this list can determine the client IP from `X-Forwarded-For`. Do not add player IPs or public Cloudflare edge ranges.

Because `CF-Connecting-IP` is trusted whenever it is present, the backend must not be exposed directly to untrusted
clients. In production, omit the host port publication and keep the backend reachable only through the private network
shared with the intended proxy. The backend directly receives requests from the local `cloudflared` container, so
`10.250.0.2` remains the trusted fallback address.

The Cloudflare Tunnel public hostname must use `http://telemetry_backend:8080` as its origin. This is the Compose
service name on the private production network; do not use `localhost:8080`, the host address, or a published backend
port. The production backend has no host-published port and is reachable only by services attached to
`telemetry_network`.

`compose.yaml` is the development stack: it builds the local source, publishes the API and database on loopback, and
does not start Cloudflared. `compose.prod.yaml` deploys the registry image, Cloudflared, and the fixed production
network.

## Local development

### Unit tests and package

```bash
./mvnw test
./mvnw package
```

### Run the database and application locally

Start only PostgreSQL in Docker:

```bash
docker compose up -d telemetry_db
```

Load the local values into the current shell, then run the backend on the host using the database port mapped by
Compose:

```bash
set -a
source .env
set +a

DB_HOST=localhost \
DB_PORT=5433 \
DB_NAME=mc_telemetry \
DB_USERNAME=mc_user \
DB_PASSWORD="$DB_PASSWORD" \
API_USERNAME="$API_USERNAME" \
API_PASSWORD="$API_PASSWORD" \
./mvnw spring-boot:run
```

Use a second terminal for local checks:

```bash
curl http://localhost:8080/telemetry/health

curl -u "$API_USERNAME:$API_PASSWORD" \
  -H 'Content-Type: application/json' \
  -d '{"mod_id":"example-mod","mod_name":"Example Mod"}' \
  http://localhost:8080/mods

curl -H 'Content-Type: application/json' \
  -d '{"mod_id":"example-mod","mod_version":"1.0.0","game_version":"1.21.1","loader":"fabric"}' \
  http://localhost:8080/data

curl -u "$API_USERNAME:$API_PASSWORD" \
  'http://localhost:8080/export/stats?period=2026-07'

curl -u "$API_USERNAME:$API_PASSWORD" \
  'http://localhost:8080/export/periods'
```

Stop local services when finished:

```bash
docker compose down
```

### Test the containerized application with Compose

Compose is also useful during development to test the same backend image and PostgreSQL wiring used in deployment. Start
both services explicitly; this does **not** start `cloudflared`, so no real tunnel or `CF_TUNNEL_TOKEN` is required:

```bash
docker compose up -d --build telemetry_db telemetry_backend
docker compose ps
docker compose logs --tail=100 telemetry_db telemetry_backend
docker compose exec telemetry_backend wget -qO- http://localhost:8080/telemetry/health
```

The containerized backend is available from the host at `http://localhost:8080`, including Swagger UI at
`http://localhost:8080/swagger-ui/index.html`.

Stop the development stack and remove its database volume when a clean test database is needed:

```bash
docker compose down -v
```

## Database migrations

Flyway applies the SQL files in `src/main/resources/db/migration` when the application starts. Migration V3 adds the
monthly `period` column and preserves all existing counters by assigning them the month when V3 is applied.

Never modify a migration that may already have run in an environment. Add a new versioned migration instead. The
`period` value for new events is generated in UTC and uses `YYYY-MM`.

## Production deployment

1. Provision a Cloudflare Tunnel for the public hostname, configure its origin as `http://telemetry_backend:8080`, and
   copy its token into `CF_TUNNEL_TOKEN`.
2. On the host, create `.env` from `.env.example` and replace all example values with strong secrets. Keep
   `DB_BIND_HOST=127.0.0.1`.
3. Build and start the complete stack:

   ```bash
   docker compose -f compose.prod.yaml pull
   docker compose -f compose.prod.yaml up -d
   ```

4. Verify container health and logs:

   ```bash
   docker compose -f compose.prod.yaml ps
   docker compose -f compose.prod.yaml logs --tail=100 telemetry_backend cloudflared
   docker compose -f compose.prod.yaml exec telemetry_backend wget -qO- http://localhost:8080/telemetry/health
   ```

5. Verify the tunnel container still has the expected address and that the backend received the matching trusted-proxy
   setting:

   ```bash
   docker inspect telemetry_tunnel --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}'
   docker compose -f compose.prod.yaml exec telemetry_backend printenv RATE_LIMIT_TRUSTED_PROXY_ADDRESSES
   ```

The expected value for both checks is `10.250.0.2`. If the network configuration changes, update the fixed container
address and `RATE_LIMIT_TRUSTED_PROXY_ADDRESSES` together before redeploying.

The production backend does not publish port 8080 to the host. Public traffic reaches it through `cloudflared`, which
connects to `http://telemetry_backend:8080` on the private Compose network and supplies the original client IP in
`CF-Connecting-IP`. `APP_BIND_HOST` applies only to the development Compose file; PostgreSQL remains bound to loopback
by default.

Verify that the backend has no host-published port and that a container on the production network can reach it:

```bash
docker inspect telemetry_backend --format '{{json .NetworkSettings.Ports}}'
docker run --rm --network mctelemetry4j_telemetry_network curlimages/curl:8.12.1 \
  -fsS http://telemetry_backend:8080/telemetry/health
```

The first command must show no host binding for `8080/tcp`; the second must return a healthy response. A request to
`http://127.0.0.1:8080` on the host must fail, while the public Cloudflare hostname must continue to work.

## API behavior to verify after deployment

- `POST /data` and `POST /telemetry/data` are public and return `201` for a registered mod.
- After 20 requests from one client IP in a minute, the next request returns `429` with `Retry-After`.
- `GET /export/**` and `GET /telemetry/export/**` require HTTP Basic authentication.
- `GET /export/periods` and `GET /telemetry/export/periods` return distinct stored periods in descending order, or `[]`
  when the telemetry table is empty.
- `DELETE /data` is intentionally unavailable and returns `405`.
- Export filters accept only `period=YYYY-MM`; invalid values return `400`.

The old n8n reporting workflow is not managed by this repository and is outside this deployment procedure.
