import json
import psycopg2

class MemoryItem:
    def __init__(self, key, value):
        self.key = key
        self.value = value
# --- Định nghĩa PostgresStore thay thế InMemoryStore ---
class PostgresStore:
    def __init__(self, dsn):
        self.conn = psycopg2.connect(dsn)
        self._setup_table()

    def _setup_table(self):
        """
        Docstring for _setup_table
        
        :param self: Description
        """
        with self.conn.cursor() as cur:
            cur.execute("""
            CREATE TABLE IF NOT EXISTS memories (
                namespace TEXT,
                key TEXT PRIMARY KEY,
                value JSONB
            );
            """)
            self.conn.commit()

    def put(self, namespace, key, value): 
        """Lưu một mục bộ nhớ vào cơ sở dữ liệu PostgreSQL."""
        with self.conn.cursor() as cur:
            cur.execute("""
            INSERT INTO memories (namespace, key, value)
            VALUES (%s, %s, %s)
            ON CONFLICT (key) DO UPDATE SET value = EXCLUDED.value;
            """, (str(namespace), key, json.dumps(value)))
            self.conn.commit()

    def search(self, namespace):
        """Tìm kiếm các mục bộ nhớ trong một namespace cụ thể."""
        with self.conn.cursor() as cur:
            cur.execute("""
            SELECT key, value FROM memories WHERE namespace = %s;
            """, (str(namespace),))
            rows = cur.fetchall()
        results = []
        for key, value_json in rows:
            results.append(MemoryItem(key=key, value=value_json))
        return results

    def close(self):
        self.conn.close()