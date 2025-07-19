# Team RoboGo Dashboard Configuration

This document provides detailed configuration for all Grafana dashboards used in the Team RoboGo monitoring system.

## 📊 Dashboard Overview

### Dashboard List

1. **System Overview Dashboard** - High-level system health
2. **Application Performance Dashboard** - Detailed app metrics
3. **Database Performance Dashboard** - Database monitoring
4. **GenAI Service Dashboard** - AI service metrics
5. **Business Metrics Dashboard** - Business KPIs
6. **Infrastructure Dashboard** - System resources

## 🎯 Dashboard Configurations

### 1. System Overview Dashboard

**Dashboard ID**: `system-overview`
**Refresh Interval**: 30s
**Time Range**: Last 6 hours

#### Panels Configuration

**Panel 1: System Status Overview**
```json
{
  "title": "System Status Overview",
  "type": "stat",
  "targets": [
    {
      "expr": "up{job=~\"server|gateway|genai\"}",
      "legendFormat": "{{job}}"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "color": {
        "mode": "thresholds"
      },
      "thresholds": {
        "steps": [
          {"color": "red", "value": 0},
          {"color": "green", "value": 1}
        ]
      }
    }
  }
}
```

**Panel 2: Request Rate**
```json
{
  "title": "Request Rate (RPS)",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(http_server_requests_total{job=\"server\"}[5m])",
      "legendFormat": "Server"
    },
    {
      "expr": "rate(gateway_requests_total{job=\"gateway\"}[5m])",
      "legendFormat": "Gateway"
    }
  ],
  "yAxis": {
    "unit": "reqps"
  }
}
```

**Panel 3: Error Rate**
```json
{
  "title": "Error Rate (%)",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(http_server_requests_total{job=\"server\",status=~\"5..\"}[5m]) / rate(http_server_requests_total{job=\"server\"}[5m]) * 100",
      "legendFormat": "Server Errors"
    }
  ],
  "yAxis": {
    "unit": "percent"
  }
}
```

**Panel 4: Response Time**
```json
{
  "title": "Response Time (95th percentile)",
  "type": "timeseries",
  "targets": [
    {
      "expr": "histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job=\"server\"}[5m]))",
      "legendFormat": "Server"
    }
  ],
  "yAxis": {
    "unit": "s"
  }
}
```

**Panel 5: Active Screens**
```json
{
  "title": "Active Screens Count",
  "type": "stat",
  "targets": [
    {
      "expr": "active_screens_count"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "color": {
        "mode": "thresholds"
      },
      "thresholds": {
        "steps": [
          {"color": "red", "value": 0},
          {"color": "yellow", "value": 1},
          {"color": "green", "value": 5}
        ]
      }
    }
  }
}
```

### 2. Application Performance Dashboard

**Dashboard ID**: `app-performance`
**Refresh Interval**: 15s
**Time Range**: Last 1 hour

#### Panels Configuration

**Panel 1: HTTP Request Rate by Endpoint**
```json
{
  "title": "HTTP Request Rate by Endpoint",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(http_server_requests_total{job=\"server\"}[5m]) by (uri)",
      "legendFormat": "{{uri}}"
    }
  ],
  "yAxis": {
    "unit": "reqps"
  }
}
```

**Panel 2: HTTP Response Time by Endpoint**
```json
{
  "title": "HTTP Response Time by Endpoint",
  "type": "timeseries",
  "targets": [
    {
      "expr": "histogram_quantile(0.95, rate(http_server_requests_duration_seconds_bucket{job=\"server\"}[5m])) by (uri)",
      "legendFormat": "{{uri}}"
    }
  ],
  "yAxis": {
    "unit": "s"
  }
}
```

**Panel 3: JVM Memory Usage**
```json
{
  "title": "JVM Memory Usage",
  "type": "timeseries",
  "targets": [
    {
      "expr": "jvm_memory_used_bytes{job=\"server\",area=\"heap\"}",
      "legendFormat": "Heap"
    },
    {
      "expr": "jvm_memory_used_bytes{job=\"server\",area=\"nonheap\"}",
      "legendFormat": "Non-Heap"
    }
  ],
  "yAxis": {
    "unit": "bytes"
  }
}
```

