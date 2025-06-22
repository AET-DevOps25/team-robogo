from pydantic import BaseModel, Field

class SuggestionRequest(BaseModel):
    text: str = Field(..., description="The original content for AI analysis and suggestion.")

class SuggestionResponse(BaseModel):
    suggestion: str = Field(..., description="AI-generated suggestion.") 