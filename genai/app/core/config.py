import os
from dotenv import load_dotenv

load_dotenv()

CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = os.getenv("API_URL", "https://gpu.aet.cit.tum.de/api/chat/completions")
MODEL_NAME = os.getenv("MODEL_NAME", "llama3.3:latest") 