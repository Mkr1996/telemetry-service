-- Create a continuous aggregate for hourly machine temperature trends
CREATE MATERIALIZED VIEW IF NOT EXISTS telemetry_hourly_avg
WITH (timescaledb.continuous) AS
SELECT
    time_bucket('1 hour', ts) AS bucket,
    machine_id,
    avg(temperature) AS avg_temp,
    avg(vibration)  AS avg_vibration,
    avg(pressure)   AS avg_pressure
FROM telemetry
GROUP BY bucket, machine_id
WITH NO DATA;

-- Automatically refresh the continuous aggregate as new data arrives
SELECT add_continuous_aggregate_policy('telemetry_hourly_avg',
    start_offset => INTERVAL '7 days',
    end_offset   => INTERVAL '1 hour',
    schedule_interval => INTERVAL '1 hour');
