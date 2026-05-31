-- Verify migration integrity between MySQL and PostgreSQL
-- Run this in BOTH databases and compare results

SELECT 'mc_mod'    AS table_name, COUNT(*) AS row_count FROM mc_mod
UNION ALL
SELECT 'telemetry' AS table_name, COUNT(*) AS row_count FROM telemetry;

-- Check telemetry count sums match (data integrity)
SELECT SUM(count) AS total_telemetry_hits FROM telemetry;

-- Sample: verify no nulls in critical columns
SELECT COUNT(*) AS null_mod_id FROM telemetry WHERE mod_id IS NULL;
SELECT COUNT(*) AS null_count FROM telemetry WHERE count IS NULL;
