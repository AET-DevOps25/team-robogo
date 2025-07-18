# API Endpoints Documentation

## Overview

This document describes all API endpoints of the Team RoboGo project, including authentication, slide management, score management, and screen control functionalities.

## Basic Information

- **Base URL**: `http://localhost:8081`
- **API Version**: v1.0.0
- **Authentication**: JWT Bearer Token
- **Content Type**: `application/json`

## Authentication Endpoints

### User Login
- **Endpoint**: `POST /auth/login`
- **Description**: Validates user credentials and returns JWT token
- **Request Body**:
  ```json
  {
    "username": "admin",
    "password": "admin"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "username": "admin"
    }
  }
  ```

### Validate Session
- **Endpoint**: `GET /auth/session`
- **Description**: Validates JWT token and returns current user session information
- **Headers**: `Authorization: Bearer <token>`
- **Response**:
  ```json
  {
    "valid": true,
    "user": {
      "id": 1,
      "username": "admin"
    }
  }
  ```

### User Logout
- **Endpoint**: `POST /auth/logout`
- **Description**: User logout operation
- **Response**:
  ```json
  {
    "success": true
  }
  ```

## Slide Deck Management

### Get All Slide Decks
- **Endpoint**: `GET /slidedecks`
- **Description**: Returns all slide decks in the system
- **Authentication**: Requires JWT token
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "Main Screen Slides",
      "transitionTime": 5000,
      "version": 1,
      "competitionId": 1,
      "slides": [...],
      "lastUpdate": "2024-01-01T10:00:00.000"
    }
  ]
  ```

### Get Slide Deck by ID
- **Endpoint**: `GET /slidedecks/{deckId}`
- **Description**: Gets detailed information of slide deck by ID
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Response**: Same as above for single slide deck object

### Create Slide Deck
- **Endpoint**: `POST /slidedecks`
- **Description**: Creates a new slide deck
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "name": "New Slide Deck",
    "transitionTime": 3000,
    "competitionId": 1
  }
  ```

### Update Slide Deck
- **Endpoint**: `PUT /slidedecks/{deckId}`
- **Description**: Updates information of specified slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token

### Delete Slide Deck
- **Endpoint**: `DELETE /slidedecks/{deckId}`
- **Description**: Deletes specified slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token

### Add Slide to Deck
- **Endpoint**: `POST /slidedecks/{deckId}/slides`
- **Description**: Adds new slide to specified slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "name": "Image Slide",
    "index": 0,
    "type": "IMAGE",
    "imageMeta": {
      "id": 1,
      "name": "example.jpg",
      "contentType": "image/jpeg"
    }
  }
  ```

### Reorder Slides
- **Endpoint**: `POST /slidedecks/{deckId}/slides/reorder`
- **Description**: Reorders slides in slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  [1, 3, 2, 4]
  ```

### Remove Slide from Deck
- **Endpoint**: `DELETE /slidedecks/{deckId}/slides/{slideId}`
- **Description**: Removes specified slide from slide deck
- **Parameters**: 
  - `deckId` - Slide deck ID
  - `slideId` - Slide ID
- **Authentication**: Requires JWT token

### Get Slide from Deck
- **Endpoint**: `GET /slidedecks/{deckId}/slides/{slideId}`
- **Description**: Gets detailed information of specified slide in deck
- **Parameters**: 
  - `deckId` - Slide deck ID
  - `slideId` - Slide ID
- **Authentication**: Requires JWT token

### Update Slide Deck Speed
- **Endpoint**: `PUT /slidedecks/{deckId}/speed`
- **Description**: Updates slide deck transition time
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "transitionTime": 4000
  }
  ```

## Synchronization Endpoints

### Get Sync Status
- **Endpoint**: `GET /slidedecks/{deckId}/sync`
- **Description**: Gets current synchronization status of slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Response**:
  ```json
  {
    "slideDeck": {...},
    "hasChanges": false
  }
  ```

### Update Sync Status
- **Endpoint**: `POST /slidedecks/{deckId}/sync`
- **Description**: Updates synchronization status of slide deck
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "slideDeckId": 1,
    "version": 2
  }
  ```

