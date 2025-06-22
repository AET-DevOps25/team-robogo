# GenAI Suggestion Service

A FastAPI-based microservice that provides content suggestions using various Large Language Model (LLM) backends, including OpenWebUI and OpenAI.

## Features

- **FastAPI Backend**: High-performance, easy-to-use Python web framework.
- **Multiple LLM Support**: Easily switch between different LLM providers like OpenAI and OpenWebUI.
- **Dynamic LLM Factory**: A factory pattern allows for easy extension to support more LLM services in the future.
- **Prometheus Monitoring**: Exposes a `/metrics` endpoint for easy integration with Prometheus and Grafana.
- **Clean Architecture**: Follows best practices with a clear separation of concerns (routes, services, models).
- **Comprehensive Unit Tests**: A full test suite using `pytest` to ensure code quality and reliability.
- **Environment-based Configuration**: Securely manage credentials and settings using a `.env` file.

## Project Structure

The project follows a modular structure for better maintainability:

```
genai/
├── app/
│   ├── core/
│   │   └── config.py         # Environment variables and configuration
│   ├── models/
│   │   └── schemas.py        # Pydantic data models (request/response)
│   ├── routes/
│   │   ├── general.py        # General routes (/health, /)
│   │   └── suggestion.py     # Suggestion-related routes
│   └── services/
│   │   └── llm_service.py      # LLM Factory and service integrations
│   └── main.py               # FastAPI app instantiation and router inclusion
├── tests/
│   ├── test_models.py
│   ├── test_routes.py
│   └── test_services.py
├── .env.example              # Example environment file
├── Dockerfile
├── pytest.ini                # Pytest configuration
├── README.md                 # This file
├── requirements.txt          # Python dependencies
└── run_tests.py              # Test runner script
```

## Setup and Installation

1.  **Clone the repository** (if not already done).

2.  **Navigate to the `genai` directory**:
    ```bash
    cd genai
    ```

3.  **Create a virtual environment** (recommended):
    ```bash
    python -m venv venv
    source venv/bin/activate  # On Windows, use `venv\Scripts\activate`
    ```

4.  **Install dependencies**:
    ```bash
    pip install -r requirements.txt
    ```

5.  **Configure Environment Variables**:
    Create a `.env` file in the `genai` directory by copying the example:
    ```bash
    cp .env.example .env
    ```
    Now, edit the `.env` file and add your API keys:
    ```
    # For OpenWebUI (if needed)
    CHAIR_API_KEY="your-openwebui-api-key"
    API_URL="https://your.openwebui.instance/api/chat/completions"
    MODEL_NAME="your-model-name"

    # For OpenAI
    OPENAI_API_KEY="your-openai-api-key"
    ```

## Running the Application

To run the service locally, use `uvicorn`:

```bash
uvicorn app.main:app --host 0.0.0.0 --port 5000 --reload
```
The `--reload` flag enables auto-reloading when code changes are detected, which is useful for development.

## API Endpoints

All endpoints are prefixed with `/genai`.

### Health Check

- **Endpoint**: `GET /genai/health`
- **Description**: Checks if the service is running.
- **Response**:
  ```json
  {
    "status": "healthy",
    "service": "LLM Suggestion Service"
  }
  ```

### Get Suggestion

- **Endpoint**: `POST /genai/suggestion`
- **Description**: Generates a suggestion for the given text.
- **Request Body**:
  ```json
  {
    "text": "Your content to be analyzed.",
    "service": "openai"
  }
  ```
  - `text` (string, required): The content to analyze.
  - `service` (string, optional): The LLM service to use. Can be `"openai"` or `"openwebui"`. Defaults to `"openwebui"`.
- **Success Response**:
  ```json
  {
    "suggestion": "This is an AI-generated suggestion."
  }
  ```

## Monitoring

The service exposes a `/metrics` endpoint for Prometheus to scrape performance and usage data.

- **Metrics Endpoint**: `GET /metrics`

## Running Tests

To ensure everything is working correctly, run the comprehensive test suite:

```bash
# Make sure you are in the genai/ directory
# Install test dependencies if you haven't already
pip install -r requirements.txt

# Run all tests
python run_tests.py
```
For more details, see the README in the `tests/` directory. 