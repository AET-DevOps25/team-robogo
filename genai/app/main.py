from fastapi import FastAPI
from app.api import router

app = FastAPI(
    title="LLM Suggestion Service",
    description="Service that generates suggestions using an LLM",
    version="1.0.0"
)

app.include_router(router) 