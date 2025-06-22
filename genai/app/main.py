from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator

from app.routes import general, suggestion

app = FastAPI(
    title="LLM Suggestion Service",
    description="Service that generates suggestions using an LLM",
    version="1.0.0"
)

Instrumentator().instrument(app).expose(app, include_in_schema=False, endpoint="/metrics")

routes = [
    general.router,
    suggestion.router
]

for route in routes:
    app.include_router(route, prefix="/genai", tags=[route.tags[0]]) 
