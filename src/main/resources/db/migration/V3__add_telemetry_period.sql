ALTER TABLE telemetry ADD COLUMN period VARCHAR(7) NOT NULL DEFAULT to_char(CURRENT_DATE, 'YYYY-MM');

ALTER TABLE telemetry DROP CONSTRAINT telemetry_pkey;
ALTER TABLE telemetry ADD PRIMARY KEY (mod_id, period, game_version, mod_version, loader);

CREATE INDEX idx_telemetry_period ON telemetry(period);
