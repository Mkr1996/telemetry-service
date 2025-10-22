-- Enable pgvector extension (for semantic vector search)
CREATE EXTENSION IF NOT EXISTS vector;

-- Create embeddings table for RAG-based AI insights
CREATE TABLE IF NOT EXISTS telemetry_embeddings (
  id           BIGSERIAL PRIMARY KEY,
  machine_id   BIGINT NOT NULL REFERENCES machine(id) ON DELETE CASCADE,
  ts           TIMESTAMPTZ NOT NULL,
  temperature  DOUBLE PRECISION,
  vibration    DOUBLE PRECISION,
  pressure     DOUBLE PRECISION,
  embedding    vector(384)  -- embedding dimension for all-MiniLM-L6-v2 (SentenceTransformers)
);

-- Create approximate nearest-neighbor index for fast similarity search
CREATE INDEX IF NOT EXISTS telemetry_embeddings_ivfflat
  ON telemetry_embeddings
  USING ivfflat (embedding vector_cosine_ops)
  WITH (lists = 100);

-- Ensure query planner optimizes this table
ANALYZE telemetry_embeddings;
