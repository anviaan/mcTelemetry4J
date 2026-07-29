-- Repair databases whose Flyway history says V3 is applied but whose
-- telemetry primary key is missing or still uses the pre-period columns.

ALTER TABLE telemetry
    ADD COLUMN IF NOT EXISTS period VARCHAR(7);

UPDATE telemetry
SET period = to_char(CURRENT_DATE, 'YYYY-MM')
WHERE period IS NULL;

ALTER TABLE telemetry
    ALTER COLUMN period SET DEFAULT to_char(CURRENT_DATE, 'YYYY-MM'),
    ALTER COLUMN period SET NOT NULL;

-- The existing database can contain rows that are duplicates under the
-- intended key. Consolidate them before recreating the constraint so counts
-- are preserved rather than discarded.
CREATE TEMP TABLE telemetry_aggregated ON COMMIT DROP AS
SELECT mod_id,
       period,
       game_version,
       mod_version,
       loader,
       SUM(count)::BIGINT AS count
FROM telemetry
GROUP BY mod_id, period, game_version, mod_version, loader;

-- Drop whichever primary-key constraint is actually present. Its name can
-- differ when the schema was restored or managed outside Flyway.
DO $$
DECLARE
    primary_key_name TEXT;
BEGIN
    SELECT conname
    INTO primary_key_name
    FROM pg_constraint
    WHERE conrelid = 'telemetry'::regclass
      AND contype = 'p';

    IF primary_key_name IS NOT NULL THEN
        EXECUTE format('ALTER TABLE telemetry DROP CONSTRAINT %I', primary_key_name);
    END IF;
END
$$;

DELETE FROM telemetry;

INSERT INTO telemetry (mod_id, period, game_version, mod_version, loader, count)
SELECT mod_id, period, game_version, mod_version, loader, count
FROM telemetry_aggregated;

ALTER TABLE telemetry
    ADD CONSTRAINT telemetry_pkey
    PRIMARY KEY (mod_id, period, game_version, mod_version, loader);

CREATE INDEX IF NOT EXISTS idx_telemetry_period ON telemetry(period);
