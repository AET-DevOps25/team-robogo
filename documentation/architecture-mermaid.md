# Team RoboGo System Architecture Diagrams (Mermaid)

## High-Level System Architecture Visualization

![High-Level System Architecture](high-level.png)
*High-level system architecture showing the complete layered structure from client to infrastructure*

## 1. High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        Client[Vue.js 3 Frontend]
        Screen1[Screen Display 1]
        Screen2[Screen Display 2]
        ScreenN[Screen Display N]
    end
    
    subgraph "Gateway Layer"
        Gateway[API Gateway<br/>Spring Cloud Gateway]
    end
    
    subgraph "Service Layer"
        Server[Spring Boot Service<br/>Business Logic Layer]
        GenAI[GenAI Service<br/>FastAPI]
    end
    
    subgraph "Data Layer"
        DB[(PostgreSQL<br/>Main Database)]
        Cache[(Redis<br/>Cache)]
        FileStorage[File Storage<br/>Images/Slides]
    end
    
    subgraph "Infrastructure Layer"
        Docker[Docker Containers]
        K8s[Kubernetes<br/>Orchestration]
        Monitoring[Monitoring System<br/>Prometheus + Grafana]
    end
    
    Client --> Gateway
    Screen1 --> Gateway
    Screen2 --> Gateway
    ScreenN --> Gateway
    
    Gateway --> Server
    Gateway --> GenAI
    
    Server --> DB
    Server --> Cache
    Server --> FileStorage
    
    GenAI --> Cache
    GenAI --> FileStorage
    
    Server --> Monitoring
    GenAI --> Monitoring
    
    Docker --> K8s
```

## 2. Detailed Component Architecture

![Detailed Component Architecture](detailed-level.png)
*Detailed component architecture showing frontend, backend, AI services, and data storage relationships*

```mermaid
graph TB
    subgraph "Frontend Components"
        VueApp[Vue.js 3 Application]
        PiniaStore[Pinia State Management]
        TailwindCSS[Tailwind CSS]
        Components[Vue Component Library]
    end
    
    subgraph "Backend Services"
        AuthService[Authentication Service<br/>JWT]
        SlideService[Slide Service]
        ScoreService[Score Service]
        SyncService[Sync Service]
        CategoryService[Category Service]
    end
    
    subgraph "AI Services"
        LLMService[Large Language Model Service]
        ContentGen[Content Generation]
        ScoreAnalysis[Score Analysis]
        Optimization[Content Optimization]
    end
    
    subgraph "Data Storage"
        UserTable[(User Table)]
        SlideTable[(Slide Table)]
        ScoreTable[(Score Table)]
        CategoryTable[(Category Table)]
        SyncTable[(Sync Status Table)]
    end
    
    VueApp --> PiniaStore
    PiniaStore --> Components
    Components --> TailwindCSS
    
    AuthService --> UserTable
    SlideService --> SlideTable
    ScoreService --> ScoreTable
    CategoryService --> CategoryTable
    SyncService --> SyncTable
    
    LLMService --> ContentGen
    LLMService --> ScoreAnalysis
    LLMService --> Optimization
    
    ContentGen --> SlideTable
    ScoreAnalysis --> ScoreTable
```

## 4. Real-time Synchronization Architecture
![Real-time Synchronization](sync.png)
```mermaid
---
config:
  theme: redux-color
  look: neo
