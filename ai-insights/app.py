import os
from datetime import datetime
from typing import Optional, List, Dict

import numpy as np
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
import psycopg
from psycopg.rows import dict_row
from pgvector.psycopg import register_vector
from sentence_transformers import SentenceTransformer

DB_HOST = os.getenv("DB_HOST", "db")
DB_PORT = int(os.getenv("DB_PORT", "5432"))
DB_NAME = os.getenv("DB_NAME", "telemetrydb")
DB_USER = os.getenv("DB_USER", "postgres")
DB_PASS = os.getenv("DB_PASS", "postgres")
EMBED_MODEL_NAME = os.getenv("EMBED_MODEL", "sentence-transformers/all-MiniLM-L6-v2")
EMBED_DIM = int(os.getenv("EMBED_DIM", "384"))

app = FastAPI(title="AI Insights Service")
model = SentenceTransformer(EMBED_MODEL_NAME)

def get_conn():
    conn = psycopg.connect(
        host=DB_HOST, port=DB_PORT, dbname=DB_NAME, user=DB_USER, password=DB_PASS,
        row_factory=dict_row
    )
    register_vector(conn)
    return conn

class TelemetryIn(BaseModel):
    machineId: int = Field(..., alias="machineId")
    temperature: float
    vibration: float
    pressure: float
    ts: datetime

class InsightRequest(BaseModel):
    machineId: Optional[int] = None
    telemetry: Optional[TelemetryIn] = None
    topK: int = 5

def telemetry_to_text(t: TelemetryIn) -> str:
    return (
        f"Machine {t.machineId}, Temp {t.temperature:.2f}, Vib {t.vibration:.4f}, "
        f"Pressure {t.pressure:.2f}, Time {t.ts.isoformat()}"
    )

def embed_text(text: str) -> List[float]:
    vec = model.encode([text], normalize_embeddings=True)[0]
    return vec.astype(np.float32).tolist()

def mock_ai_response(latest: TelemetryIn, neighbors: List[Dict]) -> str:
    response = "AI-Based Diagnosis (Simulated)*:\n"
    if latest.temperature > 90:
        response += "-High temperature detected. Potential overheating.\n"
    if latest.vibration > 0.035:
        response += "-Abnormal vibration. Possible mechanical imbalance.\n"
    if latest.pressure > 110:
        response += "-Pressure spike observed. Check pneumatic systems.\n"
    if not neighbors:
        response += "-No similar incidents found. Consider monitoring more frequently.\n"
    else:
        response += f"- Found {len(neighbors)} similar historical patterns. Review maintenance logs.\n"
    response += "\n Recommended Actions: Reduce load, inspect cooling systems, and run predictive diagnostic.\n"
    return response

@app.get("/health")
def health():
    return {"status": "ok", "embedding_model": EMBED_MODEL_NAME, "dim": EMBED_DIM}

@app.post("/embed")
def embed_telemetry(t: TelemetryIn):
    text = telemetry_to_text(t)
    vec = embed_text(text)
    if len(vec) != EMBED_DIM:
        raise HTTPException(500, "Embedding dimension mismatch")
    with get_conn() as conn, conn.cursor() as cur:
        cur.execute("""
            INSERT INTO telemetry_embeddings (machine_id, ts, temperature, vibration, pressure, embedding)
            VALUES (%s, %s, %s, %s, %s, %s)
        """, (t.machineId, t.ts, t.temperature, t.vibration, t.pressure, vec))
    return {"status": "success", "machineId": t.machineId}

@app.post("/insights")
def insights(req: InsightRequest):
    with get_conn() as conn, conn.cursor() as cur:
        if req.telemetry:
            latest = req.telemetry
            query_vec = embed_text(telemetry_to_text(latest))
        elif req.machineId:
            cur.execute("""
                SELECT machine_id, ts, temperature, vibration, pressure, embedding
                FROM telemetry_embeddings
                WHERE machine_id = %s
                ORDER BY ts DESC LIMIT 1
            """, (req.machineId,))
            row = cur.fetchone()
            if not row:
                raise HTTPException(404, "No data available")
            latest = TelemetryIn(
                machineId=row["machine_id"], ts=row["ts"],
                temperature=row["temperature"], vibration=row["vibration"], pressure=row["pressure"]
            )
            query_vec = row["embedding"]
        else:
            raise HTTPException(400, "Provide telemetry or machineId")

        cur.execute("""
            SELECT id, machine_id, ts, temperature, vibration, pressure
            FROM telemetry_embeddings
            ORDER BY embedding <=> %s
            LIMIT %s
        """, (query_vec, req.topK))
        neighbors = cur.fetchall()

    guidance = mock_ai_response(latest, neighbors)
    return {"latest": latest, "neighbors": neighbors, "insight": guidance}
