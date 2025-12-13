import operator
from langgraph.graph import StateGraph, START, END
from typing import Annotated
from IPython.display import Image, display
from langgraph.graph import MessagesState
from langchain_core.messages import SystemMessage
from langchain_openai import ChatOpenAI
from .settings import OPENAI_API_KEY
from langchain_community.document_loaders import WikipediaLoader
import operator
from typing import Annotated
from langchain_openai import ChatOpenAI
llm = ChatOpenAI(model="gpt-4o", api_key=OPENAI_API_KEY)
class WikiState(MessagesState):
    query: str
    context: Annotated[list, operator.add]
def search_wikipedia(state):
    
    """ Retrieve docs from wikipedia """

    # Search
    search_docs = WikipediaLoader(query=state['query'], 
                                  load_max_docs=6).load()

     # Format
    formatted_search_docs = "\n\n---\n\n".join(
        [
            f'<Document source="{doc.metadata["source"]}" page="{doc.metadata.get("page", "")}">\n{doc.page_content}\n</Document>'
            for doc in search_docs
        ]
    )
    return {"context": [formatted_search_docs]}     
def generateWiki(state):
    """ Generate a recommendation based on the context """
    query = state["query"]
    context = state["context"]
    prompt_template = """
Bạn là một chuyên gia trả lời câu hỏi có sẵn từ wikipedia. Bạn đã nhận được dữ liệu từ wikipedia như sau: {context}

Hãy phân tích, tổng hợp để trả lời câu hỏi của người dùng."""
    answer_instructions = prompt_template.format(context=context)
    response = llm.invoke([SystemMessage(content=answer_instructions)] + state['messages'])
    return {"messages": response}
    
ws_builder = StateGraph(WikiState)
ws_builder.add_node("search_wikipedia", search_wikipedia)
ws_builder.add_node("generateWiki", generateWiki)
ws_builder.add_edge(START, "search_wikipedia")
ws_builder.add_edge("search_wikipedia", "generateWiki")
ws_builder.add_edge("generateWiki", END)
ws_graph = ws_builder.compile()