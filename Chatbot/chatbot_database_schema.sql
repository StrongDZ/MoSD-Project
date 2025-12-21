-- ============================================
-- CHATBOT DATABASE SCHEMA
-- Database: chatbot_db
-- Purpose: Lưu trữ chat history và user memories cho Chatbot AI
-- ============================================

-- ============================================
-- TABLE: memories
-- Purpose: Lưu trữ chat history và user preferences
-- ============================================
CREATE TABLE IF NOT EXISTS memories (
    -- Namespace để phân loại memory theo user/session
    -- Ví dụ: "user_123", "session_abc", "preferences"
    namespace TEXT NOT NULL,
    
    -- Key duy nhất cho mỗi memory item
    -- Format: namespace:memory_id hoặc uuid
    key TEXT PRIMARY KEY,
    
    -- Nội dung memory dưới dạng JSONB
    -- Cho phép query và index trên JSON data
    value JSONB NOT NULL,
    
    -- Timestamp tự động khi tạo record
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Timestamp tự động cập nhật mỗi khi record thay đổi
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    -- Metadata bổ sung (optional)
    metadata JSONB DEFAULT '{}'::jsonb
);

-- ============================================
-- INDEXES
-- ============================================

-- Index cho tìm kiếm theo namespace (most common query)
CREATE INDEX IF NOT EXISTS idx_memories_namespace 
ON memories(namespace);

-- Index cho tìm kiếm trong JSONB content
CREATE INDEX IF NOT EXISTS idx_memories_value_gin 
ON memories USING gin(value);

-- Index cho tìm kiếm theo thời gian (để cleanup old data)
CREATE INDEX IF NOT EXISTS idx_memories_created_at 
ON memories(created_at DESC);

-- Composite index cho query phổ biến: namespace + created_at
CREATE INDEX IF NOT EXISTS idx_memories_namespace_created 
ON memories(namespace, created_at DESC);

-- ============================================
-- TRIGGERS & FUNCTIONS
-- ============================================

-- Function tự động update updated_at timestamp
CREATE OR REPLACE FUNCTION update_memories_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger để tự động update updated_at
DROP TRIGGER IF EXISTS trigger_update_memories_timestamp ON memories;
CREATE TRIGGER trigger_update_memories_timestamp
    BEFORE UPDATE ON memories
    FOR EACH ROW
    EXECUTE FUNCTION update_memories_timestamp();

-- ============================================
-- OPTIONAL TABLES (Uncomment nếu cần mở rộng)
-- ============================================

/*
-- TABLE: chat_sessions
-- Purpose: Quản lý các session chat
CREATE TABLE IF NOT EXISTS chat_sessions (
    session_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id INTEGER REFERENCES "user"(user_id),
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    session_metadata JSONB DEFAULT '{}'::jsonb,
    is_active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_chat_sessions_user ON chat_sessions(user_id);
CREATE INDEX idx_chat_sessions_active ON chat_sessions(is_active, started_at DESC);
*/

/*
-- TABLE: chat_messages
-- Purpose: Lưu chi tiết từng tin nhắn trong conversation
CREATE TABLE IF NOT EXISTS chat_messages (
    message_id SERIAL PRIMARY KEY,
    session_id UUID REFERENCES chat_sessions(session_id) ON DELETE CASCADE,
    role VARCHAR(20) NOT NULL CHECK (role IN ('user', 'assistant', 'system')),
    content TEXT NOT NULL,
    tokens_used INTEGER,
    model_used VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    message_metadata JSONB DEFAULT '{}'::jsonb
);

CREATE INDEX idx_chat_messages_session ON chat_messages(session_id, created_at);
CREATE INDEX idx_chat_messages_created ON chat_messages(created_at DESC);
*/

