from fastapi import APIRouter, HTTPException
from pydantic import BaseModel, Field
from typing import Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun
import os
import requests
from dotenv import load_dotenv

load_dotenv()
CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = "https://gpu.aet.cit.tum.de/api/chat/completions"

router = APIRouter()

class SuggestionRequest(BaseModel):
    text: str = Field(..., description="The original content for AI analysis and suggestion.")

class SuggestionResponse(BaseModel):
    suggestion: str = Field(..., description="AI-generated suggestion.")

class OpenWebUILLM(LLM):
    api_url: str = API_URL
    api_key: str = CHAIR_API_KEY
    model_name: str = "llama3.3:latest"

    @property
    def _llm_type(self) -> str:
        return "open_webui"

    def _call(
        self,
        prompt: str,
        stop: Optional[list] = None,
        run_manager: Optional[CallbackManagerForLLMRun] = None,
        **kwargs: Any,
    ) -> str:
        if not self.api_key:
            raise ValueError("CHAIR_API_KEY environment variable is required")
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }
        messages = [
            {"role": "user", "content": prompt}
        ]
        payload = {
            "model": self.model_name,
            "messages": messages,
        }
        try:
            response = requests.post(
                self.api_url,
                headers=headers,
                json=payload,
                timeout=30
            )
            response.raise_for_status()
            result = response.json()
            if "choices" in result and len(result["choices"]) > 0:
                content = result["choices"][0]["message"]["content"]
                return content.strip()
            else:
                raise ValueError("Unexpected response format from API")
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")

llm = OpenWebUILLM()

@router.get("/health")
async def health_check():
    return {"status": "healthy", "service": "LLM Suggestion Service"}

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
        suggestion = llm(prompt)
        return SuggestionResponse(suggestion=suggestion)
    except HTTPException:
        raise
    except Exception as e:
        print(f"Error generating suggestion: {str(e)}")
        raise HTTPException(status_code=500, detail=f"Failed to generate suggestion: {str(e)}")

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