### Force Sync Update
- **Endpoint**: `POST /slidedecks/{deckId}/sync/force`
- **Description**: Forces synchronization status update (used when content changes)
- **Parameters**: `deckId` - Slide deck ID
- **Authentication**: Requires JWT token

## Score Management

### Get Scores by Category
- **Endpoint**: `GET /scores/category/{categoryId}`
- **Description**: Gets scores of all teams in specified category, including ranking and highlight information
- **Parameters**: `categoryId` - Category ID
- **Authentication**: Requires JWT token
- **Response**:
  ```json
  [
    {
      "id": 1,
      "points": 100.0,
      "time": 120,
      "team": {
        "id": 1,
        "name": "Team A"
      },
      "highlight": true
    }
  ]
  ```

### Add Score
- **Endpoint**: `POST /scores`
- **Description**: Adds new score record for specified team
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "points": 95.5,
    "time": 180,
    "team": {
      "id": 1
    }
  }
  ```

### Update Score
- **Endpoint**: `PUT /scores/{id}`
- **Description**: Updates specified score record
- **Parameters**: `id` - Score record ID
- **Authentication**: Requires JWT token

### Delete Score
- **Endpoint**: `DELETE /scores/{id}`
- **Description**: Deletes specified score record
- **Parameters**: `id` - Score record ID
- **Authentication**: Requires JWT token

### Create Score Slide
- **Endpoint**: `POST /scores/slide`
- **Description**: Creates new score slide
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "name": "Score Display",
    "index": 0,
    "type": "SCORE",
    "category": {
      "id": 1,
      "name": "Junior Division"
    }
  }
  ```

## Screen Management

### Get All Screens
- **Endpoint**: `GET /screens`
- **Description**: Returns all display screens in the system
- **Authentication**: Requires JWT token
- **Response**:
  ```json
  [
    {
      "id": 1,
      "name": "Main Screen",
      "status": "ACTIVE",
      "slideDeck": {
        "id": 1,
        "name": "Main Screen Slides"
      }
    }
  ]
  ```

### Get Screen by ID
- **Endpoint**: `GET /screens/{id}`
- **Description**: Gets detailed information of specified screen
- **Parameters**: `id` - Screen ID
- **Authentication**: Requires JWT token

### Create Screen
- **Endpoint**: `POST /screens`
- **Description**: Creates new display screen
- **Authentication**: Requires JWT token
- **Request Body**:
  ```json
  {
    "name": "New Screen",
    "status": "ACTIVE"
  }
  ```

### Update Screen
- **Endpoint**: `PUT /screens/{id}`
- **Description**: Updates information of specified screen
- **Parameters**: `id` - Screen ID
- **Authentication**: Requires JWT token

### Delete Screen
- **Endpoint**: `DELETE /screens/{id}`
- **Description**: Deletes specified screen
- **Parameters**: `id` - Screen ID
- **Authentication**: Requires JWT token

### Assign Slide Deck to Screen
- **Endpoint**: `POST /screens/{id}/assign-slide-deck/{slideDeckId}`
- **Description**: Assigns specified slide deck to screen
- **Parameters**: 
  - `id` - Screen ID
  - `slideDeckId` - Slide deck ID
- **Authentication**: Requires JWT token

### Get Screen Content
- **Endpoint**: `GET /screens/{id}/content`
- **Description**: Gets current content of specified screen
- **Parameters**: `id` - Screen ID
- **Authentication**: Requires JWT token

### Update Screen Status
- **Endpoint**: `PUT /screens/{id}/status`
- **Description**: Updates status of specified screen
- **Parameters**: 
  - `id` - Screen ID
  - `status` - Screen status (ACTIVE, INACTIVE, ERROR)
- **Authentication**: Requires JWT token

## Category Management

### Get All Categories
- **Endpoint**: `GET /categories`
- **Description**: Returns all categories in the system
- **Authentication**: Requires JWT token

