from fastapi import APIRouter, HTTPException
from app.models.schemas import SuggestionRequest, SuggestionResponse
from app.services.llm_service import llm_factory
from app.services.cache_service import cache_service
import logging

logger = logging.getLogger(__name__)
router = APIRouter(tags=["Suggestion"])

@router.post("/suggestion", response_model=SuggestionResponse)
async def suggestion(req: SuggestionRequest) -> SuggestionResponse:
    try:
        if not req.text or not req.text.strip():
            raise HTTPException(status_code=400, detail="text cannot be empty")

        # 首先检查缓存
        cached_suggestion = await cache_service.get_cached_suggestion(req.text, req.service)
        if cached_suggestion:
            logger.info(f"返回缓存的建议，服务: {req.service}")
            return SuggestionResponse(suggestion=cached_suggestion)

        # 缓存未命中，调用LLM服务
        logger.info(f"缓存未命中，调用LLM服务: {req.service}")
        llm = llm_factory.get_llm(req.service)

        prompt = (
            "Please analyze the following content, identify any potentially missing elements, "
            "and provide suggestions for improvement or completion.\n"
            f"{req.text}\n"
            "Please list your suggestions in concise English."
        )
        suggestion_text = str(llm.invoke(prompt))
        
        # 缓存结果
        await cache_service.cache_suggestion(req.text, req.service, suggestion_text)
        logger.info(f"已缓存新的建议，服务: {req.service}")
        
        return SuggestionResponse(suggestion=suggestion_text)
    except (ValueError, HTTPException):
        raise
    except Exception as e:
        logger.error(f"生成建议时出错: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to generate suggestion: {str(e)}")

@router.get("/cache/stats")
async def get_cache_stats():
    """获取缓存统计信息"""
    try:
        stats = await cache_service.get_cache_stats()
        return stats
    except Exception as e:
        logger.error(f"获取缓存统计信息时出错: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to get cache stats: {str(e)}")

@router.delete("/cache/clear")
async def clear_cache():
    """清除所有缓存"""
    try:
        success = await cache_service.clear_cache()
        if success:
            return {"message": "缓存已清除", "success": True}
        else:
            return {"message": "清除缓存失败", "success": False}
    except Exception as e:
        logger.error(f"清除缓存时出错: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to clear cache: {str(e)}") 