-- Create mods table
CREATE TABLE IF NOT EXISTS `mc_mod`
(
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    mod_id   VARCHAR(50) UNIQUE NOT NULL,
    mod_name VARCHAR(100)       NOT NULL
);

CREATE TABLE IF NOT EXISTS `telemetry`
(
    mod_id       BIGINT      NOT NULL,
    game_version VARCHAR(10) NOT NULL,
    mod_version  VARCHAR(10) NOT NULL,
    loader       VARCHAR(25) NOT NULL,
    count        BIGINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (mod_id, game_version, mod_version, loader),
    FOREIGN KEY (mod_id) REFERENCES `mc_mod` (id) ON DELETE CASCADE
);

CREATE INDEX idx_telemetry_mod_id ON `telemetry` (mod_id);
CREATE INDEX idx_telemetry_game_version ON `telemetry` (game_version);