import pytest
from fastapi.testclient import TestClient
from unittest.mock import Mock, patch
from app.main import app

client = TestClient(app)


class TestGeneralRoutes:
    def test_health_check(self):
        """Test health check endpoint"""
        response = client.get("/genai/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"
        assert data["service"] == "LLM Suggestion Service"

    def test_root_endpoint(self):
        """Test root endpoint"""
        response = client.get("/genai/")
        assert response.status_code == 200
        data = response.json()
        assert data["service"] == "LLM Suggestion Service"
        assert data["version"] == "1.0.0"
        assert "endpoints" in data


class TestSuggestionRoutes:
    def test_suggestion_success_openwebui(self):
        """Test successful suggestion with OpenWebUI"""
        mock_llm = Mock()
        mock_llm.invoke.return_value = "Test suggestion response"
        
        with patch('app.routes.suggestion.llm_factory.get_llm', return_value=mock_llm):
            response = client.post(
                "/genai/suggestion",
                json={"text": "Test content", "service": "openwebui"}
            )
            assert response.status_code == 200
            data = response.json()
            assert data["suggestion"] == "Test suggestion response"

    def test_suggestion_success_openai(self):
        """Test successful suggestion with OpenAI"""
        mock_llm = Mock()
        mock_llm.invoke.return_value = "OpenAI suggestion response"
        
        with patch('app.routes.suggestion.llm_factory.get_llm', return_value=mock_llm):
            response = client.post(
                "/genai/suggestion",
                json={"text": "Test content", "service": "openai"}
            )
            assert response.status_code == 200
            data = response.json()
            assert data["suggestion"] == "OpenAI suggestion response"

    def test_suggestion_default_service(self):
        """Test suggestion with default service (openwebui)"""
        mock_llm = Mock()
        mock_llm.invoke.return_value = "Default suggestion response"
        
        with patch('app.routes.suggestion.llm_factory.get_llm', return_value=mock_llm):
            response = client.post(
                "/genai/suggestion",
                json={"text": "Test content"}
            )
            assert response.status_code == 200
            data = response.json()
            assert data["suggestion"] == "Default suggestion response"

    def test_suggestion_empty_text(self):
        """Test suggestion with empty text"""
        response = client.post(
            "/genai/suggestion",
            json={"text": "", "service": "openwebui"}
        )
        assert response.status_code == 422  # Fails Pydantic validation (min_length=1)

    def test_suggestion_whitespace_text(self):
        """Test suggestion with whitespace-only text"""
        response = client.post(
            "/genai/suggestion",
            json={"text": "   ", "service": "openwebui"}
        )
        assert response.status_code == 400
        assert "text cannot be empty" in response.json()["detail"]

    def test_suggestion_missing_text(self):
        """Test suggestion with missing text field"""
        response = client.post(
            "/genai/suggestion",
            json={"service": "openwebui"}
        )
        assert response.status_code == 422  # Validation error

    def test_suggestion_llm_error(self):
        """Test suggestion when LLM service fails"""
        mock_llm = Mock()
        mock_llm.invoke.side_effect = Exception("LLM service error")
        
        with patch('app.routes.suggestion.llm_factory.get_llm', return_value=mock_llm):
            response = client.post(
                "/genai/suggestion",
                json={"text": "Test content", "service": "openwebui"}
            )
            assert response.status_code == 500
            assert "Failed to generate suggestion" in response.json()["detail"]

    def test_suggestion_invalid_service(self):
        """Test suggestion with an invalid service name"""
        response = client.post(
            "/genai/suggestion",
            json={"text": "Test content", "service": "invalid_service_name"}
        )
        assert response.status_code == 422  # Validation Error 