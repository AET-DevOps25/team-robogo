# Team RoboGo Monitoring Guide

This document provides comprehensive monitoring instructions for the Team RoboGo system, including metrics collection, alerting, and observability practices.

## 📊 Monitoring Architecture

### System Overview

Team RoboGo is a microservices-based application with comprehensive monitoring:

- **API Gateway** (Spring Cloud Gateway) - Port 8080
- **Backend Server** (Spring Boot) - Port 8081  
- **GenAI Service** (FastAPI) - Port 5000
- **Client** (Vue.js) - Port 3000
- **Database** (PostgreSQL) - Port 5432
- **Cache** (Redis) - Port 6379

### Monitoring Stack

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

### Components

- **Prometheus** - Metrics collection and storage
- **AlertManager** - Alert management and routing
- **Grafana** - Visualization and dashboards
- **Loki** - Log aggregation
- **Promtail** - Log collection

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
- process_cpu_usage: CPU usage
- process_start_time_seconds: Process start time

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
# PostgreSQL Metrics
- pg_stat_database_numbackends: Active connections
- pg_stat_database_xact_commit: Transaction commits
- pg_stat_database_xact_rollback: Transaction rollbacks
- pg_stat_database_tup_fetched: Tuples fetched
- pg_stat_database_tup_inserted: Tuples inserted
- pg_stat_database_tup_updated: Tuples updated
- pg_stat_database_tup_deleted: Tuples deleted
- pg_stat_database_blks_hit: Cache hits
- pg_stat_database_blks_read: Disk reads
```

#### Cache (Redis)
```yaml
# Redis Metrics
- redis_memory_used_bytes: Memory usage
- redis_memory_max_bytes: Maximum memory
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
- node_cpu_seconds_total: CPU usage
- node_memory_MemAvailable_bytes: Available memory
- node_filesystem_avail_bytes: Disk space
```

## 📈 Prometheus Configuration

### prometheus.yml
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "/etc/prometheus-rules/alert.rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

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

## 🚨 Alerting Rules

### Service Alerts

#### Critical Alerts
```yaml
- alert: GatewayDown
  expr: up{job="gateway"} == 0
  for: 1m
  labels:
    severity: critical
    service: gateway
  annotations:
    summary: "API Gateway is down"
    description: "API Gateway has been unreachable for 1 minute."

- alert: ServerDown
  expr: up{job="server"} == 0
  for: 1m
  labels:
    severity: critical
    service: server
  annotations:
    summary: "Server is down"
    description: "Backend server has been unreachable for 1 minute."

- alert: DatabaseDown
  expr: up{job="postgres-exporter"} == 0
  for: 1m
  labels:
    severity: critical
    service: database
  annotations:
    summary: "Database is down"
    description: "PostgreSQL database has been unreachable for 1 minute."
```

#### Warning Alerts
```yaml
- alert: GenAIDown
  expr: up{job="genai"} == 0
  for: 1m
  labels:
    severity: warning
    service: genai
  annotations:
    summary: "GenAI service is down"
    description: "GenAI service has been unreachable for 1 minute."

- alert: RedisDown
  expr: up{job="redis-exporter"} == 0
  for: 1m
  labels:
    severity: warning
    service: redis
  annotations:
    summary: "Redis is down"
    description: "Redis cache has been unreachable for 1 minute."
```

### Performance Alerts

#### Warning Alerts
```yaml
- alert: HighGatewayCPU
  expr: rate(process_cpu_seconds_total{job="gateway"}[5m]) * 100 > 80
  for: 5m
  labels:
    severity: warning
    service: gateway
  annotations:
    summary: "Gateway CPU usage is high"
    description: "Gateway CPU usage has been above 80% for 5 minutes."

- alert: HighServerCPU
  expr: rate(process_cpu_seconds_total{job="server"}[5m]) * 100 > 80
  for: 5m
  labels:
    severity: warning
    service: server
  annotations:
    summary: "Server CPU usage is high"
    description: "Backend server CPU usage has been above 80% for 5 minutes."

- alert: HighMemoryUsage
  expr: (process_resident_memory_bytes{job="server"} / 1024 / 1024) > 1024
  for: 5m
  labels:
    severity: warning
    service: server
  annotations:
    summary: "High memory usage"
    description: "Server memory usage is above 1GB for 5 minutes."

- alert: HighDatabaseConnections
  expr: pg_stat_database_numbackends{datname="robogo_db"} > 50
  for: 2m
  labels:
    severity: warning
    service: database
  annotations:
    summary: "High database connections"
    description: "Database has more than 50 active connections for 2 minutes."
```

### Business Alerts

#### Warning Alerts
```yaml
- alert: NoActiveScreens
  expr: active_screens_count == 0
  for: 5m
  labels:
    severity: warning
    service: business
  annotations:
    summary: "No active screens detected"
    description: "No screens are currently active in the system for 5 minutes."

- alert: HighErrorRate
  expr: rate(http_server_requests_total{job="server",status=~"5.."}[5m]) / rate(http_server_requests_total{job="server"}[5m]) > 0.05
  for: 2m
  labels:
    severity: warning
    service: business
  annotations:
    summary: "High error rate detected"
    description: "Error rate is above 5% for 2 minutes."

- alert: SlowResponseTime
  expr: histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job="server"}[5m])) > 2
  for: 3m
  labels:
    severity: warning
    service: business
  annotations:
    summary: "Slow response time"
    description: "95th percentile response time is above 2 seconds for 3 minutes."

