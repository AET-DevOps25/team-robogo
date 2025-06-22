from typing import Any, Optional
from langchain.llms.base import LLM
from langchain.callbacks.manager import CallbackManagerForLLMRun
import requests

from app.core.config import API_URL, CHAIR_API_KEY, MODEL_NAME

class OpenWebUILLM(LLM):
    api_url: str = API_URL
    api_key: str = CHAIR_API_KEY
    model_name: str = MODEL_NAME

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