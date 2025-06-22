from pydantic import BaseModel, Field
from typing import Literal

class SuggestionRequest(BaseModel):
    text: str = Field(..., description="The original content for AI analysis and suggestion.")
    service: Literal["openwebui", "openai"] = Field(
        "openwebui", 
        description="The LLM service to use for the suggestion."
    )

class SuggestionResponse(BaseModel):
    suggestion: str = Field(..., description="AI-generated suggestion.") 