- alert: GenAIHighLatency
  expr: histogram_quantile(0.95, rate(genai_genai_http_request_duration_seconds_bucket{job="genai"}[5m])) > 10
  for: 3m
  labels:
    severity: warning
    service: business
  annotations:
    summary: "GenAI service high latency"
    description: "GenAI service 95th percentile response time is above 10 seconds for 3 minutes."

- alert: GenAIHighErrorRate
  expr: rate(genai_genai_http_exceptions_total{job="genai"}[5m]) / rate(genai_genai_http_requests_total{job="genai"}[5m]) > 0.1
  for: 2m
  labels:
    severity: warning
    service: business
  annotations:
    summary: "GenAI service high error rate"
    description: "GenAI service error rate is above 10% for 2 minutes."
```

#### Info Alerts
```yaml
- alert: LowActiveScreens
  expr: active_screens_count / total_screens_count < 0.5
  for: 10m
  labels:
    severity: info
    service: business
  annotations:
    summary: "Low active screens ratio"
    description: "Less than 50% of screens are active for 10 minutes."

- alert: HighRequestRate
  expr: rate(http_server_requests_total{job="server"}[1m]) > 100
  for: 2m
  labels:
    severity: info
    service: business
  annotations:
    summary: "High request rate"
    description: "Request rate is above 100 requests per minute for 2 minutes."

- alert: FrequentSlideDeckUpdates
  expr: rate(slide_deck_updates_total[5m]) > 10
  for: 2m
  labels:
    severity: info
    service: business
  annotations:
    summary: "Frequent slide deck updates"
    description: "Slide deck updates are happening more than 10 times per 5 minutes."

- alert: FrequentScoreUpdates
  expr: rate(score_updates_total[5m]) > 20
  for: 2m
  labels:
    severity: info
    service: business
  annotations:
    summary: "Frequent score updates"
    description: "Score updates are happening more than 20 times per 5 minutes."
```

## 🔧 AlertManager Configuration

### alertmanager.yml
```yaml
global:
  resolve_timeout: 5m
  slack_api_url: 'https://hooks.slack.com/services/YOUR_SLACK_WEBHOOK'

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
      continue: true
    - match:
        severity: info
      receiver: 'slack-info'
      continue: true

inhibit_rules:
  - source_match:
      severity: 'critical'
    target_match:
      severity: 'warning'
    equal: ['service']

receivers:
  - name: 'slack-notifications'
    slack_configs:
      - channel: '#team-robogo-alerts'
        title: 'Team RoboGo Alert'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
        color: '{{ if eq .CommonLabels.severity "critical" }}danger{{ else if eq .CommonLabels.severity "warning" }}warning{{ else }}good{{ end }}'
        fields:
          - title: 'Severity'
            value: '{{ .CommonLabels.severity }}'
            short: true
          - title: 'Service'
            value: '{{ .CommonLabels.service }}'
            short: true

  - name: 'slack-info'
    slack_configs:
      - channel: '#team-robogo-info'
        title: 'Team RoboGo Info'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'
        color: 'good'

  - name: 'pager-duty-critical'
    pagerduty_configs:
      - service_key: YOUR_PAGERDUTY_SERVICE_KEY
        description: 'Critical alert in Team RoboGo system'
        severity: '{{ if eq .CommonLabels.severity "critical" }}critical{{ else }}warning{{ end }}'

  - name: 'email-notifications'
    email_configs:
      - to: 'team-robogo-alerts@company.com'
        from: 'alertmanager@team-robogo.com'
        smarthost: 'smtp.company.com:587'
        auth_username: 'alertmanager@company.com'
        auth_password: 'YOUR_EMAIL_PASSWORD'
        headers:
          subject: 'Team RoboGo Alert: {{ .CommonLabels.alertname }}'
        html: |
          <h1>Team RoboGo Alert</h1>
          <p><strong>Alert:</strong> {{ .CommonLabels.alertname }}</p>
          <p><strong>Severity:</strong> {{ .CommonLabels.severity }}</p>
          <p><strong>Service:</strong> {{ .CommonLabels.service }}</p>
          <p><strong>Summary:</strong> {{ .CommonAnnotations.summary }}</p>
          <p><strong>Description:</strong> {{ .CommonAnnotations.description }}</p>
```

## 📊 Dashboards

### Available Dashboards

1. **System Overview Dashboard** - High-level system health
2. **Application Performance Dashboard** - Detailed app metrics
3. **Database Performance Dashboard** - Database monitoring
4. **GenAI Service Dashboard** - AI service metrics
5. **Business Metrics Dashboard** - Business KPIs
6. **Infrastructure Dashboard** - System resources

### Dashboard Features

- **Real-time metrics** with 15-30s refresh intervals
- **Interactive panels** with drill-down capabilities
- **Alert integration** with visual indicators
- **Custom business metrics** for domain-specific monitoring
- **Performance optimization** with efficient queries

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
curl http://prometheus:9090/api/v1/targets

# Check AlertManager
curl http://alertmanager:9093/api/v1/alerts

# Check service metrics
curl http://server:8081/actuator/prometheus
```

## 📚 Additional Resources

- [Prometheus Query Language](https://prometheus.io/docs/prometheus/latest/querying/)
- [Grafana Dashboard Documentation](https://grafana.com/docs/grafana/latest/dashboards/)
- [AlertManager Configuration](https://prometheus.io/docs/alerting/latest/configuration/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs) 