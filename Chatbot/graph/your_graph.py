from .RecommendGraph import rs_builder
from .SearchWebGraph import search_builder 
from .SearchWikiGraph import ws_builder
from .memoryCcollection import PostgresStore
from langgraph.graph import StateGraph, START, END
from pydantic import BaseModel, Field
from IPython.display import Image, display
from langchain_core.messages import  SystemMessage
from langchain.schema import HumanMessage, SystemMessage

from langgraph.graph import MessagesState
import operator
import uuid
from langchain_core.runnables.config import RunnableConfig
from typing import Annotated
from langchain_openai import ChatOpenAI
from dotenv import load_dotenv
from trustcall import create_extractor
from langchain_core.messages import merge_message_runs
from fastapi import FastAPI
load_dotenv()
from langchain_core.chat_history import InMemoryChatMessageHistory
from config import DATABASE_URL
chat_history = InMemoryChatMessageHistory()
# Khởi tạo SQLite connection
app = FastAPI()

llm = ChatOpenAI(model="gpt-4o")
class Memory(BaseModel):
    content: str = Field(description="Nội dung chính của bộ nhớ. Ví dụ: Người dùng thích ăn món phở.")

class EntryGraphState(MessagesState):
    query: str
    context: Annotated[list, operator.add]
    subgraph_name: str
    user_id: int
    
    
trustcall_extractor = create_extractor(
    llm,
    tools=[Memory],
    tool_choice="Memory",
    enable_inserts=True,
)

# --- Tin nhắn hệ thống cho chatbot ---


TRUSTCALL_INSTRUCTION = """Hãy suy ngẫm về cuộc tương tác sau đây. Sử dụng các công cụ được cung cấp để lưu lại bất kỳ thông tin cần thiết nào về người dùng. 
Hãy sử dụng gọi công cụ song song để xử lý việc cập nhật và chèn dữ liệu cùng lúc."""

# --- Hàm gọi model dựa trên trạng thái và bộ nhớ ---
def call_model(state: EntryGraphState, config: RunnableConfig):
    print("Config received in call_model:", config)
    store = config["configurable"]["store"]   # Lấy store từ config
    user_id = config["configurable"]["user_id"]
    namespace = ("memories", user_id)
    memories = store.search(namespace)

    print(f"📌 Memories fetched from store for user_id {user_id}:")
    for mem in memories:
        print(f"🧠 {mem.value['content']}")

    info = "\n".join(f"- {mem.value['content']}" for mem in memories)


    print("Response from LLM:", info)
    return {"context": [info]}

# Hàm write_memory cũng tương tự
def write_memory(state: EntryGraphState, config: RunnableConfig):
    store = config["configurable"]["store"] 
    user_id = config["configurable"]["user_id"]
    namespace = ("memories", user_id)

    # Lọc tin nhắn người dùng
    user_messages = [msg for msg in state['messages'] if isinstance(msg, HumanMessage)]

    # Chỉ ghi nhớ khi đã đủ 5 tin nhắn từ người dùng
    if len(user_messages) < 5:
        return  # Không làm gì nếu chưa đủ

    last_5_messages = state['messages'][-5:]
    # Lấy tin nhắn đầy đủ để tóm tắt
    updated_messages = list(merge_message_runs(messages=[SystemMessage(content=TRUSTCALL_INSTRUCTION)] + last_5_messages))

    existing_items = store.search(namespace)
    tool_name = "Memory"
    existing_memories = ([
        (existing_item.key, tool_name, existing_item.value)
        for existing_item in existing_items
    ] if existing_items else None)

    # Gọi TrustCall để tóm tắt và trích xuất memory
    result = trustcall_extractor.invoke({
        "messages": updated_messages,
        "existing": existing_memories
    })

    # Ghi vào store
    for r, rmeta in zip(result["responses"], result["response_metadata"]):
        store.put(
            namespace,
            rmeta.get("json_doc_id", str(uuid.uuid4())),
            r.model_dump(mode="json")
        )
    
