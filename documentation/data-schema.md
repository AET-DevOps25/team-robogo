# Data Schema Documentation

## Overview

This document describes the data architecture of the Team RoboGo project, including database entity models, API data transfer objects (DTOs), and their relationships.

## Database Entity Models

### Core Entities

#### 1. Competition
- **Table**: `competition`
- **Description**: Represents a competition event
- **Fields**:
  - `id` (Long): Primary key
  - `internal_id` (UUID): Internal unique identifier
  - `name` (String): Competition name
- **Relationships**:
  - One-to-Many: `SlideDeck` (slideDecks)
  - One-to-Many: `Category` (categories)

#### 2. SlideDeck
- **Table**: `slide_deck`
- **Description**: Represents a group of slides for display on screens
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Slide deck name
  - `transition_time` (int): Slide transition time (milliseconds)
  - `version` (int): Version number for synchronization
  - `last_update` (LocalDateTime): Last update time for multi-screen synchronization
  - `competition_id` (Long): Foreign key, references competition
- **Relationships**:
  - Many-to-One: `Competition` (competition)
  - One-to-Many: `Slide` (slides)

#### 3. Slide (Abstract Base Class)
- **Table**: `slide`
- **Description**: Abstract base class for slides, using single table inheritance strategy
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Slide name
  - `index` (Integer): Index position in slide deck
  - `slidedeck_id` (Long): Foreign key, references slide deck
  - `type` (String): Discriminator field, identifies slide type
- **Inheritance Types**:
  - `IMAGE`: ImageSlide
  - `SCORE`: ScoreSlide

#### 4. ImageSlide
- **Inherits from**: `Slide`
- **Description**: Slide for displaying images
- **Fields**:
  - `image_id` (Long): Foreign key, references image metadata
- **Relationships**:
  - Many-to-One: `SlideImageMeta` (imageMeta)

#### 5. ScoreSlide
- **Inherits from**: `Slide`
- **Description**: Slide for displaying team scores
- **Fields**:
  - `category_id` (Long): Foreign key, references category
- **Relationships**:
  - Many-to-One: `Category` (category)

#### 6. Category
- **Table**: `category`
- **Description**: Team categories
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Category name
  - `competition_id` (Long): Foreign key, references competition
- **Relationships**:
  - Many-to-One: `Competition` (competition)
  - One-to-Many: `Team` (teams)

#### 7. Team
- **Table**: `team`
- **Description**: Participating teams
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Team name
  - `category_id` (Long): Foreign key, references category
- **Relationships**:
  - Many-to-One: `Category` (category)
  - One-to-One: `Score` (score)

#### 8. Score
- **Table**: `score`
- **Description**: Team score records
- **Fields**:
  - `id` (Long): Primary key
  - `points` (double): Score points
  - `time` (int): Completion time (seconds)
  - `team_id` (Long): Foreign key, references team
- **Relationships**:
  - One-to-One: `Team` (team)

#### 9. Screen
- **Table**: `screen`
- **Description**: Display screens
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Screen name
  - `status` (ScreenStatus): Screen status enum
  - `slide_deck_id` (Long): Foreign key, references slide deck
- **Relationships**:
  - Many-to-One: `SlideDeck` (slideDeck)

#### 10. SlideImageMeta
- **Table**: `slide_image_meta`
- **Description**: Image file metadata information
- **Fields**:
  - `id` (Long): Primary key
  - `name` (String): Image name
  - `content_type` (String): Content type (MIME)
- **Relationships**:
  - One-to-One: `SlideImageContent` (content)

#### 11. SlideImageContent
- **Table**: `slide_image_content`
- **Description**: Binary content of image files
- **Fields**:
  - `id` (Long): Primary key
  - `content` (byte[]): Image binary data
  - `image_id` (Long): Foreign key, references image metadata
- **Relationships**:
  - One-to-One: `SlideImageMeta` (meta)

#### 12. User
- **Table**: `users`
- **Description**: System users
- **Fields**:
  - `id` (Long): Primary key
  - `username` (String): Username (unique)
  - `password` (String): Password

### Enum Types

#### SlideType
- `IMAGE`: Image slides
- `SCORE`: Score slides

#### ScreenStatus
- Screen status enum (specific values need to be checked in code)

## API Data Transfer Objects (DTOs)

### Core DTOs

#### 1. SlideDeckDTO
```java
{
  "id": Long,
  "name": String,
  "transitionTime": int,
  "version": int,
  "competitionId": Long,
  "slides": List<SlideDTO>,
  "lastUpdate": LocalDateTime
}
```

#### 2. SlideDTO (Polymorphic)
```java
{
  "id": Long,
  "name": String,
  "index": int,
  "type": String // "IMAGE" or "SCORE"
}
```

#### 3. ImageSlideDTO
```java
{
  "id": Long,
  "name": String,
  "index": int,
  "type": "IMAGE",
  "imageMeta": ImageSlideMetaDTO
}
```

#### 4. ScoreSlideDTO
```java
{
  "id": Long,
  "name": String,
  "index": int,
  "type": "SCORE",
  "category": CategoryDTO
}
```

