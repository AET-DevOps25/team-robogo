# Analysis Object Model

## Overview

The Analysis Object Model represents the core business objects and their relationships in the Team RoboGo system. This model focuses on the domain concepts and business rules that drive the system's functionality.

## Object Model Diagram

```mermaid
classDiagram
    class Competition {
        +id: Long
        +internalId: UUID
        +name: String
        +startDate: LocalDateTime
        +endDate: LocalDateTime
        +status: CompetitionStatus
        +createSlideDeck(name, transitionTime): SlideDeck
        +addCategory(name, scoring): Category
        +getActiveSlideDecks(): List~SlideDeck~
        +getAllCategories(): List~Category~
    }
    
    class SlideDeck {
        +id: Long
        +name: String
        +transitionTime: int
        +version: int
        +lastUpdate: LocalDateTime
        +competition: Competition
        +slides: List~Slide~
        +screens: List~Screen~
        +addSlide(slide): void
        +removeSlide(slide): void
        +reorderSlides(): void
        +incrementVersion(): void
        +isOutdated(clientVersion): boolean
        +getNextSlide(currentSlide): Slide
    }
    
    class Slide {
        <<abstract>>
        +id: Long
        +name: String
        +index: int
        +slideDeck: SlideDeck
        +type: SlideType
        +getType(): SlideType
        +getDisplayContent(): Object
        +validateContent(): boolean
    }
    
    class ImageSlide {
        +imageMeta: SlideImageMeta
        +getImageContent(): byte[]
        +getImageMetadata(): SlideImageMeta
        +validateImageFormat(): boolean
    }
    
    class ScoreSlide {
        +category: Category
        +scores: List~Score~
        +getSortedScores(): List~Score~
        +getTopScores(limit): List~Score~
        +calculateRankings(): Map~Team, int~
        +updateScore(team, points, time): void
    }
    
    class Category {
        +id: Long
        +name: String
        +competition: Competition
        +teams: List~Team~
        +scoring: CategoryScoring
        +addTeam(team): void
        +removeTeam(team): void
        +getTeamRankings(): List~Team~
        +validateTeamAssignment(team): boolean
    }
    
    class Team {
        +id: Long
        +name: String
        +category: Category
        +score: Score
        +getScore(): Score
        +updateScore(points, time): void
        +getRanking(): int
        +isInCategory(category): boolean
    }
    
    class Score {
        +id: Long
        +points: double
        +time: int
        +team: Team
        +timestamp: LocalDateTime
        +compareToWithTime(other): int
        +add(other): Score
        +isValid(): boolean
        +getFormattedTime(): String
    }
    
    class Screen {
        +id: Long
        +name: String
        +status: ScreenStatus
        +slideDeck: SlideDeck
        +currentSlide: Slide
        +lastSyncTime: LocalDateTime
        +register(): void
        +assignSlideDeck(slideDeck): void
        +updateStatus(status): void
        +pollForUpdates(): SyncResponse
        +displaySlide(slide): void
        +reportError(error): void
    }
    
    class SlideImageMeta {
        +id: Long
        +name: String
        +contentType: String
        +fileSize: long
        +uploadDate: LocalDateTime
        +content: SlideImageContent
        +getContent(): byte[]
        +getFileExtension(): String
        +isValidFormat(): boolean
        +getDisplayUrl(): String
    }
    
    class SlideImageContent {
        +id: Long
        +content: byte[]
        +imageMeta: SlideImageMeta
        +getContent(): byte[]
        +getContentSize(): long
        +isValid(): boolean
    }
    
    class User {
        +id: Long
        +username: String
        +password: String
        +role: UserRole
        +lastLogin: LocalDateTime
        +authenticate(password): boolean
        +hasPermission(permission): boolean
        +updateLastLogin(): void
    }
    
    class SyncService {
        +checkForUpdates(slideDeckId, clientVersion): SyncResponse
        +distributeUpdate(slideDeck, screens): void
        +handleConflict(resolution): void
        +getSyncStatus(slideDeckId): SyncStatus
        +forceSync(slideDeckId): void
    }
    
    class VersionController {
        +incrementVersion(slideDeck): void
        +compareVersions(clientVersion, serverVersion): VersionComparison
        +isUpdateRequired(clientVersion, serverVersion): boolean
        +getVersionHistory(slideDeckId): List~VersionInfo~
    }
    
    %% Relationships
    Competition ||--o{ SlideDeck : has
    Competition ||--o{ Category : has
    SlideDeck ||--o{ Slide : contains
    SlideDeck ||--o{ Screen : displayed_on
    Category ||--o{ Team : contains
    Team ||--|| Score : has
    SlideImageMeta ||--|| SlideImageContent : has
    SlideImageMeta ||--o{ ImageSlide : referenced_by
    
    %% Inheritance
    Slide <|-- ImageSlide
    Slide <|-- ScoreSlide
    
    %% Service relationships
    SyncService --> SlideDeck : manages
    SyncService --> Screen : updates
    VersionController --> SlideDeck : controls
    
    classDef entity fill:#e8f5e8
    classDef service fill:#f3e5f5
    classDef abstract fill:#fff3e0
    
    class Competition,SlideDeck,Slide,ImageSlide,ScoreSlide,Category,Team,Score,Screen,SlideImageMeta,SlideImageContent,User entity
    class SyncService,VersionController service
    class Slide abstract
```

## Core Business Objects

### 1. Competition
**Purpose**: Represents a robotics competition event
**Key Responsibilities**:
- Organize slide decks and categories
- Manage competition lifecycle
- Provide competition-wide data access

