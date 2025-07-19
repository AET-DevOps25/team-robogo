# Team RoboGo Operations Guide

This document provides comprehensive operational guidance for the Team RoboGo system, including monitoring architecture, metrics, dashboards, and alerting configuration.

## 📊 Monitoring Architecture

### System Overview

Team RoboGo is a microservices-based application with the following components:

- **API Gateway** (Spring Cloud Gateway) - Port 8080
- **Backend Server** (Spring Boot) - Port 8081  
- **GenAI Service** (FastAPI) - Port 5000
- **Client** (Vue.js) - Port 3000
- **Database** (PostgreSQL) - Port 5432
- **Cache** (Redis) - Port 6379
- **Monitoring Stack**:
  - Prometheus - Metrics collection
  - AlertManager - Alert management
  - Grafana - Visualization
  - Loki - Log aggregation
  - Promtail - Log collection

### Monitoring Stack Components

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Application   │    │    Prometheus   │    │   AlertManager  │
│     Services    │───▶│   (Metrics)     │───▶│   (Alerts)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                │                       │
                                ▼                       ▼
                       ┌─────────────────┐    ┌─────────────────┐
                       │     Grafana     │    │   Slack/Email   │
                       │ (Dashboards)    │    │  (Notifications)│
                       └─────────────────┘    └─────────────────┘
```

## 🎯 Key Metrics

### Application Metrics

#### Backend Service (Spring Boot)
```yaml
# HTTP Metrics
- http_server_requests_total: Total HTTP requests
- http_server_requests_duration_seconds: Request duration
- http_server_requests_duration_seconds_bucket: Request duration histogram

# JVM Metrics  
- jvm_memory_used_bytes: Memory usage
- jvm_gc_collection_seconds: Garbage collection time
- process_cpu_seconds_total: CPU usage
- process_resident_memory_bytes: Resident memory

# Custom Business Metrics
- active_screens_count: Number of active screens
- total_screens_count: Total screens in system
- total_slide_decks_count: Total slide decks
- total_teams_count: Total teams
- total_scores_count: Total scores
- slide_deck_updates_total: Slide deck update counter
- screen_status_changes_total: Screen status change counter
- score_updates_total: Score update counter
```

#### API Gateway (Spring Cloud Gateway)
```yaml
# Gateway Metrics
- gateway_requests_total: Gateway request count
- gateway_requests_duration_seconds: Gateway request duration
- gateway_requests_duration_seconds_bucket: Gateway request duration histogram

# Circuit Breaker Metrics
- resilience4j_circuitbreaker_calls: Circuit breaker calls
- resilience4j_circuitbreaker_state: Circuit breaker state
```

#### GenAI Service (FastAPI)
```yaml
# HTTP Metrics
- genai_genai_http_requests_total: Total requests
- genai_genai_http_request_duration_seconds: Request duration
- genai_genai_http_exceptions_total: Exception count
- genai_genai_http_requests_in_progress: Requests in progress

# AI Service Metrics
- genai_model_inference_time: Model inference time
- genai_token_usage: Token consumption
- genai_cache_hit_rate: Cache hit rate
```

### Infrastructure Metrics

#### Database (PostgreSQL)
```yaml
# Connection Metrics
- pg_stat_database_numbackends: Active connections
- pg_stat_database_xact_commit: Transaction commits
- pg_stat_database_xact_rollback: Transaction rollbacks

# Performance Metrics
- pg_stat_database_tup_fetched: Tuples fetched
- pg_stat_database_tup_inserted: Tuples inserted
- pg_stat_database_tup_updated: Tuples updated
- pg_stat_database_tup_deleted: Tuples deleted

# Cache Metrics
- pg_stat_database_blks_hit: Cache hits
- pg_stat_database_blks_read: Disk reads
```

#### Cache (Redis)
```yaml
# Memory Metrics
- redis_memory_used_bytes: Memory usage
- redis_memory_max_bytes: Maximum memory

