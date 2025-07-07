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

        # First check cache
        cached_suggestion = await cache_service.get_cached_suggestion(req.text, req.service)
        if cached_suggestion:
            logger.info(f"Returning cached suggestion for service: {req.service}")
            return SuggestionResponse(suggestion=str(cached_suggestion))

        # Cache miss, call LLM service
        logger.info(f"Cache miss, calling LLM service: {req.service}")
        llm = llm_factory.get_llm(req.service)

        prompt = (
            "Please analyze the following content, identify any potentially missing elements, "
            "and provide suggestions for improvement or completion.\n"
            f"{req.text}\n"
            "Please list your suggestions in concise English."
        )
        response = llm.invoke(prompt)
        # Handle different types of responses
        try:
            # Try to get content attribute (for AIMessage)
            suggestion_text = str(response.content)  # type: ignore
        except AttributeError:
            # If no content attribute, convert directly to string
            suggestion_text = str(response)
        
        # Cache the result
        await cache_service.cache_suggestion(req.text, req.service, suggestion_text)
        logger.info(f"Cached new suggestion for service: {req.service}")
        
        return SuggestionResponse(suggestion=suggestion_text)
    except (ValueError, HTTPException):
        raise
    except Exception as e:
        logger.error(f"Error generating suggestion: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to generate suggestion: {str(e)}")