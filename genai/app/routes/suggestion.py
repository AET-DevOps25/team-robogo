from fastapi import APIRouter, HTTPException
from app.models.schemas import SuggestionRequest, SuggestionResponse
from app.services.llm_service import llm

router = APIRouter(tags=["Suggestion"])

@router.post("/suggestion", response_model=SuggestionResponse)
async def suggestion(req: SuggestionRequest) -> SuggestionResponse:
    try:
        if not req.text or not req.text.strip():
            raise HTTPException(status_code=400, detail="text cannot be empty")
        prompt = (
            "Please analyze the following content, identify any potentially missing elements, "
            "and provide suggestions for improvement or completion.\n"
            f"{req.text}\n"
            "Please list your suggestions in concise English."
        )
        suggestion_text = llm.invoke(prompt)
        return SuggestionResponse(suggestion=suggestion_text)
    except HTTPException:
        raise
    except Exception as e:
        print(f"Error generating suggestion: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to generate suggestion: {str(e)}") 