# Performance Metrics
- redis_commands_processed_total: Commands processed
- redis_connections_total: Active connections
- redis_keyspace_hits_total: Cache hits
- redis_keyspace_misses_total: Cache misses
```

#### System Metrics
```yaml
# Container Metrics
- container_cpu_usage_seconds_total: CPU usage
- container_memory_usage_bytes: Memory usage
- container_network_receive_bytes_total: Network receive
- container_network_transmit_bytes_total: Network transmit

# Node Metrics
- node_cpu_seconds_total: Node CPU usage
- node_memory_MemAvailable_bytes: Available memory
- node_filesystem_avail_bytes: Disk space
```

## 📈 Dashboards

### 1. System Overview Dashboard

**Purpose**: High-level system health and performance overview

**Panels**:
- System Status Overview
- Service Health Status
- Request Rate (RPS)
- Error Rate (%)
- Response Time (95th percentile)
- CPU Usage by Service
- Memory Usage by Service
- Active Screens Count
- Database Connections

**Queries**:
```promql
# Request Rate
rate(http_server_requests_total{job="server"}[5m])

# Error Rate  
rate(http_server_requests_total{job="server",status=~"5.."}[5m]) / rate(http_server_requests_total{job="server"}[5m])

# Response Time
histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job="server"}[5m]))

# Active Screens
active_screens_count
```

### 2. Application Performance Dashboard

**Purpose**: Detailed application performance metrics

**Panels**:
- HTTP Request Rate by Endpoint
- HTTP Response Time by Endpoint
- HTTP Status Code Distribution
- JVM Memory Usage
- Garbage Collection Time
- Custom Business Metrics
- Slide Deck Updates Rate
- Score Updates Rate

**Queries**:
```promql
# Request Rate by Endpoint
rate(http_server_requests_total{job="server"}[5m]) by (uri)

# Response Time by Endpoint
histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job="server"}[5m])) by (uri)

# Business Metrics
rate(slide_deck_updates_total[5m])
rate(score_updates_total[5m])
```

### 3. Database Performance Dashboard

**Purpose**: Database performance and health monitoring

**Panels**:
- Database Connections
- Transaction Rate
- Query Performance
- Cache Hit Ratio
- Table Statistics
- Lock Statistics
- Database Size

**Queries**:
```promql
# Database Connections
pg_stat_database_numbackends{datname="robogo_db"}

# Transaction Rate
rate(pg_stat_database_xact_commit{datname="robogo_db"}[5m])

# Cache Hit Ratio
pg_stat_database_blks_hit{datname="robogo_db"} / (pg_stat_database_blks_hit{datname="robogo_db"} + pg_stat_database_blks_read{datname="robogo_db"})
```

### 4. GenAI Service Dashboard

**Purpose**: AI service performance and usage monitoring

**Panels**:
- Request Rate
- Response Time
- Error Rate
- Model Inference Time
- Token Usage
- Cache Hit Rate
- Requests in Progress

**Queries**:
```promql
# Request Rate
rate(genai_genai_http_requests_total[5m])

# Response Time
histogram_quantile(0.95, rate(genai_genai_http_request_duration_seconds_bucket[5m]))

# Error Rate
rate(genai_genai_http_exceptions_total[5m]) / rate(genai_genai_http_requests_total[5m])
```

### 5. Business Metrics Dashboard

**Purpose**: Business-specific metrics and KPIs

**Panels**:
- Active Screens Count
- Total Screens Count
- Screen Activity Ratio
- Slide Deck Updates
- Score Updates
- Team Count
- Competition Activity

**Queries**:
```promql
# Screen Metrics
active_screens_count
total_screens_count
active_screens_count / total_screens_count

