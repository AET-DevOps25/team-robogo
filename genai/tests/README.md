# GenAI Module Tests

This directory contains the unit tests for the `genai` module.

## Test Coverage

### 1. Data Model Tests (`test_models.py`)
- `SuggestionRequest` model validation
- `SuggestionResponse` model validation
- Edge case testing (empty values, invalid values, etc.)

### 2. Service Layer Tests (`test_services.py`)
- `LLMFactory` class testing
- `OpenWebUILLM` class testing
- API call success and failure scenarios
- Error handling tests

### 3. Route Tests (`test_routes.py`)
- General routes testing (health check, root)
- Suggestion endpoint testing
- Testing support for different LLM services
- Error handling and validation tests

## Running Tests

### Method 1: Using pytest directly
```bash
cd genai
pytest tests/ -v
```

### Method 2: Using the test runner script
```bash
cd genai
python run_tests.py
```

### Method 3: Running a specific test file
```bash
cd genai
pytest tests/test_models.py -v
pytest tests/test_services.py -v
pytest tests/test_routes.py -v
```

### Method 4: Running a specific test class
```bash
cd genai
pytest tests/test_models.py::TestSuggestionRequest -v
```

## Test Configuration

Tests are configured using the `pytest.ini` file, which includes:
- Test path settings
- Output format configuration
- Marker definitions

## Notes

1.  Tests use mock objects to simulate external dependencies (e.g., API calls).
2.  No real API keys are required to run the tests.
3.  All tests are independent and can be run in parallel.
4.  Tests cover major success and failure scenarios. 