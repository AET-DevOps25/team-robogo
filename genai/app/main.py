from fastapi import FastAPI
from app.api import router
from prometheus_fastapi_instrumentator import Instrumentator


app = FastAPI(
    title="LLM Suggestion Service",
    description="Service that generates suggestions using an LLM",
    version="1.0.0"
)

Instrumentator().instrument(app).expose(app, include_in_schema=False, endpoint="/metrics")

app.include_router(router, prefix="/genai") 