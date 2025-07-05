from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator, Info

from app.routes import general, suggestion

app = FastAPI(
    title="LLM Suggestion Service",
    description="Service that generates suggestions using an LLM",
    version="1.0.0"
)

instrumentator = (
    Instrumentator()
    .add(lambda: Info(
        "fastapi_app_info",
        "FastAPI application info",
        {"app_name": "genai"}
    ))
    .instrument(app, metric_namespace='genai', metric_subsystem='genai')
    .expose(app)
)

app.include_router(general.router, prefix="/genai", tags=["General"])
app.include_router(suggestion.router, prefix="/genai", tags=["Suggestion"]) 