**Business Rules**:
- Each competition can have multiple slide decks
- Categories are unique within a competition
- Competition status affects system behavior

### 2. SlideDeck
**Purpose**: Manages presentation content and synchronization
**Key Responsibilities**:
- Organize slides in presentation order
- Control version management for synchronization
- Manage transition timing between slides

**Business Rules**:
- Version increments on every modification
- Slides maintain order through index field
- Transition time applies to all slides in deck

### 3. Slide (Abstract)
**Purpose**: Base class for different slide types
**Key Responsibilities**:
- Provide common slide functionality
- Define slide type-specific behavior
- Validate slide content

**Business Rules**:
- Each slide belongs to exactly one slide deck
- Slide index determines display order
- Slide type determines content structure

### 4. ImageSlide
**Purpose**: Displays image content
**Key Responsibilities**:
- Store and retrieve image data
- Validate image formats
- Provide image metadata

**Business Rules**:
- Images stored separately from metadata
- Supports multiple image formats
- Image size limited to 100MB

### 5. ScoreSlide
**Purpose**: Displays team scores and rankings
**Key Responsibilities**:
- Aggregate scores by category
- Calculate rankings
- Update in real-time

**Business Rules**:
- Scores sorted by points, then by time
- Rankings update automatically
- Supports multiple scoring categories

### 6. Category
**Purpose**: Organizes teams and scoring
**Key Responsibilities**:
- Group teams by competition criteria
- Define scoring rules
- Manage team assignments

**Business Rules**:
- Teams belong to exactly one category
- Categories are competition-specific
- Scoring rules defined per category

### 7. Team
**Purpose**: Represents competing teams
**Key Responsibilities**:
- Maintain team information
- Track team scores
- Calculate team rankings

**Business Rules**:
- Each team has exactly one score record
- Team names unique within category
- Team can change categories

### 8. Score
**Purpose**: Records team performance
**Key Responsibilities**:
- Store points and completion time
- Support score comparisons
- Validate score data

**Business Rules**:
- Points and time are required
- Time measured in seconds
- Scores can be updated

### 9. Screen
**Purpose**: Represents physical display screens
**Key Responsibilities**:
- Display synchronized content
- Report status to system
- Handle update polling

**Business Rules**:
- Screens poll for updates regularly
- Screen status affects synchronization
- Each screen displays one slide deck

### 10. SlideImageMeta & SlideImageContent
**Purpose**: Manage image storage and retrieval
**Key Responsibilities**:
- Store image metadata separately from content
- Support efficient image retrieval
- Validate image formats

**Business Rules**:
- Metadata and content stored separately
- Supports multiple image formats
- Content size limited by system

## Business Rules and Constraints

### Synchronization Rules
1. **Version Control**: Every slide deck modification increments version
2. **Polling Frequency**: Screens poll every 5-10 seconds
3. **Conflict Resolution**: Server version always takes precedence
4. **Update Distribution**: Changes distributed to all connected screens

### Data Integrity Rules
1. **Referential Integrity**: All foreign key relationships maintained
2. **Unique Constraints**: Team names unique within category
3. **Cascade Operations**: Deleting slide deck removes all slides
4. **Validation**: All input data validated before storage

### Performance Rules
1. **Caching**: Frequently accessed data cached in Redis
2. **Lazy Loading**: Large objects loaded on demand
3. **Batch Operations**: Multiple updates batched when possible
4. **Connection Pooling**: Database connections pooled

## Object Lifecycle Management

### Creation Lifecycle
1. **Validation**: Input data validated
2. **Persistence**: Object saved to database
3. **Cache Update**: Cache updated if applicable
4. **Event Notification**: Related objects notified

### Update Lifecycle
1. **Version Check**: Optimistic locking applied
2. **Validation**: Updated data validated
3. **Persistence**: Changes saved to database
4. **Synchronization**: Related screens notified

### Deletion Lifecycle
1. **Dependency Check**: Related objects checked
2. **Cascade Operations**: Dependent objects handled
3. **Cleanup**: Resources released
4. **Notification**: Related objects notified

## Object Relationships

### One-to-Many Relationships
- Competition → SlideDeck
- Competition → Category
- SlideDeck → Slide
- SlideDeck → Screen
- Category → Team

### One-to-One Relationships
- Team → Score
- SlideImageMeta → SlideImageContent

### Many-to-Many Relationships
- SlideDeck ↔ Screen (through assignment)

### Inheritance Relationships
- Slide → ImageSlide
- Slide → ScoreSlide

## Service Layer Objects

### SyncService
**Purpose**: Manages real-time synchronization
**Key Methods**:
- `checkForUpdates()`: Determines if updates needed
- `distributeUpdate()`: Sends updates to screens
- `handleConflict()`: Resolves synchronization conflicts

### VersionController
**Purpose**: Manages version control system
**Key Methods**:
- `incrementVersion()`: Increases version number
- `compareVersions()`: Compares client/server versions
- `isUpdateRequired()`: Determines update necessity

## Data Access Patterns

### Repository Pattern
- Each entity has corresponding repository
- Repositories handle data access logic
- Supports custom query methods

### DTO Pattern
- Data transfer objects for API communication
- Separates internal and external data models
- Supports polymorphic serialization

### Assembler Pattern
- Converts between entities and DTOs
- Handles complex object transformations
- Maintains separation of concerns

This analysis object model provides a comprehensive view of the system's domain objects, their relationships, and business rules, serving as the foundation for system design and implementation. 