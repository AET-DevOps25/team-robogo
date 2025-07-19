# Team RoboGo Monitoring Metrics

This document provides a comprehensive overview of all metrics collected in the Team RoboGo monitoring system.

## 📊 Metrics Overview

### Metric Categories

1. **Application Metrics** - Business logic and performance
2. **Infrastructure Metrics** - System resources and health
3. **Custom Business Metrics** - Domain-specific measurements
4. **Service Metrics** - Individual service performance

## 🎯 Application Metrics

### Backend Service (Spring Boot)

#### HTTP Metrics
```yaml
# Request Counters
http_server_requests_total
  - Description: Total HTTP requests processed
  - Labels: method, status, uri, outcome
  - Type: Counter
  - Example: http_server_requests_total{method="GET",status="200",uri="/api/screens"}

# Request Duration
http_server_requests_duration_seconds
  - Description: HTTP request duration in seconds
  - Labels: method, status, uri, outcome
  - Type: Histogram
  - Example: http_server_requests_duration_seconds_bucket{le="0.1",method="GET"}

# Request Duration Summary
http_server_requests_duration_seconds_sum
http_server_requests_duration_seconds_count
  - Description: Sum and count of request durations
  - Type: Counter
```

#### JVM Metrics
```yaml
# Memory Usage
jvm_memory_used_bytes
  - Description: JVM memory usage in bytes
  - Labels: area (heap, nonheap), id
  - Type: Gauge
  - Example: jvm_memory_used_bytes{area="heap",id="PS Eden Space"}

# Garbage Collection
jvm_gc_collection_seconds
  - Description: Time spent in garbage collection
  - Labels: gc
  - Type: Counter
  - Example: jvm_gc_collection_seconds{gc="G1 Young Generation"}

# Thread Metrics
jvm_threads_live_threads
  - Description: Number of live threads
  - Type: Gauge

jvm_threads_daemon_threads
  - Description: Number of daemon threads
  - Type: Gauge
```

#### Process Metrics
```yaml
# CPU Usage
process_cpu_seconds_total
  - Description: Total CPU time used by the process
  - Type: Counter
  - Example: rate(process_cpu_seconds_total[5m]) * 100

# Memory Usage
process_resident_memory_bytes
  - Description: Resident memory usage
  - Type: Gauge
  - Example: process_resident_memory_bytes / 1024 / 1024

# Start Time
process_start_time_seconds
  - Description: Start time of the process
  - Type: Gauge
```

### API Gateway (Spring Cloud Gateway)

#### Gateway Metrics
```yaml
# Gateway Requests
gateway_requests_total
  - Description: Total requests processed by gateway
  - Labels: routeId, status, outcome
  - Type: Counter
  - Example: gateway_requests_total{routeId="server_route",status="200"}

# Gateway Duration
gateway_requests_duration_seconds
  - Description: Gateway request processing time
  - Labels: routeId, status, outcome
  - Type: Histogram
  - Example: gateway_requests_duration_seconds_bucket{le="0.1",routeId="server_route"}
```

#### Circuit Breaker Metrics
```yaml
# Circuit Breaker State
resilience4j_circuitbreaker_state
  - Description: Current state of circuit breaker
  - Labels: name, kind
  - Type: Gauge
  - Values: 0 (CLOSED), 1 (OPEN), 2 (HALF_OPEN)

# Circuit Breaker Calls
resilience4j_circuitbreaker_calls
  - Description: Number of calls to circuit breaker
  - Labels: name, kind, result
  - Type: Counter
  - Example: resilience4j_circuitbreaker_calls{name="default",result="success"}
```

### GenAI Service (FastAPI)

#### HTTP Metrics
```yaml
# Request Counters
genai_genai_http_requests_total
  - Description: Total HTTP requests to GenAI service
  - Labels: method, status, endpoint
  - Type: Counter
  - Example: genai_genai_http_requests_total{method="POST",endpoint="/genai/suggest"}

# Request Duration
genai_genai_http_request_duration_seconds
  - Description: Request processing time
  - Labels: method, status, endpoint
  - Type: Histogram
  - Example: genai_genai_http_request_duration_seconds_bucket{le="1.0"}

# Exceptions
genai_genai_http_exceptions_total
  - Description: Total exceptions thrown
  - Labels: exception_type
  - Type: Counter
  - Example: genai_genai_http_exceptions_total{exception_type="ValidationError"}

# Requests in Progress
genai_genai_http_requests_in_progress
  - Description: Number of requests currently being processed
  - Type: Gauge
  - Example: genai_genai_http_requests_in_progress
```