**Panel 4: Business Metrics**
```json
{
  "title": "Business Activity",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(slide_deck_updates_total[5m])",
      "legendFormat": "Slide Deck Updates"
    },
    {
      "expr": "rate(score_updates_total[5m])",
      "legendFormat": "Score Updates"
    },
    {
      "expr": "rate(screen_status_changes_total[5m])",
      "legendFormat": "Screen Status Changes"
    }
  ]
}
```

### 3. Database Performance Dashboard

**Dashboard ID**: `database-performance`
**Refresh Interval**: 30s
**Time Range**: Last 2 hours

#### Panels Configuration

**Panel 1: Database Connections**
```json
{
  "title": "Database Connections",
  "type": "stat",
  "targets": [
    {
      "expr": "pg_stat_database_numbackends{datname=\"robogo_db\"}"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "color": {
        "mode": "thresholds"
      },
      "thresholds": {
        "steps": [
          {"color": "green", "value": 0},
          {"color": "yellow", "value": 20},
          {"color": "red", "value": 50}
        ]
      }
    }
  }
}
```

**Panel 2: Transaction Rate**
```json
{
  "title": "Transaction Rate",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(pg_stat_database_xact_commit{datname=\"robogo_db\"}[5m])",
      "legendFormat": "Commits"
    },
    {
      "expr": "rate(pg_stat_database_xact_rollback{datname=\"robogo_db\"}[5m])",
      "legendFormat": "Rollbacks"
    }
  ]
}
```

**Panel 3: Cache Hit Ratio**
```json
{
  "title": "Cache Hit Ratio",
  "type": "gauge",
  "targets": [
    {
      "expr": "pg_stat_database_blks_hit{datname=\"robogo_db\"} / (pg_stat_database_blks_hit{datname=\"robogo_db\"} + pg_stat_database_blks_read{datname=\"robogo_db\"}) * 100"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "steps": [
          {"color": "red", "value": 0},
          {"color": "yellow", "value": 80},
          {"color": "green", "value": 95}
        ]
      }
    }
  }
}
```

### 4. GenAI Service Dashboard

**Dashboard ID**: `genai-service`
**Refresh Interval**: 15s
**Time Range**: Last 1 hour

#### Panels Configuration

**Panel 1: Request Rate**
```json
{
  "title": "Request Rate",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(genai_genai_http_requests_total[5m])",
      "legendFormat": "Requests/sec"
    }
  ],
  "yAxis": {
    "unit": "reqps"
  }
}
```

**Panel 2: Response Time**
```json
{
  "title": "Response Time",
  "type": "timeseries",
  "targets": [
    {
      "expr": "histogram_quantile(0.95, rate(genai_genai_http_request_duration_seconds_bucket[5m]))",
      "legendFormat": "95th percentile"
    },
    {
      "expr": "histogram_quantile(0.50, rate(genai_genai_http_request_duration_seconds_bucket[5m]))",
      "legendFormat": "50th percentile"
    }
  ],
  "yAxis": {
    "unit": "s"
  }
}
```

**Panel 3: Error Rate**
```json
{
  "title": "Error Rate",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(genai_genai_http_exceptions_total[5m]) / rate(genai_genai_http_requests_total[5m]) * 100",
      "legendFormat": "Error %"
    }
  ],
  "yAxis": {
    "unit": "percent"
  }
}
```

**Panel 4: Requests in Progress**
```json
{
  "title": "Requests in Progress",
  "type": "stat",
  "targets": [
    {
      "expr": "genai_genai_http_requests_in_progress"
    }
  ]
}
```

### 5. Business Metrics Dashboard

**Dashboard ID**: `business-metrics`
**Refresh Interval**: 30s
**Time Range**: Last 24 hours

#### Panels Configuration

**Panel 1: Screen Activity**
```json
{
  "title": "Screen Activity",
  "type": "stat",
  "targets": [
    {
      "expr": "active_screens_count",
      "legendFormat": "Active Screens"
    },
    {
      "expr": "total_screens_count",
      "legendFormat": "Total Screens"
    }
  ]
}
```

