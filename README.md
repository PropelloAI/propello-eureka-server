# Propello Eureka Server

Netflix Eureka service registry for Propello microservices architecture.

## Overview

This is a standalone service discovery server that allows microservices to:
- Register themselves on startup
- Discover other services dynamically
- Load balance across multiple instances
- Monitor service health with automatic failover

## Quick Start

### Run Locally

```bash
./gradlew bootRun
```

The Eureka dashboard will be available at: http://localhost:8761

### Build

```bash
./gradlew build
```

### Run with Docker

```bash
docker build -t propello-eureka-server .
docker run -p 8761:8761 propello-eureka-server
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `EUREKA_PORT` | Server port | `8761` |
| `EUREKA_HOSTNAME` | Server hostname | `localhost` |
| `EUREKA_SELF_PRESERVATION` | Enable self-preservation mode | `false` (dev), `true` (prod) |

### Profiles

**Local Development (default)**
```bash
./gradlew bootRun
```
- Standalone mode
- Self-preservation disabled
- Fast eviction for quick testing

**Production**
```bash
./gradlew bootRun --args='--spring.profiles.active=production'
```
- Self-preservation enabled
- Optimized logging

**Cluster Mode (High Availability)**
```bash
EUREKA_PEER_URLS=http://eureka-2:8761/eureka/ ./gradlew bootRun --args='--spring.profiles.active=cluster'
```
- Multiple Eureka servers peer with each other
- Automatic replication
- Improved availability

## Registering Services

Services register with Eureka using the Spring Cloud Netflix Eureka Client dependency.

### 1. Add Dependency

```kotlin
dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
}
```

### 2. Configure Service

```yaml
spring:
  application:
    name: my-service

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    preferIpAddress: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30
```

### 3. Enable Eureka Client

```kotlin
@SpringBootApplication
@EnableDiscoveryClient  // Optional in newer versions
class MyServiceApplication
```

## Monitoring

### Eureka Dashboard

Access the web dashboard at: http://localhost:8761

Features:
- List of all registered services
- Instance status and health
- Replica information
- General server info

### Actuator Endpoints

- Health: http://localhost:8761/actuator/health
- Metrics: http://localhost:8761/actuator/metrics
- Eureka: http://localhost:8761/actuator/eureka

### REST API

Get all registered services:
```bash
curl http://localhost:8761/eureka/apps
```

Get specific service:
```bash
curl http://localhost:8761/eureka/apps/MY-SERVICE
```

## Service Discovery Usage

Services can discover each other using:

**1. Load Balanced RestTemplate**
```kotlin
@LoadBalanced
@Bean
fun restTemplate(): RestTemplate = RestTemplate()

// Usage
val response = restTemplate.getForObject(
    "http://user-management-service/api/v1/users/123",
    User::class.java
)
```

**2. WebClient with Load Balancer**
```kotlin
@Bean
fun webClient(builder: WebClient.Builder): WebClient {
    return builder.build()
}

// Usage
webClient.get()
    .uri("http://user-management-service/api/v1/users/123")
    .retrieve()
    .bodyToMono(User::class.java)
```

**3. Spring Cloud Gateway**
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-management
          uri: lb://user-management-service  # lb:// prefix for load balancing
          predicates:
            - Path=/api/v1/users/**
```

## Architecture

```
┌─────────────────┐
│ Eureka Server   │
│   Port: 8761    │
└────────┬────────┘
         │
    Registration
      & Discovery
         │
    ┌────┴────┬────────────┬─────────────┐
    │         │            │             │
┌───▼────┐ ┌──▼──────┐ ┌──▼──────┐  ┌───▼─────┐
│Gateway │ │User-Mgmt│ │Chat-Agt │  │Auth-Svc │
│ :8080  │ │  :8081  │ │  :8082  │  │  :8083  │
└────────┘ └─────────┘ └─────────┘  └─────────┘
```

## High Availability Setup

For production, run multiple Eureka servers:

```yaml
# Eureka Server 1
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-2:8761/eureka/,http://eureka-3:8761/eureka/

# Eureka Server 2
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-1:8761/eureka/,http://eureka-3:8761/eureka/

# Eureka Server 3
eureka:
  client:
    serviceUrl:
      defaultZone: http://eureka-1:8761/eureka/,http://eureka-2:8761/eureka/
```

## Troubleshooting

### Services not appearing in dashboard
- Check network connectivity to Eureka server
- Verify `eureka.client.serviceUrl.defaultZone` is correct
- Check application logs for registration errors
- Ensure service has started successfully

### Red message about self-preservation mode
- Normal in development when few services are running
- Can disable with `eureka.server.enable-self-preservation: false`
- In production, this is a safety feature and should stay enabled

### Services taking time to appear
- Default registration interval is 30 seconds
- Reduce with `eureka.instance.lease-renewal-interval-in-seconds: 10`
- Response cache update interval is 30 seconds by default

## Dependencies

- Spring Boot 3.5.6
- Spring Cloud 2024.0.0
- Netflix Eureka Server
- Kotlin 2.1.20
- Java 21

## Integration with Propello Platform

This service uses the propello-platform BOM for centralized dependency management:
- Consistent Spring Boot and Spring Cloud versions
- Managed Kotlin version
- Shared build conventions

## License

Proprietary - Propello AI
