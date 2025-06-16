import os
import requests
from typing import Any, Optional
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel, Field
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun

load_dotenv()
# Environment configuration
CHAIR_API_KEY = os.getenv("CHAIR_API_KEY")
API_URL = "https://gpu.aet.cit.tum.de/api/chat/completions"

# Create FastAPI application instance
app = FastAPI(
    title="LLM Suggestion Service",
    description="Service that generates suggestions using an LLM",
    version="1.0.0"
)


class SuggestionRequest(BaseModel):
    text: str = Field(..., description="The original content for AI analysis and suggestion.")


class SuggestionResponse(BaseModel):
    suggestion: str = Field(..., description="AI-generated suggestion.")


class OpenWebUILLM(LLM):
    """
    Custom LangChain LLM wrapper for Open WebUI API.
    
    This class integrates the Open WebUI API with LangChain's LLM interface,
    allowing us to use the API in LangChain chains and pipelines.
    """
    
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
        """
        Call the Open WebUI API to generate a response.
        
        Args:
            prompt: The input prompt to send to the model
            stop: Optional list of stop sequences
            run_manager: Optional callback manager for LangChain
            **kwargs: Additional keyword arguments
            
        Returns:
            The generated response text
            
        Raises:
            Exception: If API call fails
        """
        if not self.api_key:
            raise ValueError("CHAIR_API_KEY environment variable is required")
        
        headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
        }
        
        # Build messages for chat completion
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
            
            # Extract the response content
            if "choices" in result and len(result["choices"]) > 0:
                content = result["choices"][0]["message"]["content"]
                return content.strip()
            else:
                raise ValueError("Unexpected response format from API")
                
        except requests.RequestException as e:
            raise Exception(f"API request failed: {str(e)}")
        except (KeyError, IndexError, ValueError) as e:
            raise Exception(f"Failed to parse API response: {str(e)}")


# Initialize the LLM
llm = OpenWebUILLM()

@app.get("/health")
async def health_check():
    """Health check endpoint."""
    return {"status": "healthy", "service": "LLM Suggestion Service"}


@app.post("/suggestion", response_model=SuggestionResponse)
async def suggestion(req: SuggestionRequest) -> SuggestionResponse:
    """
    Generate a suggestion using LangChain and Ollama.
    
    Args:
        req: Request containing the text for suggestion
    
    Returns:
        SuggestionResponse containing the suggestion
    
    Raises:
        HTTPException: If the API call fails or other errors occur
    """
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


@app.get("/")
async def root():
    """Root endpoint with service information."""
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

# Entry point for direct execution
if __name__ == "__main__":
    """
    Entry point for `python main.py` invocation.
    Starts Uvicorn server serving this FastAPI app.

    Honors PORT environment variable (default: 5000).
    Reload=True enables live-reload during development.
    """
    import uvicorn

    port = int(os.getenv("PORT", 5000))
    
    print(f"Starting LLM Suggestion Service on port {port}")
    print(f"API Documentation available at: http://localhost:{port}/docs")
    
    uvicorn.run(
        "main:app",
        host="0.0.0.0",
        port=port,
        reload=True
    )
