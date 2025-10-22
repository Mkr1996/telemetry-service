FROM python:3.11-slim

WORKDIR /app

# Install system dependencies
RUN apt-get update && apt-get install -y build-essential libpq-dev && rm -rf /var/lib/apt/lists/*

# Copy requirements (if needed for basics)
COPY requirements.txt .

# Install general dependencies first
RUN pip install --no-cache-dir -r requirements.txt

# Install a compatible CPU-only PyTorch
RUN pip install --no-cache-dir torch==2.1.2+cpu -f https://download.pytorch.org/whl/cpu/torch_stable.html

# Install compatible versions of SentenceTransformers & HuggingFace Hub
RUN pip install --no-cache-dir "sentence-transformers==2.2.2" "huggingface_hub==0.19.4"

# Test model import after compatible install
RUN python -c "from sentence_transformers import SentenceTransformer; SentenceTransformer('sentence-transformers/all-MiniLM-L6-v2')"

# Copy application code
COPY app.py .

# Expose and run
EXPOSE 8000
ENV PYTHONUNBUFFERED=1

CMD ["uvicorn", "app:app", "--host", "0.0.0.0", "--port", "8000"]
