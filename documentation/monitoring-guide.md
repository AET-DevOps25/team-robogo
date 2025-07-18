# Team RoboGo Monitoring Guide

This document provides comprehensive monitoring instructions for the Team RoboGo system, including metrics collection, alerting, and observability practices.

## 📊 Monitoring Architecture


## 🎯 Key Metrics

### Application Metrics

#### Backend Service Metrics
```yaml
# Spring Boot Actuator Metrics
application:
  - http_server_requests_total
  - http_server_requests_duration_seconds
  - jvm_memory_used_bytes
  - jvm_gc_collection_seconds
  - process_cpu_usage
  - process_start_time_seconds
```

#### Frontend Metrics
```yaml
# Vue.js Performance Metrics
frontend:
  - page_load_time
  - api_response_time
  - user_interactions
  - error_rate
  - memory_usage
```

#### GenAI Service Metrics
```yaml
# AI Service Metrics
genai:
  - model_inference_time
  - api_request_count
  - token_usage
  - error_rate
  - cache_hit_rate
```

### Infrastructure Metrics

#### Database Metrics
```yaml
# PostgreSQL Metrics
database:
  - postgres_connections_total
  - postgres_queries_total
  - postgres_transactions_total
  - postgres_locks_total
  - postgres_cache_hit_ratio
```

#### Container Metrics
```yaml
# Docker Container Metrics
containers:
  - container_cpu_usage_seconds_total
  - container_memory_usage_bytes
  - container_network_receive_bytes_total
  - container_network_transmit_bytes_total
```

## 📈 Prometheus Configuration

### prometheus.yml
```yaml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

rule_files:
  - "alert.rules.yml"

alerting:
  alertmanagers:
    - static_configs:
        - targets:
          - alertmanager:9093

scrape_configs:
  - job_name: 'spring-boot-app'
    static_configs:
      - targets: ['server:8081']
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s

  - job_name: 'genai-service'
    static_configs:
      - targets: ['genai:8000']
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

  - job_name: 'node-exporter'
    static_configs:
      - targets: ['node-exporter:9100']
    scrape_interval: 30s
```

### Alert Rules (alert.rules.yml)
```yaml
groups:
  - name: application_alerts
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_total{status=~"5.."}[5m]) > 0.1
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value }} errors per second"

      - alert: HighResponseTime
        expr: histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket[5m])) > 2
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High response time detected"
          description: "95th percentile response time is {{ $value }} seconds"

      - alert: HighMemoryUsage
        expr: (jvm_memory_used_bytes / jvm_memory_max_bytes) > 0.85
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "Memory usage is {{ $value | humanizePercentage }}"

  - name: infrastructure_alerts
    rules:
      - alert: DatabaseConnectionHigh
        expr: postgres_connections_total > 80
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High database connections"
          description: "Database has {{ $value }} active connections"

      - alert: ContainerDown
        expr: up == 0
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Container is down"
          description: "Container {{ $labels.instance }} is down"

  - name: ai_service_alerts
    rules:
      - alert: HighAILatency
        expr: histogram_quantile(0.95, rate(genai_request_duration_seconds_bucket[5m])) > 5
        for: 2m
        labels:
          severity: warning
        annotations:
          summary: "High AI service latency"
          description: "AI service 95th percentile latency is {{ $value }} seconds"
```

## 📊 Grafana Dashboards

### Application Dashboard
```json
{
  "dashboard": {
    "title": "Team RoboGo Application Metrics",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_total[5m])",
            "legendFormat": "{{method}} {{status}}"
          }
        ]
      },
      {
        "title": "Response Time",
        "type": "graph",
        "targets": [
          {
            "expr": "histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket[5m]))",
            "legendFormat": "95th percentile"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(http_server_requests_total{status=~\"5..\"}[5m])",
            "legendFormat": "5xx errors"
          }
        ]
      },
      {
        "title": "Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "jvm_memory_used_bytes / jvm_memory_max_bytes",
            "legendFormat": "Memory usage"
          }
        ]
      }
    ]
  }
}
```

### Infrastructure Dashboard
```json
{
  "dashboard": {
    "title": "Team RoboGo Infrastructure",
    "panels": [
      {
        "title": "Database Connections",
        "type": "graph",
        "targets": [
          {
            "expr": "postgres_connections_total",
            "legendFormat": "Active connections"
          }
        ]
      },
      {
        "title": "Container CPU Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "rate(container_cpu_usage_seconds_total[5m])",
            "legendFormat": "{{container_name}}"
          }
        ]
      },
      {
        "title": "Container Memory Usage",
        "type": "graph",
        "targets": [
          {
            "expr": "container_memory_usage_bytes",
            "legendFormat": "{{container_name}}"
          }
        ]
      }
    ]
  }
}
```

## 📝 Logging Configuration

### Logback Configuration (server/src/main/resources/logback-spring.xml)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <springProfile name="!prod">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </springProfile>

    <springProfile name="prod">
        <appender name="JSON" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
                <providers>
                    <timestamp/>
                    <logLevel/>
                    <loggerName/>
                    <message/>
                    <mdc/>
                    <stackTrace/>
                </providers>
            </encoder>
        </appender>
        <root level="INFO">
            <appender-ref ref="JSON"/>
        </root>
    </springProfile>