**Panel 2: Screen Activity Ratio**
```json
{
  "title": "Screen Activity Ratio",
  "type": "gauge",
  "targets": [
    {
      "expr": "active_screens_count / total_screens_count * 100"
    }
  ],
  "fieldConfig": {
    "defaults": {
      "unit": "percent",
      "min": 0,
      "max": 100,
      "thresholds": {
        "steps": [
          {"color": "red", "value": 0},
          {"color": "yellow", "value": 50},
          {"color": "green", "value": 80}
        ]
      }
    }
  }
}
```

**Panel 3: Business Activity Rate**
```json
{
  "title": "Business Activity Rate",
  "type": "timeseries",
  "targets": [
    {
      "expr": "rate(slide_deck_updates_total[5m])",
      "legendFormat": "Slide Deck Updates"
    },
    {
      "expr": "rate(score_updates_total[5m])",
      "legendFormat": "Score Updates"
    },
    {
      "expr": "rate(screen_status_changes_total[5m])",
      "legendFormat": "Screen Status Changes"
    }
  ]
}
```

**Panel 4: System Counts**
```json
{
  "title": "System Counts",
  "type": "stat",
  "targets": [
    {
      "expr": "total_slide_decks_count",
      "legendFormat": "Slide Decks"
    },
    {
      "expr": "total_teams_count",
      "legendFormat": "Teams"
    },
    {
      "expr": "total_scores_count",
      "legendFormat": "Scores"
    }
  ]
}
```

## 🔧 Dashboard Setup Instructions

### 1. Import Dashboards

```bash
# Import System Overview Dashboard
curl -X POST http://grafana:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d @system-overview-dashboard.json

# Import Application Performance Dashboard  
curl -X POST http://grafana:3000/api/dashboards/db \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -d @app-performance-dashboard.json
```

### 2. Configure Data Sources

Ensure the following data sources are configured in Grafana:

- **Prometheus**: `http://prometheus:9090`
- **Loki**: `http://loki:3100`

### 3. Set Up Variables

Configure the following dashboard variables:

```json
{
  "job": {
    "query": "server,gateway,genai",
    "current": {"value": "server"}
  },
  "instance": {
    "query": "label_values(up, instance)",
    "current": {"value": "server:8081"}
  }
}
```

## 📊 Dashboard Maintenance

### Regular Tasks

1. **Weekly Review**
   - Check dashboard performance
   - Review query efficiency
   - Update thresholds if needed

2. **Monthly Optimization**
   - Optimize PromQL queries
   - Review panel configurations
   - Update dashboard layouts

3. **Quarterly Updates**
   - Add new metrics as needed
   - Update business requirements
   - Review alert thresholds

### Performance Optimization

1. **Query Optimization**
   - Use rate() for counters
   - Limit time ranges appropriately
   - Use recording rules for complex queries

2. **Dashboard Optimization**
   - Limit number of panels per dashboard
   - Use appropriate refresh intervals
   - Optimize legend formats

## 🚨 Dashboard Alerts

### Panel Alerts

Configure alerts on key panels:

1. **Error Rate > 5%**
2. **Response Time > 2s**
3. **Active Screens = 0**
4. **Database Connections > 50**

### Alert Configuration

```json
{
  "alert": {
    "name": "High Error Rate",
    "message": "Error rate is above 5%",
    "conditions": [
      {
        "type": "query",
        "query": {
          "params": ["A", "5m", "now"]
        },
        "reducer": {
          "type": "avg",
          "params": []
        },
        "evaluator": {
          "type": "gt",
          "params": [0.05]
        }
      }
    ]
  }
}
```

## 📚 Additional Resources

- [Grafana Dashboard API](https://grafana.com/docs/grafana/latest/http_api/dashboard/)
- [PromQL Best Practices](https://prometheus.io/docs/prometheus/latest/querying/best_practices/)
- [Grafana Panel Types](https://grafana.com/docs/grafana/latest/panels/) 