---
sequenceDiagram
    participant Client as Client
    participant Gateway as API Gateway
    participant Server as Spring Boot Service
    participant SyncService as Sync Service
    participant DB as Database
    participant Cache as Redis Cache
    Note over Client, Cache: Initial Sync
    Client->>Gateway: HTTP Poll - Check version
    Gateway->>Server: Route API Request
    Server->>SyncService: getSyncStatus(deckId)
    SyncService->>DB: Query SlideDeck table
    DB-->>SyncService: Return version and lastUpdate
    SyncService-->>Server: Return sync status
    Server-->>Gateway: Return sync data
    Gateway-->>Client: Return current sync status
    Note over Client, Cache: Content Update
    Server->>DB: Update slide content
    DB->>DB: Increment version, update timestamp
    Server->>SyncService: forceSyncUpdate(deckId)
    SyncService->>DB: Update version and lastUpdate
    DB-->>SyncService: Confirm update
    SyncService-->>Server: Return updated sync status
    Note over Client, Cache: Client Detects Change and Syncs
    loop Every 5 seconds
        Client->>Gateway: HTTP Poll - Check version
        Gateway->>Server: Route API Request
        Server->>SyncService: isSyncNeeded(deckId, lastKnownUpdate)
        SyncService->>DB: Compare timestamps
        alt Sync needed
            DB-->>SyncService: Return new version and lastUpdate
            SyncService-->>Server: Return updated sync status
            Server-->>Gateway: Return updated sync data
            Gateway-->>Client: Return updated content
        else No sync needed
            DB-->>SyncService: Return current version
            SyncService-->>Server: Return no change
            Server-->>Gateway: Return no change
            Gateway-->>Client: Return no change
        end
    end

```

## 5. Deployment Architecture
![Deployment Architecture](deployment.png)
```mermaid
---
config:
  theme: base
  look: handDrawn
  layout: elk
---
flowchart TB
 subgraph subGraph0["Development Environment"]
        DevClient["Development Client<br>localhost:3000"]
        DevServer["Development Server<br>localhost:8081"]
        DevGenAI["Development AI Service<br>localhost:5000"]
        DevDB["Development Database"]
  end
 subgraph subGraph1["Production Environment"]
        ProdClient["Production Client<br>Kubernetes Pod"]
        ProdServer["Production Server<br>Kubernetes Pod"]
        ProdGenAI["Production AI Service<br>Kubernetes Pod"]
        ProdDB["Production Database<br>PostgreSQL"]
  end
 subgraph subGraph2["Monitoring System"]
        Prometheus["Prometheus<br>Metrics Collection"]
        Grafana["Grafana<br>Visualization"]
        Loki["Loki<br>Log Aggregation"]
        AlertManager["AlertManager<br>Alert Management"]
        n2["grafana"]
  end
 subgraph Infrastructure["Infrastructure"]
        Docker["Docker Containers"]
        K8s["Kubernetes Cluster"]
        Ingress["Ingress Controller"]
        LoadBalancer["Load Balancer"]
        n1["k8s"]
  end
    DevClient --> DevServer
    DevServer --> DevGenAI & DevDB
    ProdClient --> Ingress
    ProdServer --> Ingress & ProdDB & Prometheus
    ProdGenAI --> Ingress & ProdDB & Prometheus
    Ingress --> LoadBalancer
    Prometheus --> Grafana & AlertManager
    Docker --> K8s
    K8s --> Ingress
    n2@{ icon: "aws:arch-amazon-managed-grafana", pos: "t"}
    n1@{ icon: "gcp:kuberun", pos: "b"}

```


## Technology Stack Summary

### Frontend Technology Stack
- **Framework**: Vue.js 3 (Composition API)
- **UI Library**: Tailwind CSS
- **State Management**: Pinia
- **Build Tool**: Vite
- **Testing**: Vitest

### Backend Technology Stack
- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Cache**: Redis
- **Authentication**: JWT
- **Documentation**: Swagger-UI

### AI Service Technology Stack
- **Framework**: FastAPI (Python)
- **ML Libraries**: Based on requirements
- **Communication**: REST APIs

### Infrastructure Technology Stack
- **Containerization**: Docker
- **Orchestration**: Kubernetes
- **Monitoring**: Prometheus, Grafana, Loki
- **CI/CD**: GitHub Actions

This architecture provides a robust foundation for the Team RoboGo system, ensuring scalability, reliability, and maintainability while supporting the specific requirements of robotics competition management. 