</configuration>
```

### Loki Configuration (monitoring/loki-config.yml)
```yaml
auth_enabled: false

server:
  http_listen_port: 3100

ingester:
  lifecycler:
    address: 127.0.0.1
    ring:
      kvstore:
        store: inmemory
      replication_factor: 1
    final_sleep: 0s
  chunk_idle_period: 5m
  chunk_retain_period: 30s

schema_config:
  configs:
    - from: 2020-05-15
      store: boltdb-shipper
      object_store: filesystem
      schema: v11
      index:
        prefix: index_
        period: 24h

storage_config:
  boltdb_shipper:
    active_index_directory: /tmp/loki/boltdb-shipper-active
    cache_location: /tmp/loki/boltdb-shipper-cache
    cache_ttl: 24h
    shared_store: filesystem
  filesystem:
    directory: /tmp/loki/chunks

compactor:
  working_directory: /tmp/loki/compactor
  shared_store: filesystem

limits_config:
  enforce_metric_name: false
  reject_old_samples: true
  reject_old_samples_max_age: 168h
```

## 🚨 Alerting Configuration

### AlertManager Configuration (monitoring/alertmanager.yml)
```yaml
global:
  resolve_timeout: 5m
  slack_api_url: 'https://hooks.slack.com/services/YOUR_SLACK_WEBHOOK'

route:
  group_by: ['alertname']
  group_wait: 10s
  group_interval: 10s
  repeat_interval: 1h
  receiver: 'slack-notifications'
  routes:
    - match:
        severity: critical
      receiver: 'pager-duty-critical'
      continue: true

receivers:
  - name: 'slack-notifications'
    slack_configs:
      - channel: '#team-robogo-alerts'
        title: 'Team RoboGo Alert'
        text: '{{ range .Alerts }}{{ .Annotations.summary }}{{ end }}'

  - name: 'pager-duty-critical'
    pagerduty_configs:
      - service_key: YOUR_PAGERDUTY_SERVICE_KEY
        description: 'Critical alert in Team RoboGo system'
```

## 🔧 Monitoring Setup Instructions

### 1. Start Monitoring Stack
```bash
# Start monitoring services
docker-compose -f docker-compose.monitoring.yml up -d

# Verify services are running
docker-compose -f docker-compose.monitoring.yml ps
```

### 2. Access Monitoring Interfaces
```bash
# Prometheus
open http://localhost:9090

# Grafana (admin/admin)
open http://localhost:3000

# AlertManager
open http://localhost:9093

# Loki
open http://localhost:3100
```

### 3. Import Dashboards
```bash
# Import application dashboard
curl -X POST http://admin:admin@localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @monitoring/dashboards/application-dashboard.json

# Import infrastructure dashboard
curl -X POST http://admin:admin@localhost:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -d @monitoring/dashboards/infrastructure-dashboard.json
```

### 4. Configure Alerts
```bash
# Update alert rules
docker-compose -f docker-compose.monitoring.yml exec prometheus \
  curl -X POST http://localhost:9090/-/reload

# Test alert configuration
docker-compose -f docker-compose.monitoring.yml exec alertmanager \
  amtool check-config /etc/alertmanager/alertmanager.yml
```

## 📋 Monitoring Checklist

### Daily Monitoring Tasks
- [ ] Check application response times
- [ ] Review error rates
- [ ] Monitor database performance
- [ ] Verify AI service health
- [ ] Check container resource usage

### Weekly Monitoring Tasks
- [ ] Review alert effectiveness
- [ ] Update dashboard configurations
- [ ] Analyze log patterns
- [ ] Check metric retention policies
- [ ] Validate backup procedures

### Monthly Monitoring Tasks
- [ ] Capacity planning review
- [ ] Performance trend analysis
- [ ] Security audit of monitoring
- [ ] Update monitoring documentation
- [ ] Review alert thresholds

## 🛠️ Troubleshooting

### Common Issues

#### Prometheus Not Scraping
```bash
# Check target status
curl http://localhost:9090/api/v1/targets

# Verify service endpoints
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8000/metrics
```

#### Grafana Dashboard Issues
```bash
# Check data source
curl http://admin:admin@localhost:3000/api/datasources

# Test Prometheus connection
curl http://admin:admin@localhost:3000/api/datasources/proxy/1/api/v1/query?query=up
```

#### AlertManager Not Sending Alerts
```bash
# Check alert configuration
docker-compose exec alertmanager amtool check-config /etc/alertmanager/alertmanager.yml

# Test alert sending
docker-compose exec alertmanager amtool alert --alertmanager.url=http://localhost:9093
```

## 📚 Additional Resources

- [Prometheus Documentation](https://prometheus.io/docs/)
- [Grafana Documentation](https://grafana.com/docs/)
- [Loki Documentation](https://grafana.com/docs/loki/)
- [AlertManager Documentation](https://prometheus.io/docs/alerting/latest/alertmanager/)

This monitoring guide provides comprehensive observability for the Team RoboGo system, ensuring reliable operation and quick issue resolution. 