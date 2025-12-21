from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
import sys
import os
from langchain.schema import HumanMessage, SystemMessage

from graph.memoryCollection import PostgresStore
from typing import Optional

# Setup path để import graph
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
from graph.your_graph import graph
from config import DATABASE_URL

from langchain_core.chat_history import InMemoryChatMessageHistory

# --- FastAPI init ---
app = FastAPI()

# --- Cấu hình CORS ---
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # hoặc giới hạn cụ thể origin như "http://localhost"
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- DTO tương thích với Spring ---
class ChatRequest(BaseModel):
    userId: Optional[int] = None
    message: str

# --- Dùng chat history (bạn có thể thay đổi về session nếu cần thiết) ---
chat_history = InMemoryChatMessageHistory()
user_sessions = {}
across_thread_memory = PostgresStore(DATABASE_URL)

# --- Endpoint chính ---
@app.post("/chat")
def chat_endpoint(chat: ChatRequest):
    user_msg = chat.message
    # Lưu tin nhắn người dùng
    chat_history.add_user_message(user_msg)
    human_messages = [m for m in chat_history.messages if isinstance(m, HumanMessage)]
    print(f"User id: {chat.userId}, message: {user_msg}")
    # Gọi LangGraph
    inputs = {
        "messages": chat_history.messages,
        "query": user_msg,
        "context": [],
    }
    graph_response = graph.invoke(
    inputs,
    config={"configurable": {"user_id": chat.userId}, "store": across_thread_memory}  # <-- thêm dòng này
)
    # Trích xuất phản hồi từ bot
    if isinstance(graph_response, dict) and "messages" in graph_response:
        bot_reply = graph_response["messages"][-1].content if graph_response["messages"] else "No reply"
    else:
        bot_reply = "Error: No valid response from LangGraph"

    # Lưu phản hồi bot
    chat_history.add_ai_message(bot_reply)
    # Nếu có đủ 5 tin nhắn Human thì xóa toàn bộ lịch sử chat
    if len(human_messages) >= 5:
        chat_history.messages.clear()
        print("Chat history cleared after processing 5 HumanMessages.")
    # Trả về dạng chuỗi, tương thích với .bodyToMono(String.class)
    return {"data": bot_reply}
