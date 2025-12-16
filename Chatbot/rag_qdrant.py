"""
RAG Retrieval System - Qdrant Implementation
Tạo và quản lý vector database cho hotels, ships và restaurants với Qdrant
"""

import pandas as pd
from dotenv import load_dotenv
from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import Qdrant
from qdrant_client import QdrantClient
from qdrant_client.models import Distance, VectorParams

# Load environment variables
load_dotenv('.env')


def load_data():
    """Load data từ CSV files và tạo documents"""
    print("📖 Đang load data từ CSV files...")
    
    doc = []
    
    # Load hotels
    csv_file = "data/hotel_with_nearest_restaurants.csv"
    df = pd.read_csv(csv_file)
    
    # Load ships
    ship_file = "data/ship.csv"
    df3 = pd.read_csv(ship_file)
    for idx, row in df3.iterrows():
        doc.append(Document(
            page_content=f"Tàu {row['ship_name']} có link trên web MonkeyDvuvi là {row['link_web']}, có số cabin là {row['cabin']}, trip đi {row['trip']} bởi công ty {row['admin']}, giá thành là {row['ship_price']}, địa chỉ ở {row['address']} với link map {row['map_link']}. Thuyền gồm các tiện ích sau {row['ship_features']}, {row['long_description']}\n",
            metadata={"type": "ship"}
        ))
    
    # Load ship rooms
    room_ship_file = "data/ship_rooms.csv"
    df4 = pd.read_csv(room_ship_file)
    df4 = df4.merge(df3[['ship_id', 'ship_name']], on='ship_id', how='left')
    
    for idx, row in df4.iterrows():
        doc.append(Document(
            page_content=f"Tàu {row['ship_id']} có phòng loại {row['room_name']}, có kích thước {row['size']}m2, chứa được {row['max_persons']}. Giá phòng là {row['room_price']} và có các đặc trưng sau {row['room_features']}\n",
            metadata={"type": "ship_room", "ship_name": row['ship_name']}
        ))
    
    # Load hotels
    for idx, row in df.iterrows():
        doc.append(Document(
            page_content=f"Khách sạn {row['hotel_name']} có link trên web MonkeyDvuvi là {row['link_web']} với tổng cộng {row['total_rooms']} phòng, được quản lý bởi {row['admin']}, có giá {row['hotel_price']} đồng, nằm tại {row['city']}, địa chỉ {row['address']}, với đường dẫn bản đồ {row['map_link']}, có các tiện ích {row['hotel_features']}, và có các nhà hàng gần nhất:\n{row['nearest_restaurants']}, mô tả ngắn gọn: {row['short_description']}, mô tả chi tiết: {row['long_description']} \n",
            metadata={"city": row['city'], "type": "hotel"}
        ))
    
    # Load restaurants
    df2 = pd.read_csv("data/restaurant_final.csv")
    for idx, row in df2.iterrows():
        doc.append(Document(
            page_content=f"Nhà hàng {row['name']} có địa chỉ {row['address']} với link Map {row['map_link']} có đánh giá {row['rating']}, giờ mở cửa là {row['open_hours']}, website {row['website']}, và số điện thoại là {row['phone']} \n",
            metadata={"type": "restaurant"}
        ))
    
    print(f"✅ Đã load {len(doc)} documents")
    return doc


def create_qdrant_db(doc, host="localhost", port=6333, collection_name="hotels_and_ship_and_restaurants"):
    """Tạo vector database với Qdrant"""
    print("🔨 Đang tạo Qdrant vector database...")
    
    # Connect to Qdrant
    client = QdrantClient(host=host, port=port)
    print(f"✅ Đã kết nối tới Qdrant server: {host}:{port}")
    
    # Split documents
    splitter = RecursiveCharacterTextSplitter(chunk_size=800, chunk_overlap=150)
    split_docs = splitter.split_documents(doc)
    print(f"📄 Đã split thành {len(split_docs)} chunks")
    
    # Create embeddings
    embeddings = OpenAIEmbeddings(chunk_size=200)
    
    # Recreate collection
    print("🗑️ Đang xóa collection cũ (nếu có)...")
    client.recreate_collection(
        collection_name=collection_name,
        vectors_config=VectorParams(size=1536, distance=Distance.COSINE),
    )
    
    # Index documents
    print("⏳ Đang embed và index documents (có thể mất vài phút)...")
    vectorstore = Qdrant.from_documents(
        documents=split_docs,
        embedding=embeddings,
        location=f"http://{host}:{port}",
        collection_name=collection_name
    )
    
    print(f"✅ Vector database đã được tạo: {collection_name}")
    return vectorstore


def load_qdrant_db(host="localhost", port=6333, collection_name="hotels_and_ship_and_restaurants"):
    """Load vector database từ Qdrant server"""
    print("📂 Đang load Qdrant vector database...")
    
    # Connect to Qdrant
    client = QdrantClient(host=host, port=port)
    embeddings = OpenAIEmbeddings()
    
    # Load vectorstore
    vectorstore = Qdrant(
        client=client,
        collection_name=collection_name,
        embeddings=embeddings
    )
    
    print(f"✅ Đã load vector database từ: {host}:{port}/{collection_name}")
    return vectorstore


def search(vectorstore, query, k=10):
    """Tìm kiếm trong vector database"""
    print(f"🔍 Đang tìm kiếm: {query}")
    results = vectorstore.similarity_search(query, k=k)
    print(f"✅ Tìm thấy {len(results)} kết quả")
    return results


def main():
    """Main function"""
    print("=" * 60)
    print("RAG RETRIEVAL SYSTEM - QDRANT")
    print("=" * 60)
    
    host = "localhost"
    port = 6333
    collection_name = "hotels_and_ship_and_restaurants"
    
    print("\n⚠️  LƯU Ý: Cần chạy Qdrant server trước!")
    print("   Docker: docker run -p 6333:6333 qdrant/qdrant")
    print("   Hoặc: Download từ https://qdrant.tech/documentation/quick-start/")
    
    print("\n📌 Chọn tác vụ:")
    print("1. Tạo mới vector database")
    print("2. Load vector database có sẵn")
    choice = input("Nhập lựa chọn [1/2]: ").strip()
    
    try:
        if choice == "1":
            # Tạo mới database
            doc = load_data()
            vectorstore = create_qdrant_db(doc, host, port, collection_name)
        else:
            # Load database có sẵn
            vectorstore = load_qdrant_db(host, port, collection_name)
        
        # Test search
        print("\n" + "=" * 60)
        print("TEST SEARCH")
        print("=" * 60)
        query = "Xây dựng một tour đi Tràng An và các điểm liên quan 2 ngày 1 đêm"
        results = search(vectorstore, query, k=10)
        
        print("\n📋 Kết quả tìm kiếm:")
        for i, doc in enumerate(results[:3], 1):
            print(f"\n{i}. {doc.page_content[:200]}...")
        
        print("\n✅ Hoàn tất!")
        
    except Exception as e:
        print(f"\n❌ Lỗi: {e}")
        print("\n💡 Kiểm tra:")
        print("   1. Qdrant server có đang chạy không?")
        print("   2. Port 6333 có bị chiếm không?")
        print("   3. Collection đã tồn tại chưa (nếu chọn option 2)?")


if __name__ == "__main__":
    main()
