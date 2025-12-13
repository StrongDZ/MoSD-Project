# settings.py
import os
from dotenv import load_dotenv

# Load .env file
load_dotenv()

# OpenAI Settings
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# Qdrant Settings
QDRANT_URL = os.getenv("QDRANT_URL")
QDRANT_API_KEY = os.getenv("QDRANT_API_KEY")

# Other configs (nếu có)
OTHER_CONFIG = os.getenv("OTHER_CONFIG")

# Validate (optional)
if not OPENAI_API_KEY:
    raise ValueError("OPENAI_API_KEY not set in .env file")