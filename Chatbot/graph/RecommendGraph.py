import os
from langgraph.graph import StateGraph, START, END, MessagesState
from typing import Annotated
import operator

from langchain_community.vectorstores import Qdrant
from qdrant_client import QdrantClient
from langchain_openai import OpenAIEmbeddings
from langchain_community.tools import TavilySearchResults
from langchain_core.messages import SystemMessage
from langchain_openai import ChatOpenAI
from .settings import OPENAI_API_KEY

llm = ChatOpenAI(model="gpt-4o", api_key=OPENAI_API_KEY)


class RecommendState(MessagesState):
    query: str
    context: Annotated[list, operator.add]


def RAG_retrieval(state):
    """Recommend hotels, restaurants based on user input"""
    qdrant_host = os.getenv("QDRANT_HOST", "qdrant")
    qdrant_port = int(os.getenv("QDRANT_PORT", "6333"))
    collection_name = os.getenv("QDRANT_COLLECTION", "hotels_and_ship_and_restaurants")

    # Connect to Qdrant
    client = QdrantClient(host=qdrant_host, port=qdrant_port)
    embeddings = OpenAIEmbeddings()

    # Load vectorstore từ collection đã tồn tại
    # Sử dụng client như trong rag_qdrant.py
    vectorstore = Qdrant(client=client, collection_name=collection_name, embeddings=embeddings)
    context_items = vectorstore.similarity_search(state["query"], k=5)
    # Lấy nội dung của các tài liệu
    context_text = "\n".join(
        doc.page_content if hasattr(doc, "page_content") else str(doc)
        for sublist in context_items
        for doc in (sublist if isinstance(sublist, list) else [sublist])
    )
    context_text = "Dưới đây là thông tin về các khách sạn, nhà hàng, du thuyền trên cơ sở dữ liệu của website MonkeyDvuvi: " + context_text
    return {"context": [context_text]}


def search_web(state):
    """Retrieve docs from web search about everything related to tourism"""

    # Search
    tavily_search = TavilySearchResults(max_results=5)
    search_docs = tavily_search.invoke(state["query"])
    # Format
    formatted_search_docs = "\n\n---\n\n".join([f'<Document href="{doc["url"]}">\n{doc["content"]}\n</Document>' for doc in search_docs])
    formatted_search_docs = "Dưới đây là thông tin về các khách sạn, nhà hàng, du thuyền trong được search từ trên mạng: " + formatted_search_docs
    return {"context": [formatted_search_docs]}


def generateRecommend(state):
    """Generate a recommendation based on the context"""
    context = "\n\n".join(str(item) for item in state["context"])
    query = state["messages"]
    prompt_template = """
Bạn là một trợ lý ảo của web du lịch MonkeyDvuvi, một website hỗ trợ đặt phòng khách sạn, du thuyền trực tuyến. Nhiệm vụ của bạn là trả lời các câu hỏi của người dùng
 về các địa điểm du lịch, nhà hàng, khách sạn, du thuyền tại Việt Nam, ngoài ra có thể gợi ý tour và các lịch trình chi tiết phù hợp với nhu cầu người dùng. Bạn hãy trả lời câu hỏi của tôi dựa trên thông tin mà tôi đã cung cấp cho bạn. Các câu hỏi chủ yếu về các địa điểm du lịch, và cách xây dựng tour du lịch hợp lí bao gồm khách sạn, nhà hàng cũng như các địa điểm du lịch nổi tiếng gần đó.
Dưới đây là thông tin về một khách sạn mà tôi đã tìm thấy: {context}

Hãy chỉ đưa ra các khách sạn, du thuyền, nhà hàng trên cơ sở dữ liệu của website MonkeyDvuvi, không lấy từ search web. Còn lại có thể dùng các thông
tin khác từ search web nếu muốn. Hãy cung cấp thông tin chi tiết về khách sạn, nhà hàng và tour du lịch theo như tôi yêu cầu, sử dụng các thông tin đã có và bổ sung thêm nếu có. Nếu có các thông tin về đường dẫn tới map hay facebook, website của khách sạn hay nhà hàng, hãy cung cấp cho tôi, đặc biệt là link của khách sạn hay nhà hàng đó trên website MonkeyDvuvi. Nếu không có thông tin nào, hãy nói là không có thông tin nào.
"""
    answer_instructions = prompt_template.format(context=context)
    response = llm.invoke([SystemMessage(content=answer_instructions)] + state["messages"])
    return {"messages": response}


rs_builder = StateGraph(RecommendState)
rs_builder.add_node("RAG_retrieval", RAG_retrieval)
rs_builder.add_node("search_web", search_web)
rs_builder.add_node("generateRecommend", generateRecommend)

rs_builder.add_edge(START, "RAG_retrieval")
rs_builder.add_edge(START, "search_web")
rs_builder.add_edge("RAG_retrieval", "generateRecommend")
rs_builder.add_edge("search_web", "generateRecommend")
rs_builder.add_edge("generateRecommend", END)
rs_graph = rs_builder.compile()
