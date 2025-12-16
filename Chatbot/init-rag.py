#!/usr/bin/env python3
"""
Non-interactive script to initialize RAG vector database
This script automatically creates the vector database without user input
"""
import sys
import os
from dotenv import load_dotenv

# Load environment variables
load_dotenv('.env')

# Add parent directory to path to import rag_qdrant functions
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))

from rag_qdrant import load_data, create_qdrant_db

def main():
    print("=== Auto-initializing RAG Vector Database ===")
    
    # Get Qdrant host and port from environment or use defaults
    host = os.getenv('QDRANT_HOST', 'qdrant')
    port = int(os.getenv('QDRANT_PORT', '6333'))
    collection_name = os.getenv('QDRANT_COLLECTION', 'hotels_and_ship_and_restaurants')
    
    print(f"Connecting to Qdrant at {host}:{port}")
    print(f"Collection name: {collection_name}")
    
    try:
        # Load data from CSV files
        print("\n📖 Loading data from CSV files...")
        doc = load_data()
        print(f"✅ Loaded {len(doc)} documents")
        
        # Create vector database
        print(f"\n🔨 Creating vector database...")
        vectorstore = create_qdrant_db(doc, host, port, collection_name)
        
        print("\n✅ Vector database initialized successfully!")
        print(f"   Collection: {collection_name}")
        print(f"   Documents: {len(doc)}")
        
        return 0
        
    except Exception as e:
        print(f"\n❌ Error: {e}")
        import traceback
        traceback.print_exc()
        return 1

if __name__ == "__main__":
    sys.exit(main())