#### AI Service Metrics
```yaml
# Model Inference Time
genai_model_inference_time
  - Description: Time taken for model inference
  - Labels: model_name
  - Type: Histogram
  - Example: genai_model_inference_time_bucket{model_name="gpt-3.5-turbo"}

# Token Usage
genai_token_usage
  - Description: Number of tokens consumed
  - Labels: model_name, token_type
  - Type: Counter
  - Example: genai_token_usage{model_name="gpt-3.5-turbo",token_type="prompt"}

# Cache Hit Rate
genai_cache_hit_rate
  - Description: Cache hit ratio
  - Type: Gauge
  - Example: genai_cache_hit_rate
```

## 🏗️ Infrastructure Metrics

### Database (PostgreSQL)

#### Connection Metrics
```yaml
# Active Connections
pg_stat_database_numbackends
  - Description: Number of active connections
  - Labels: datname, datid
  - Type: Gauge
  - Example: pg_stat_database_numbackends{datname="robogo_db"}

# Connection Statistics
pg_stat_database_xact_commit
pg_stat_database_xact_rollback
  - Description: Transaction commit/rollback counts
  - Labels: datname, datid
  - Type: Counter
  - Example: rate(pg_stat_database_xact_commit{datname="robogo_db"}[5m])
```

#### Performance Metrics
```yaml
# Tuple Operations
pg_stat_database_tup_fetched
pg_stat_database_tup_inserted
pg_stat_database_tup_updated
pg_stat_database_tup_deleted
  - Description: Tuple operation counts
  - Labels: datname, datid
  - Type: Counter
  - Example: rate(pg_stat_database_tup_inserted{datname="robogo_db"}[5m])

# Cache Performance
pg_stat_database_blks_hit
pg_stat_database_blks_read
  - Description: Cache hit and disk read counts
  - Labels: datname, datid
  - Type: Counter
  - Example: pg_stat_database_blks_hit{datname="robogo_db"} / (pg_stat_database_blks_hit{datname="robogo_db"} + pg_stat_database_blks_read{datname="robogo_db"})
```

### Cache (Redis)

#### Memory Metrics
```yaml
# Memory Usage
redis_memory_used_bytes
  - Description: Memory used by Redis
  - Type: Gauge
  - Example: redis_memory_used_bytes

redis_memory_max_bytes
  - Description: Maximum memory configured
  - Type: Gauge
  - Example: redis_memory_max_bytes
```

#### Performance Metrics
```yaml
# Command Processing
redis_commands_processed_total
  - Description: Total commands processed
  - Labels: cmd
  - Type: Counter
  - Example: rate(redis_commands_processed_total[5m])

# Connections
redis_connections_total
  - Description: Total connections handled
  - Type: Counter
  - Example: redis_connections_total

# Cache Performance
redis_keyspace_hits_total
redis_keyspace_misses_total
  - Description: Cache hit and miss counts
  - Type: Counter
  - Example: rate(redis_keyspace_hits_total[5m]) / (rate(redis_keyspace_hits_total[5m]) + rate(redis_keyspace_misses_total[5m]))
```

### System Metrics

#### Container Metrics
```yaml
# CPU Usage
container_cpu_usage_seconds_total
  - Description: CPU usage by container
  - Labels: container, pod, namespace
  - Type: Counter
  - Example: rate(container_cpu_usage_seconds_total{container="server"}[5m])

# Memory Usage
container_memory_usage_bytes
  - Description: Memory usage by container
  - Labels: container, pod, namespace
  - Type: Gauge
  - Example: container_memory_usage_bytes{container="server"}

# Network Usage
container_network_receive_bytes_total
container_network_transmit_bytes_total
  - Description: Network I/O by container
  - Labels: container, pod, namespace
  - Type: Counter
  - Example: rate(container_network_receive_bytes_total{container="server"}[5m])
```

#### Node Metrics
```yaml
# CPU Usage
node_cpu_seconds_total
  - Description: CPU time by mode
  - Labels: cpu, mode
  - Type: Counter
  - Example: rate(node_cpu_seconds_total{mode="idle"}[5m])

# Memory Usage
node_memory_MemAvailable_bytes
node_memory_MemTotal_bytes
  - Description: Available and total memory
  - Type: Gauge
  - Example: node_memory_MemAvailable_bytes / node_memory_MemTotal_bytes * 100

# Disk Usage
node_filesystem_avail_bytes
node_filesystem_size_bytes
  - Description: Available and total disk space
  - Labels: device, fstype, mountpoint
  - Type: Gauge
  - Example: node_filesystem_avail_bytes{mountpoint="/"} / node_filesystem_size_bytes{mountpoint="/"} * 100
```

## 🎯 Custom Business Metrics

### Screen Metrics
```yaml
# Active Screens
active_screens_count
  - Description: Number of currently active screens
  - Type: Gauge
  - Source: Custom metric from ScreenRepository
  - Example: active_screens_count

# Total Screens
total_screens_count
  - Description: Total number of screens in system
  - Type: Gauge
  - Source: Custom metric from ScreenRepository
  - Example: total_screens_count
```