# Business Activity
rate(slide_deck_updates_total[5m])
rate(score_updates_total[5m])
rate(screen_status_changes_total[5m])
```

## 🚨 Alerting Rules

### Service Alerts

#### Critical Alerts
- **GatewayDown**: API Gateway service unavailable
- **ServerDown**: Backend server unavailable  
- **DatabaseDown**: PostgreSQL database unavailable

#### Warning Alerts
- **GenAIDown**: GenAI service unavailable
- **RedisDown**: Redis cache unavailable

### Performance Alerts

#### Warning Alerts
- **HighGatewayCPU**: Gateway CPU usage > 80%
- **HighServerCPU**: Server CPU usage > 80%
- **HighMemoryUsage**: Server memory > 1GB
- **HighDatabaseConnections**: DB connections > 50

### Business Alerts

#### Warning Alerts
- **NoActiveScreens**: No active screens detected
- **HighErrorRate**: Error rate > 5%
- **SlowResponseTime**: 95th percentile response time > 2s
- **GenAIHighLatency**: GenAI response time > 10s
- **GenAIHighErrorRate**: GenAI error rate > 10%

#### Info Alerts
- **LowActiveScreens**: < 50% screens active
- **HighRequestRate**: > 100 requests/minute
- **FrequentSlideDeckUpdates**: > 10 updates/5min
- **FrequentScoreUpdates**: > 20 updates/5min

## 🔧 AlertManager Configuration

### Notification Channels

1. **Slack Notifications**
   - Channel: #team-robogo-alerts
   - Severity-based colors
   - Grouped by alert name and service

2. **PagerDuty Integration**
   - Critical alerts only
   - Automatic escalation

3. **Email Notifications**
   - HTML formatted alerts
   - Detailed alert information

### Alert Routing

```yaml
route:
  group_by: ['alertname', 'service']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'slack-notifications'
  routes:
    - match:
        severity: critical
      receiver: 'pager-duty-critical'
    - match:
        severity: info
      receiver: 'slack-info'
```

### Alert Suppression

```yaml
inhibit_rules:
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['service']
```

## 🛠️ Operational Procedures

### Daily Operations

1. **Health Check**
   - Review Grafana dashboards
   - Check AlertManager for active alerts
   - Verify all services are running

2. **Performance Review**
   - Monitor response times
   - Check error rates
   - Review resource usage

3. **Business Metrics**
   - Monitor active screens
   - Track slide deck updates
   - Review score updates

### Weekly Operations

1. **Capacity Planning**
   - Review resource usage trends
   - Plan for scaling if needed
   - Update alert thresholds

2. **Maintenance**
   - Update monitoring rules
   - Review and optimize queries
   - Clean up old metrics

### Incident Response

1. **Alert Investigation**
   - Check service logs
   - Review metrics history
   - Identify root cause

2. **Escalation**
   - Critical alerts → PagerDuty
   - Warning alerts → Slack
   - Info alerts → Slack info channel

3. **Resolution**
   - Implement fixes
   - Update monitoring if needed
   - Document lessons learned

## 📋 Deployment Checklist

### Pre-deployment
- [ ] Verify all services are healthy
- [ ] Check resource availability
- [ ] Review alert configurations
- [ ] Test notification channels

### Post-deployment
- [ ] Verify all metrics are being collected
- [ ] Check dashboard functionality
- [ ] Test alert notifications
- [ ] Review business metrics

## 🔍 Troubleshooting

### Common Issues

1. **Missing Metrics**
   - Check service endpoints
   - Verify Prometheus targets
   - Review service logs

2. **High Error Rates**
   - Check application logs
   - Review database connections
   - Monitor resource usage

3. **Slow Response Times**
   - Check database performance
   - Review cache hit rates
   - Monitor network latency

### Debug Commands

```bash
# Check Prometheus targets
kubectl port-forward svc/prometheus 9090:9090
curl http://localhost:9090/api/v1/targets

# Check AlertManager
kubectl port-forward svc/team-robogo-alertmanager-service 9093:9093
curl http://localhost:9093/api/v1/alerts

# Check service metrics
kubectl port-forward svc/server-service 8081:8081
curl http://localhost:8081/actuator/prometheus
```

## 📚 Additional Resources

- [Prometheus Query Language](https://prometheus.io/docs/prometheus/latest/querying/)
- [Grafana Dashboard Documentation](https://grafana.com/docs/grafana/latest/dashboards/)
- [AlertManager Configuration](https://prometheus.io/docs/alerting/latest/configuration/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html) 