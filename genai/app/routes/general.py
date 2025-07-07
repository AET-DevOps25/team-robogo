from fastapi import APIRouter
from app.services.cache_service import cache_service

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
            "cache": "/cache",
            "cache/clear": "/cache/clear",
            "docs": "/docs"
        }
    }

@router.get("/cache/stats")
async def get_cache_status():
    """Get cache connection status and statistics"""
    try:
        stats = await cache_service.get_cache_stats()
        return {
            "cache_service": "Redis",
            "status": "connected" if stats.get("connected", False) else "disconnected", 
            "stats": stats
        }
    except Exception as e:
        return {
            "cache_service": "Redis",
            "status": "error",
            "error": str(e),
            "stats": {}
        }

@router.delete("/cache/clear")
async def clear_cache():
    """Clear all cache entries"""
    try:
        success = await cache_service.clear_cache()
        if success:
            return {
                "message": "Cache cleared successfully",
                "success": True,
                "cache_service": "Redis"
            }
        else:
            return {
                "message": "Failed to clear cache",
                "success": False,
                "cache_service": "Redis"
            }
    except Exception as e:
        return {
            "message": f"Error clearing cache: {str(e)}",
            "success": False,
            "cache_service": "Redis",
            "error": str(e)
        }