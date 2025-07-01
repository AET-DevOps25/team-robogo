from fastapi import APIRouter

router = APIRouter(tags=["General"])

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "LLM Suggestion Service"}

@router.get("/")
async def root():
    return {
        "service": "LLM Suggestion Service",
        "version": "1.0.0",
        "description": "Generates suggestions using LangChain and Open WebUI",
        "endpoints": {
            "health": "/health",
            "suggestion": "/suggestion",
            "docs": "/docs"
        }
    } 