### Content Metrics
```yaml
# Slide Decks
total_slide_decks_count
  - Description: Total number of slide decks
  - Type: Gauge
  - Source: Custom metric from SlideDeckRepository
  - Example: total_slide_decks_count

# Teams
total_teams_count
  - Description: Total number of teams
  - Type: Gauge
  - Source: Custom metric from TeamRepository
  - Example: total_teams_count

# Scores
total_scores_count
  - Description: Total number of scores
  - Type: Gauge
  - Source: Custom metric from ScoreRepository
  - Example: total_scores_count
```

### Activity Metrics
```yaml
# Slide Deck Updates
slide_deck_updates_total
  - Description: Total slide deck updates
  - Type: Counter
  - Source: Custom metric from SlideDeckService
  - Example: rate(slide_deck_updates_total[5m])

# Screen Status Changes
screen_status_changes_total
  - Description: Total screen status changes
  - Type: Counter
  - Source: Custom metric from ScreenService
  - Example: rate(screen_status_changes_total[5m])

# Score Updates
score_updates_total
  - Description: Total score updates
  - Type: Counter
  - Source: Custom metric from ScoreService
  - Example: rate(score_updates_total[5m])
```

## 📊 Metric Collection Configuration

### Prometheus Scrape Configuration

```yaml
scrape_configs:
  - job_name: 'server'
    static_configs:
      - targets: ['server:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s

  - job_name: 'gateway'
    static_configs:
      - targets: ['gateway:8080']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s

  - job_name: 'genai'
    static_configs:
      - targets: ['genai:5000']
    metrics_path: '/metrics'
    scrape_interval: 10s

  - job_name: 'postgres-exporter'
    static_configs:
      - targets: ['postgres-exporter:9187']
    scrape_interval: 30s

  - job_name: 'redis-exporter'
    static_configs:
      - targets: ['redis-exporter:9121']
    scrape_interval: 30s
```

### Custom Metrics Configuration

```java
@Configuration
public class MetricsConfig {
    
    @Bean
    public Counter slideDeckUpdateCounter(MeterRegistry meterRegistry) {
        return Counter.builder("slide_deck_updates_total")
                .description("Total number of slide deck updates")
                .register(meterRegistry);
    }
    
    @Bean
    public Gauge activeScreensGauge(MeterRegistry meterRegistry, ScreenRepository screenRepository) {
        return Gauge.builder("active_screens_count", screenRepository, 
            repo -> repo.findAll().stream()
                .filter(screen -> "ONLINE".equals(screen.getStatus().name()))
                .count())
                .description("Number of currently active screens")
                .register(meterRegistry);
    }
}
```

## 🔍 Metric Query Examples

### Performance Queries

```promql
# Request Rate
rate(http_server_requests_total{job="server"}[5m])

# Error Rate
rate(http_server_requests_total{job="server",status=~"5.."}[5m]) / rate(http_server_requests_total{job="server"}[5m])

# Response Time (95th percentile)
histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job="server"}[5m]))

# CPU Usage
rate(process_cpu_seconds_total{job="server"}[5m]) * 100

# Memory Usage (MB)
process_resident_memory_bytes{job="server"} / 1024 / 1024
```

### Business Queries

```promql
# Active Screens Ratio
active_screens_count / total_screens_count * 100

# Slide Deck Update Rate
rate(slide_deck_updates_total[5m])

# Score Update Rate
rate(score_updates_total[5m])

# Screen Status Change Rate
rate(screen_status_changes_total[5m])
```

### Database Queries

```promql
# Database Connections
pg_stat_database_numbackends{datname="robogo_db"}

# Transaction Rate
rate(pg_stat_database_xact_commit{datname="robogo_db"}[5m])

# Cache Hit Ratio
pg_stat_database_blks_hit{datname="robogo_db"} / (pg_stat_database_blks_hit{datname="robogo_db"} + pg_stat_database_blks_read{datname="robogo_db"})
```

## 🚨 Alert Thresholds

### Service Health
- **Service Down**: `up{job="service"} == 0`
- **High Error Rate**: Error rate > 5%
- **Slow Response Time**: 95th percentile > 2s

### Performance
- **High CPU**: CPU usage > 80%
- **High Memory**: Memory usage > 1GB
- **High Database Connections**: Connections > 50

### Business
- **No Active Screens**: `active_screens_count == 0`
- **Low Screen Activity**: Active ratio < 50%
- **High Activity**: Update rate > threshold

## 📚 Additional Resources

- [Prometheus Metric Types](https://prometheus.io/docs/concepts/metric_types/)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)
- [Micrometer Documentation](https://micrometer.io/docs)
- [PromQL Query Language](https://prometheus.io/docs/prometheus/latest/querying/) 