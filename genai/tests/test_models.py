import pytest
from pydantic import ValidationError
from app.models.schemas import SuggestionRequest, SuggestionResponse


class TestSuggestionRequest:
    def test_valid_request(self):
        """Test creating a valid SuggestionRequest"""
        request = SuggestionRequest(text="Test content")
        assert request.text == "Test content"
        assert request.service == "openwebui"  # default value

    def test_valid_request_with_service(self):
        """Test creating a SuggestionRequest with explicit service"""
        request = SuggestionRequest(text="Test content", service="openai")
        assert request.text == "Test content"
        assert request.service == "openai"

    def test_empty_text_raises_error(self):
        """Test that empty text raises ValidationError"""
        with pytest.raises(ValidationError):
            SuggestionRequest(text="")

    def test_none_text_raises_error(self):
        """Test that None text raises ValidationError"""
        with pytest.raises(ValidationError):
            SuggestionRequest(text=None)

    def test_invalid_service_raises_error(self):
        """Test that invalid service raises ValidationError"""
        with pytest.raises(ValidationError):
            SuggestionRequest(text="Test content", service="invalid_service")


class TestSuggestionResponse:
    def test_valid_response(self):
        """Test creating a valid SuggestionResponse"""
        response = SuggestionResponse(suggestion="Test suggestion")
        assert response.suggestion == "Test suggestion"

    def test_empty_suggestion(self):
        """Test creating a SuggestionResponse with empty suggestion"""
        response = SuggestionResponse(suggestion="")
        assert response.suggestion == ""

    def test_none_suggestion_raises_error(self):
        """Test that None suggestion raises ValidationError"""
        with pytest.raises(ValidationError):
            SuggestionResponse(suggestion=None) 