# McTelemetry4J

McTelemetry4J is a small service for collecting **anonymous, aggregated usage telemetry** from Minecraft mods. It helps
mod authors understand which mod, Minecraft, and loader versions are in use so they can prioritize maintenance and
updates.

It stores counters rather than player identities. Telemetry is retained by month, which makes it possible to compare
usage over time without deleting previous periods.

## What is collected

Each telemetry event contains:

- Mod ID and mod version
- Minecraft game version
- Loader (for example, Fabric or Forge)
- The UTC month in which the event is received (`YYYY-MM`)

The service does not collect usernames, UUIDs, IP addresses as telemetry data, or other player-identifying information.
IP addresses are used transiently only to enforce the public endpoint's rate limit.

Mods using this service can let players opt out by editing the mod's `telemetry.json` file in its configuration
directory. The service also exposes the privacy notice at its root URL.

## Integrating a mod

Before a mod can report data, an administrator registers it once. Mod management requires HTTP Basic authentication:

```bash
curl -u "$API_USERNAME:$API_PASSWORD" \
  -H 'Content-Type: application/json' \
  -d '{"mod_id":"example-mod","mod_name":"Example Mod"}' \
  https://telemetry.example.com/mods
```

Then the mod can send anonymous telemetry to the public endpoint:

```bash
curl -X POST \
  -H 'Content-Type: application/json' \
  -d '{"mod_id":"example-mod","mod_version":"1.2.0","game_version":"1.21.1","loader":"fabric"}' \
  https://telemetry.example.com/data
```

Successful submissions return `201 Created`. An unknown mod returns `404 Not Found`.

### Rate limit

`POST /data` is limited to **20 requests per client IP per minute**. When the limit is reached, the API returns
`429 Too Many Requests`, a JSON error body, and a `Retry-After` header. Clients should wait for the advertised number of
seconds before retrying.

## Viewing telemetry

Export routes require HTTP Basic authentication. The optional `period` parameter must be in `YYYY-MM` format; omit it to
retrieve all retained periods.

| Endpoint                           | Result                              |
|------------------------------------|-------------------------------------|
| `GET /export/csv?period=2026-07`   | Download detailed telemetry as CSV  |
| `GET /export/json?period=2026-07`  | Detailed telemetry as JSON          |
| `GET /export/stats?period=2026-07` | Counts aggregated by mod and period |

For example:

```bash
curl -u "$API_USERNAME:$API_PASSWORD" \
  'https://telemetry.example.com/export/stats?period=2026-07'
```

The API also accepts the compatibility prefix `/telemetry`, such as `/telemetry/data` and `/telemetry/export/stats`.

## API documentation

Interactive API documentation is available at [`/swagger-ui/index.html`](/swagger-ui/index.html). The OpenAPI
specification is available in JSON at [`/api-docs`](/api-docs) and in YAML at [`/api-docs.yaml`](/api-docs.yaml).
The documentation is public; operations that manage mods or export telemetry still require HTTP Basic authentication.

## Development and deployment

See [the developer guide](docs/DEVELOPMENT.md) for local setup, tests, Docker deployment, database migrations,
Cloudflare Tunnel configuration, and operational verification.
