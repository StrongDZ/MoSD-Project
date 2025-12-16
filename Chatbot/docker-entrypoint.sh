#!/bin/sh
set +e

echo "=== Chatbot Container Startup ==="

# Initialize PostgreSQL database first
echo "Initializing PostgreSQL database..."
/app/init-db.sh

# Get Qdrant configuration from environment
QDRANT_HOST="${QDRANT_HOST:-qdrant}"
QDRANT_PORT="${QDRANT_PORT:-6333}"

# Wait for Qdrant to be ready
echo "Waiting for Qdrant to be ready at $QDRANT_HOST:$QDRANT_PORT..."
RETRIES=30
until python3 -c "import urllib.request; urllib.request.urlopen('http://$QDRANT_HOST:$QDRANT_PORT/readyz').read()" 2>/dev/null || [ $RETRIES -eq 0 ]; do
  echo "Qdrant is unavailable - sleeping ($RETRIES retries left)"
  RETRIES=$((RETRIES-1))
  sleep 2
done

if [ $RETRIES -eq 0 ]; then
  echo "WARNING: Qdrant is not available after waiting."
else
  echo "Qdrant is ready!"
fi

# Check if vector database needs to be initialized
SKIP_RAG_INIT="${SKIP_RAG_INIT:-false}"

if [ "$SKIP_RAG_INIT" = "true" ]; then
    echo "SKIP_RAG_INIT is set to true, skipping vector database initialization..."
else
    echo "Checking if vector database needs initialization..."
    
    # Check if collections exist in Qdrant
    COLLECTIONS_COUNT=$(python3 -c "
from qdrant_client import QdrantClient
import os
host = os.getenv('QDRANT_HOST', 'qdrant')
port = int(os.getenv('QDRANT_PORT', '6333'))
try:
    client = QdrantClient(host=host, port=port, timeout=10)
    collections = client.get_collections().collections
    print(len(collections))
except Exception as e:
    print('0')
" 2>&1)
    
    if [ -z "$COLLECTIONS_COUNT" ] || [ "$COLLECTIONS_COUNT" = "0" ]; then
        echo "Vector database not found or empty. Initializing..."
        echo "This may take a few minutes..."
        python3 init-rag.py
        if [ $? -eq 0 ]; then
            echo "✅ Vector database initialized successfully!"
        else
            echo "⚠️  Warning: Failed to initialize vector database, continuing anyway..."
        fi
    else
        echo "✅ Vector database already exists ($COLLECTIONS_COUNT collections), skipping initialization."
    fi
fi

# Start the API server
echo "Starting FastAPI server..."
exec uvicorn Web-Based.main:app --host 0.0.0.0 --port 8000