def controller(state):
    """ Controller to decide which subgraph to use """
    prompt_template = """Bạn là một trợ lý ảo của web du lịch MonkeyDvuvi, một website hỗ trợ đặt phòng khách sạn, du thuyền trực tuyến. Nhiệm vụ của bạn là trả lời các câu hỏi của người dùng
    các kiến thức về các địa điểm du lịch, nhà hàng, khách sạn, du thuyền tại Việt Nam, ngoài ra có thể gợi ý tour và các lịch trình chi tiết phù hợp với nhu cầu người dùng.
    Dựa trên câu hỏi của người dùng, hãy quyết định subgraph nào nên được kích hoạt để Agent có thể đưa ra câu trả lời tốt nhất cho người dùng.
    Hãy trả lời với một trong các subgraph sau: Recommendation System, Search Wikipedia và Search Web.
    Nếu câu hỏi đưa vào liên quan tới yêu cầu xây dựng tour du lịch, hỏi đáp về nhà hàng, khách sạn cụ thể, hãy gọi tới Recommendation System.
    Nếu câu hỏi đưa vào liên quan tới yêu cầu tìm kiếm thông tin từ wikipedia, hãy gọi tới Search Wikipedia.
    Nếu câu hỏi đưa vào liên quan tới yêu cầu tìm kiếm thông tin từ google, hãy gọi tới Search Web.
    Nếu không có subgraph nào phù hợp, hãy gọi tới answer.
    LƯU Ý: CHỈ ĐƯA RA MỘT TRONG CÁC GIÁ TRỊ NÀY, KHÔNG ĐƯA RA CÁC GIÁ TRỊ KHÁC.
    Hãy đưa ra câu trả lời ngắn gọn và súc tích.
    Đây là câu hỏi của người dùng: {query}"""
    print("The prior messages are:" , state['messages']) 
    messages = llm.invoke(prompt_template.format(query=state["query"]))
    if messages.content == "Recommendation System":
        print("Recommendation System")
        return {"subgraph_name": "Recommendation System"}
    elif messages.content == "Search Wikipedia":
        print("Search Wikipedia")
        return {"subgraph_name": "Search Wikipedia"}
    elif messages.content == "Search Web":
        print("Search Web")
        return {"subgraph_name": "Search Web"}
    else:
        return {"subgraph_name": "answer"}

def answer(state):
    prompt_template = f"""
Bạn là một trợ lý ảo của web du lịch MonkeyDvuvi, một website hỗ trợ đặt phòng khách sạn, du thuyền trực tuyến. 
Bạn hãy trả lời các câu hỏi tương tác với người dùng một cách tự nhiên và thân thiện, nếu cần thiết hãy vui tính.

Ngữ cảnh hiện tại:
{state['context']}
Hãy trả lời câu hỏi của người dùng dựa trên ngữ cảnh hiện tại và các thông tin đã có trong bộ nhớ.
"""
    print("The prior messages are:", state['messages']) 
    response = llm.invoke([SystemMessage(content=prompt_template)] + state['messages'])
    return {"messages": response}


def condition_tools(state):
    if state['subgraph_name'] == "Recommendation System":
        return "Recommendation System"
    elif state['subgraph_name'] == "Search Wikipedia":
        return "Search Wikipedia"
    elif state['subgraph_name'] == "Search Web":
        return "Search Web"
    elif state['subgraph_name'] == "answer":
        return "answer" 
    
across_thread_memory = PostgresStore(DATABASE_URL) 

builder = StateGraph(EntryGraphState)
builder.add_node("call_model", call_model)
builder.add_node("write_memory", write_memory)
builder.add_node("controller", controller)
builder.add_node("Recommendation System", rs_builder.compile())
builder.add_node("Search Wikipedia", ws_builder.compile())
builder.add_node("Search Web", search_builder.compile())
builder.add_node("answer", answer)
builder.add_edge(START, "call_model")
builder.add_edge("call_model", "controller")
builder.add_conditional_edges(
    "controller",
    condition_tools,
    {
        "Recommendation System": "Recommendation System",
        "Search Wikipedia": "Search Wikipedia",
        "Search Web": "Search Web",
        "answer": "answer"
    }
)
builder.add_edge("Recommendation System", "write_memory")
builder.add_edge("Search Wikipedia", "write_memory")
builder.add_edge("Search Web", "write_memory")
builder.add_edge("answer", "write_memory")
builder.add_edge("write_memory", END)
graph = builder.compile()