/*
-- TABLE: user_preferences
-- Purpose: Lưu preferences của user cho chatbot
CREATE TABLE IF NOT EXISTS user_preferences (
    user_id INTEGER PRIMARY KEY REFERENCES "user"(user_id) ON DELETE CASCADE,
    preferred_language VARCHAR(10) DEFAULT 'vi',
    preferred_destinations TEXT[],
    budget_range_min INTEGER,
    budget_range_max INTEGER,
    travel_style VARCHAR(50),
    interests TEXT[],
    preferences_data JSONB DEFAULT '{}'::jsonb,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_user_preferences_destinations ON user_preferences USING gin(preferred_destinations);
*/

-- ============================================
-- UTILITY FUNCTIONS
-- ============================================

-- Function để cleanup old memories (older than X days)
CREATE OR REPLACE FUNCTION cleanup_old_memories(days_old INTEGER DEFAULT 90)
RETURNS INTEGER AS $$
DECLARE
    deleted_count INTEGER;
BEGIN
    DELETE FROM memories 
    WHERE created_at < CURRENT_TIMESTAMP - (days_old || ' days')::INTERVAL;
    
    GET DIAGNOSTICS deleted_count = ROW_COUNT;
    RETURN deleted_count;
END;
$$ LANGUAGE plpgsql;

-- Function để get memory statistics
CREATE OR REPLACE FUNCTION get_memory_stats()
RETURNS TABLE (
    total_memories BIGINT,
    unique_namespaces BIGINT,
    oldest_memory TIMESTAMP,
    newest_memory TIMESTAMP,
    avg_memories_per_namespace NUMERIC
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        COUNT(*)::BIGINT as total_memories,
        COUNT(DISTINCT namespace)::BIGINT as unique_namespaces,
        MIN(created_at) as oldest_memory,
        MAX(created_at) as newest_memory,
        ROUND(COUNT(*)::NUMERIC / NULLIF(COUNT(DISTINCT namespace), 0), 2) as avg_memories_per_namespace
    FROM memories;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- SAMPLE QUERIES (Comment - for reference)
-- ============================================

-- Query 1: Lấy tất cả memories của một user
-- SELECT * FROM memories WHERE namespace = 'user_123' ORDER BY created_at DESC;

-- Query 2: Tìm kiếm trong JSON content
-- SELECT * FROM memories WHERE value @> '{"type": "hotel_preference"}';

-- Query 3: Đếm số memories theo namespace
-- SELECT namespace, COUNT(*) FROM memories GROUP BY namespace ORDER BY COUNT(*) DESC;

-- Query 4: Xóa memories cũ hơn 90 ngày
-- SELECT cleanup_old_memories(90);

-- Query 5: Xem thống kê
-- SELECT * FROM get_memory_stats();

-- Query 6: Lấy memories mới nhất của user
-- SELECT * FROM memories 
-- WHERE namespace = 'user_123' 
-- ORDER BY created_at DESC 
-- LIMIT 10;

-- ============================================
-- PERMISSIONS (Optional - uncomment nếu cần)
-- ============================================

/*
-- Tạo role cho chatbot application
CREATE ROLE chatbot_app WITH LOGIN PASSWORD 'your_secure_password';

-- Grant permissions
GRANT CONNECT ON DATABASE chatbot_db TO chatbot_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON TABLE memories TO chatbot_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO chatbot_app;
*/

-- ============================================
-- COMPLETION MESSAGE
-- ============================================
\echo '✅ Chatbot database schema created successfully!'
\echo ''
\echo '📊 Tables created:'
\echo '   - memories (chat history and user memories)'
\echo ''
\echo '🔧 Functions created:'
\echo '   - update_memories_timestamp()'
\echo '   - cleanup_old_memories(days_old)'
\echo '   - get_memory_stats()'
\echo ''
\echo '📝 Next steps:'
\echo '   1. Review the schema and uncomment optional tables if needed'
\echo '   2. Update .env file with database credentials'
\echo '   3. Test the connection: python -c "from config import DATABASE_URL; print(DATABASE_URL)"'
\echo '   4. Start chatbot: cd Web-Based && uvicorn main:app --reload'
