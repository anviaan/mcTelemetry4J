#!/bin/bash
set -euo pipefail

echo "=== MySQL → PostgreSQL Data Migration ==="
echo "This script uses pgloader to migrate data."
echo ""
echo "Prerequisites:"
echo "  - pgloader installed (brew install pgloader / apt install pgloader)"
echo "  - Both MySQL and PostgreSQL accessible"
echo ""

read -rp "MySQL host [localhost]: " MYSQL_HOST
MYSQL_HOST=${MYSQL_HOST:-localhost}

read -rp "MySQL port [3306]: " MYSQL_PORT
MYSQL_PORT=${MYSQL_PORT:-3306}

read -rp "MySQL database [mc_telemetry]: " MYSQL_DB
MYSQL_DB=${MYSQL_DB:-mc_telemetry}

read -rp "MySQL user [root]: " MYSQL_USER
MYSQL_USER=${MYSQL_USER:-root}

read -rsp "MySQL password: " MYSQL_PASS
echo ""

read -rp "PostgreSQL host [localhost]: " PG_HOST
PG_HOST=${PG_HOST:-localhost}

read -rp "PostgreSQL port [5432]: " PG_PORT
PG_PORT=${PG_PORT:-5432}

read -rp "PostgreSQL database [mc_telemetry]: " PG_DB
PG_DB=${PG_DB:-mc_telemetry}

read -rp "PostgreSQL user [mc_user]: " PG_USER
PG_USER=${PG_USER:-mc_user}

read -rsp "PostgreSQL password: " PG_PASS
echo ""

cat <<EOF > /tmp/pgloader.load
LOAD DATABASE
     FROM mysql://${MYSQL_USER}:${MYSQL_PASS}@${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DB}
     INTO postgresql://${PG_USER}:${PG_PASS}@${PG_HOST}:${PG_PORT}/${PG_DB}

WITH data only,
     batch rows = 10000,
     batch concurrency = 4

CAST type bigint to bigint drop typemod,
     type varchar to varchar drop typemod;

ALTER SCHEMA '${MYSQL_DB}' RENAME TO 'public';
EOF

echo ""
echo "=== Starting migration... ==="
pgloader /tmp/pgloader.load

echo ""
echo "=== Migration complete. Run scripts/verify-migration.sql to verify. ==="
rm -f /tmp/pgloader.load
