import os
from dotenv import load_dotenv

load_dotenv()

CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = os.getenv("API_URL", "https://gpu.aet.cit.tum.de/api/chat/completions")
MODEL_NAME = os.getenv("MODEL_NAME", "llama3.3:latest")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")

# Redis配置
REDIS_HOST = os.getenv("REDIS_HOST", "redis")
REDIS_PORT = int(os.getenv("REDIS_PORT", "6379"))
REDIS_DB = int(os.getenv("REDIS_DB", "0"))
REDIS_PASSWORD = os.getenv("REDIS_PASSWORD")
REDIS_TTL = int(os.getenv("REDIS_TTL", "3600"))  # 默认缓存1小时 