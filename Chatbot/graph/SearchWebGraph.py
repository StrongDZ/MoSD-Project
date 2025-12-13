from langgraph.graph import StateGraph, START, END
from langchain.agents import tool
from langgraph.prebuilt import ToolNode, tools_condition
from langchain_core.messages import HumanMessage, AIMessage
from typing import List, TypedDict
from langchain.vectorstores import Qdrant
from qdrant_client import QdrantClient
from IPython.display import Image, display
from langchain.embeddings import OpenAIEmbeddings
from langchain.tools import tool
from langchain_community.tools import TavilySearchResults
from langgraph.graph import MessagesState
from langchain_core.messages import HumanMessage, SystemMessage
from langchain_community.document_loaders import WikipediaLoader
import operator
from typing import Annotated
from langchain_openai import ChatOpenAI
from .settings import OPENAI_API_KEY
llm = ChatOpenAI(model="gpt-4o", api_key=OPENAI_API_KEY)

class SearchState(MessagesState):
    query: str
    context: Annotated[list, operator.add]
    answer: str

def search_web(state):
    """ Retrieve docs from web search about everything """
    # Search
    tavily_search = TavilySearchResults(max_results=5)
    search_docs = tavily_search.invoke(state['query'])
    print(search_docs)
    print(type(search_docs))
     # Format
    formatted_search_docs = "\n\n---\n\n".join(
        [
            f'<Document href="{doc["url"]}">\n{doc["content"]}\n</Document>'
            for doc in search_docs
        ]
    )
    print(type(formatted_search_docs))
    return {"context": [formatted_search_docs]}
    
def generate_answer(state):
    """ Generate a recommendation based on the context """
    query = state["query"]
    context = state["context"]
    prompt_template = """
Bạn là một chuyên gia trả lời câu hỏi có sẵn từ google. Bạn đã nhận được dữ liệu từ google như sau: {context}"""
    answer_instructions = prompt_template.format(context=context)
    print(state['messages'])
    response = llm.invoke([SystemMessage(content=answer_instructions)] + state['messages'])
    return {"messages": response}

search_builder = StateGraph(SearchState)
search_builder.add_node("search_web", search_web)
search_builder.add_node("generate_answer", generate_answer)
search_builder.add_edge(START, "search_web")
search_builder.add_edge("search_web", "generate_answer")
search_builder.add_edge("generate_answer", END)
search_graph = search_builder.compile()