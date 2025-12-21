# 🚀 HƯỚNG DẪN CHẠY CHATBOT

## Yêu cầu
- Python >= 3.9
- PostgreSQL >= 12
- Docker

---

## 1. Cài đặt

```bash
cd MoSD-Project\Chatbot
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install --upgrade pip
pip install -r requirements.txt
```

---

## 2. Cấu hình .env

Tạo file `.env`:

```env
OPENAI_API_KEY=your_key
TAVILY_API_KEY=your_key
DB_USERNAME=postgres
DB_PASSWORD=your_password
DB_NAME=chatbot_db
DB_HOST=localhost
DB_PORT=5432
```

---

## 3. Setup PostgreSQL

```bash
psql -U postgres
```

```sql
CREATE DATABASE chatbot_db;
\c chatbot_db

CREATE TABLE memories (
    namespace TEXT NOT NULL,
    key TEXT PRIMARY KEY,
    value JSONB NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_memories_namespace ON memories(namespace);

CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_update_memories_updated_at
    BEFORE UPDATE ON memories
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

---

## 4. Khởi động Qdrant

### Lần đầu (tạo container mới):
```bash
docker run -d --name qdrant -p 6333:6333 qdrant/qdrant
```

### Các lần sau (start container đã có):
```bash
docker start qdrant
```

### Kiểm tra container:
```bash
# Xem container đang chạy
docker ps

# Xem tất cả container (kể cả đã dừng)
docker ps -a

# Stop container
docker stop qdrant

# Xóa container
docker rm qdrant
```

---

## 5. Tạo Vector Database

```bash
python rag_qdrant.py
```

---

## 6. Chạy API Server

```bash
uvicorn Web-Based.main:app --reload --host 0.0.0.0 --port 8000
```

API: http://localhost:8000/docs

---

## Script nhanh (Windows)

```powershell
.\venv\Scripts\Activate.ps1
docker start qdrant 2>$null || docker run -d --name qdrant -p 6333:6333 qdrant/qdrant
cd Web-Based
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```