### Get Category by ID
- **Endpoint**: `GET /categories/{id}`
- **Description**: Gets detailed information of specified category
- **Parameters**: `id` - Category ID
- **Authentication**: Requires JWT token

### Create Category
- **Endpoint**: `POST /categories`
- **Description**: Creates new category
- **Authentication**: Requires JWT token

### Update Category
- **Endpoint**: `PUT /categories/{id}`
- **Description**: Updates information of specified category
- **Parameters**: `id` - Category ID
- **Authentication**: Requires JWT token

### Delete Category
- **Endpoint**: `DELETE /categories/{id}`
- **Description**: Deletes specified category
- **Parameters**: `id` - Category ID
- **Authentication**: Requires JWT token

## Team Management

### Get All Teams
- **Endpoint**: `GET /teams`
- **Description**: Returns all teams in the system
- **Authentication**: Requires JWT token

### Get Team by ID
- **Endpoint**: `GET /teams/{id}`
- **Description**: Gets detailed information of specified team
- **Parameters**: `id` - Team ID
- **Authentication**: Requires JWT token

### Create Team
- **Endpoint**: `POST /teams`
- **Description**: Creates new team
- **Authentication**: Requires JWT token

### Update Team
- **Endpoint**: `PUT /teams/{id}`
- **Description**: Updates information of specified team
- **Parameters**: `id` - Team ID
- **Authentication**: Requires JWT token

### Delete Team
- **Endpoint**: `DELETE /teams/{id}`
- **Description**: Deletes specified team
- **Parameters**: `id` - Team ID
- **Authentication**: Requires JWT token

## Image Management

### Upload Image
- **Endpoint**: `POST /slideimages`
- **Description**: Uploads new image file
- **Authentication**: Requires JWT token
- **Content Type**: `multipart/form-data`

### Get Image
- **Endpoint**: `GET /slideimages/{id}`
- **Description**: Gets content of specified image
- **Parameters**: `id` - Image ID
- **Authentication**: Requires JWT token

### Get Image Metadata
- **Endpoint**: `GET /slideimages/{id}/meta`
- **Description**: Gets metadata information of specified image
- **Parameters**: `id` - Image ID
- **Authentication**: Requires JWT token

### Delete Image
- **Endpoint**: `DELETE /slideimages/{id}`
- **Description**: Deletes specified image
- **Parameters**: `id` - Image ID
- **Authentication**: Requires JWT token

## Error Responses

All API endpoints may return the following error responses:

### 400 Bad Request
```json
{
  "error": "Bad Request",
  "message": "Detailed error message"
}
```

### 401 Unauthorized
```json
{
  "error": "Authentication Failed",
  "message": "Invalid JWT token"
}
```

### 404 Not Found
```json
{
  "error": "Resource Not Found",
  "message": "Requested resource not found"
}
```

### 500 Internal Server Error
```json
{
  "error": "Internal Server Error",
  "message": "Server error occurred while processing request"
}
```

## Usage Examples

### 1. User Login and Get Token
```bash
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin"}'
```

### 2. Access Protected Resources with Token
```bash
curl -X GET http://localhost:8081/slidedecks \
  -H "Authorization: Bearer <your-jwt-token>"
```

### 3. Create New Slide Deck
```bash
curl -X POST http://localhost:8081/slidedecks \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Main Screen Slides",
    "transitionTime": 5000,
    "competitionId": 1
  }'
```

### 4. Add Score Record
```bash
curl -X POST http://localhost:8081/scores \
  -H "Authorization: Bearer <your-jwt-token>" \
  -H "Content-Type: application/json" \
  -d '{
    "points": 95.5,
    "time": 180,
    "team": {"id": 1}
  }'
```

## Important Notes

1. **Authentication**: All endpoints except login and session validation require valid JWT tokens
2. **Version Control**: Slide decks use version numbers for synchronization, clients should check version changes
3. **File Upload**: Image upload supports files up to 100MB
4. **Concurrency Control**: System supports multi-client concurrent access through version control mechanism
5. **Error Handling**: All errors return standardized error response format 