#### 5. CategoryDTO
```java
{
  "id": Long,
  "name": String
}
```

#### 6. TeamDTO
```java
{
  "id": Long,
  "name": String
}
```

#### 7. ScoreDTO
```java
{
  "id": Long,
  "points": double,
  "time": int
}
```

#### 8. ImageSlideMetaDTO
```java
{
  "id": Long,
  "name": String,
  "contentType": String
}
```

#### 9. ScreenContentDTO
```java
{
  "id": Long,
  "name": String
}
```

### Authentication Related DTOs

#### 10. LoginRequestDTO
```java
{
  "username": String,
  "password": String
}
```

#### 11. LoginResponseDTO
```java
{
  "token": String,
  "user": UserDTO
}
```

#### 12. UserDTO
```java
{
  "id": Long,
  "username": String
}
```

#### 13. SessionResponseDTO
```java
{
  "valid": boolean,
  "user": UserDTO
}
```

### Synchronization Related DTOs

#### 14. SlideDeckSyncRequestDTO
```java
{
  "slideDeckId": Long,
  "version": int
}
```

#### 15. SlideDeckSyncDTO
```java
{
  "slideDeck": SlideDeckDTO,
  "hasChanges": boolean
}
```

## Data Relationship Diagram

The following diagram shows the complete data model relationships:

![Data Schema Diagram](diagram.drawio.svg)

### Text Representation

```
Competition (1) ──── (N) SlideDeck (1) ──── (N) Slide
     │                                              │
     │                                              │
     │ (1) ──── (N) Category (1) ──── (N) Team (1) ──── (1) Score
     │
     └─── (1) ──── (N) Screen (N) ──── (1) SlideDeck

SlideImageMeta (1) ──── (1) SlideImageContent
     │
     └─── (1) ──── (N) ImageSlide
```

## Key Design Features

### 1. Inheritance Strategy
- `Slide` uses single table inheritance strategy, distinguished by `type` field
- Supports `IMAGE` and `SCORE` slide types

### 2. Synchronization Mechanism
- `SlideDeck` uses `version` field for version control
- `lastUpdate` field for multi-screen synchronization
- Clients can determine if reload is needed by comparing versions

**Real-time Synchronization Design:**
The version control system enables real-time synchronization across all frontend screens:

1. **Polling Mechanism**: Frontend screens periodically poll the backend with their current version
2. **Change Detection**: Backend compares client version with server version
3. **Efficient Updates**: Only sends data when changes are detected, reducing network traffic
4. **Multi-screen Sync**: All screens automatically stay synchronized without manual intervention
5. **Conflict Prevention**: Optimistic locking prevents concurrent update conflicts

**Sync Flow:**
```
Frontend → Poll with version → Backend → Compare versions → Return changes if needed → Update all screens
```

This design ensures that all display screens show the same content simultaneously while minimizing resource usage.

### 3. Image Storage
- Image metadata and content are stored separately
- `SlideImageMeta` stores metadata information
- `SlideImageContent` stores binary content
- Supports different content types (MIME)

### 4. Scoring System
- Each team corresponds to one score record
- Scores include points and completion time
- Supports sorting by score and time

### 5. Polymorphic JSON Serialization
- Uses Jackson's polymorphic serialization feature
- `SlideDTO` automatically selects correct subtype based on `type` field

## Database Constraints

### Unique Constraints
- `slide_deck_id` + `index`: Ensures unique slide index within deck
- `team_id`: Ensures each team has only one score record
- `username`: Ensures unique usernames
- `internal_id`: Ensures unique competition internal ID

### Foreign Key Constraints
- All association fields have corresponding foreign key constraints
- Cascade delete configuration ensures data consistency

## API Design Principles

1. **RESTful Design**: Follows REST API design principles
2. **Version Control**: Implements optimistic locking through version fields
3. **Synchronization Mechanism**: Supports real-time data synchronization
4. **Polymorphic Support**: Supports different slide types
5. **Authentication & Authorization**: Complete user authentication system

### Real-time Synchronization Strategy

The system implements a sophisticated real-time synchronization mechanism:

**Frontend Polling Strategy:**
- Screens periodically poll the backend (typically every 5-10 seconds)
- Each poll includes the current slide deck version
- Backend responds with changes only when version differs
- All connected screens receive updates simultaneously

**Benefits:**
- **Efficiency**: Minimal network traffic by only transferring changed data
- **Reliability**: Automatic conflict resolution through version control
- **Scalability**: Supports multiple screens without performance degradation
- **User Experience**: Seamless real-time updates across all displays

**Implementation Details:**
- Version increments on every slide deck modification
- Timestamp tracking for change detection
- Optimistic locking prevents concurrent modification conflicts
- Graceful handling of network interruptions

## Extensibility Considerations

1. **Slide Types**: New slide types can be easily added
2. **Scoring System**: Can be extended with more complex scoring rules
3. **Media Support**: Can be extended to support video, audio, and other media types
4. **Internationalization**: Supports internationalization extensions
5. **Permission System**: Can be extended with more granular permission controls 