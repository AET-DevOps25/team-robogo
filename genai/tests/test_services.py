import pytest
from unittest.mock import Mock, patch
import requests  # Import requests library
import os
from app.services.llm_service import LLMFactory, OpenWebUILLM
from app.core.config import OPENAI_API_KEY, CHAIR_API_KEY


class TestLLMFactory:
    def test_get_llm_openwebui(self):
        """Test getting OpenWebUI LLM instance"""
        with patch('app.services.llm_service.CHAIR_API_KEY', 'test_key'):
            llm = LLMFactory.get_llm("openwebui")
            assert isinstance(llm, OpenWebUILLM)

    def test_get_llm_openai(self):
        """Test getting OpenAI LLM instance"""
        with patch.dict('os.environ', {'OPENAI_API_KEY': 'test_key'}):
            llm = LLMFactory.get_llm("openai")
            from langchain_openai import ChatOpenAI
            assert isinstance(llm, ChatOpenAI)

    def test_get_llm_invalid_service(self):
        """Test getting LLM with invalid service name"""
        with pytest.raises(ValueError, match="Unsupported LLM service"):
            LLMFactory.get_llm("invalid_service")  # type: ignore

    def test_get_llm_openai_missing_key(self):
        """Test getting OpenAI LLM without API key"""
        with patch('app.services.llm_service.OPENAI_API_KEY', None):
            with pytest.raises(ValueError, match="OPENAI_API_KEY environment variable is required"):
                LLMFactory.get_llm("openai")


class TestOpenWebUILLM:
    def test_llm_type(self):
        """Test LLM type property"""
        llm = OpenWebUILLM()
        assert llm._llm_type == "open_webui"

    def test_call_success(self):
        """Test successful API call"""
        mock_response = Mock()
        mock_response.json.return_value = {
            "choices": [{"message": {"content": "Test response"}}]
        }
        mock_response.raise_for_status.return_value = None

        with patch('requests.post', return_value=mock_response), \
             patch('app.services.llm_service.CHAIR_API_KEY', 'test_key'):
            llm = OpenWebUILLM()
            result = llm._call("Test prompt")
            assert result == "Test response"

    def test_call_api_error(self):
        """Test API call with error response"""
        mock_response = Mock()
        mock_response.raise_for_status.side_effect = requests.RequestException("API Error")

        with patch('requests.post', return_value=mock_response), \
             patch('app.services.llm_service.CHAIR_API_KEY', 'test_key'):
            llm = OpenWebUILLM()
            with pytest.raises(Exception, match="OpenWebUI API request failed"):
                llm._call("Test prompt")

    def test_call_invalid_response_format(self):
        """Test API call with invalid response format"""
        mock_response = Mock()
        mock_response.json.return_value = {"invalid": "format"}
        mock_response.raise_for_status.return_value = None

        with patch('requests.post', return_value=mock_response), \
             patch('app.services.llm_service.CHAIR_API_KEY', 'test_key'):
            llm = OpenWebUILLM()
            with pytest.raises(Exception, match="Failed to parse OpenWebUI API response"):
                llm._call("Test prompt")

    def test_call_missing_api_key(self):
        """Test API call without API key"""
        with patch('app.services.llm_service.CHAIR_API_KEY', None):
            llm = OpenWebUILLM()
            with pytest.raises(ValueError, match="CHAIR_API_KEY environment variable is required for OpenWebUI"):
                llm._call("Test prompt") 