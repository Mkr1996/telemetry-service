-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- Drop tables if they already exist (useful for rebuilds)
DROP TABLE IF EXISTS telemetry CASCADE;
DROP TABLE IF EXISTS machine CASCADE;

-- Create Machine metadata table
CREATE TABLE machine (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    location TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create Telemetry table
-- Add 'id' so it matches your Java entity
CREATE TABLE telemetry (
    id BIGSERIAL,
    machine_id BIGINT NOT NULL REFERENCES machine(id) ON DELETE CASCADE,
    ts TIMESTAMPTZ NOT NULL,
    temperature DOUBLE PRECISION NOT NULL,
    vibration DOUBLE PRECISION NOT NULL,
    pressure DOUBLE PRECISION,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (id, ts)
);


-- Convert to Timescale hypertable
SELECT create_hypertable('telemetry', 'ts', if_not_exists => TRUE);

-- Create indexes for faster queries
CREATE INDEX IF NOT EXISTS idx_telemetry_machine_ts ON telemetry (machine_id, ts DESC);
CREATE INDEX IF NOT EXISTS idx_machine_name ON machine (name);
