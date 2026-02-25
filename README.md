# bpoc-api — DevOps/Developer Runbook 🥷

A lightweight Spring Boot 3 API that **proxies a free Hero/Superhero dataset** and exposes:

- **Business API** under `/api/v1/...`
- **Swagger UI** at `/api/docs`
- **Actuator + metrics** under `/api/*` (health/info/metrics/prometheus)
- **Unit tests (JUnit 5)** + **coverage (JaCoCo)** with an optional coverage gate

---

## What this service does

### Business endpoint: Hero by ID

The service calls an upstream free hero API (Akabab superhero dataset served via jsDelivr CDN) and returns the JSON payload via our own endpoint.

- **Upstream base URL (CDN):**
  - `https://cdn.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api`

- **Upstream path used by the service:**
  - `/id/{id}.json`

- **Our endpoint (example):**
  - `GET http://localhost:8080/api/v1/heroes/1`

> The upstream API is unauthenticated, public, and intended for demos/POCs. Treat it as **best-effort** (it can be slow, rate-limited, or unavailable).

---

## Local run

### Requirements

- **Java:** 21
- **Maven:** 3.9+
- **Network:** outbound HTTPS allowed (to jsDelivr)

### Run the service

```bash
mvn spring-boot:run
```

### Stop the service

If started from terminal:

```bash
# press
Ctrl + C
```

---

## API endpoints

### 1) Business API (Hero)

- **Get hero by id**
  - `GET /api/v1/heroes/{id}`
  - Example:
    ```bash
    curl -s http://localhost:8080/api/v1/heroes/1 | jq
    ```

### 2) API Documentation (Swagger UI)

Swagger UI is served via **springdoc-openapi**.

- **Swagger UI**
  - `GET /api/docs`
  - Example: open in browser
    - `http://localhost:8080/api/docs`

- **OpenAPI spec**
  - JSON: `GET /v3/api-docs`
  - YAML: `GET /v3/api-docs.yaml`

> Note: `/v3/api-docs*` remain on the default springdoc routes unless you explicitly remap them.

### 3) Observability & Health (Actuator)

Actuator endpoints are exposed under `/api` (not `/actuator`) to keep everything under an API-like base path.

- **Health**
  - `GET /api/health`
  - Example:
    ```bash
    curl -s http://localhost:8080/api/health | jq
    ```

- **Info**
  - `GET /api/info`

- **Metrics**
  - `GET /api/metrics`
  - Example (list metric names):
    ```bash
    curl -s http://localhost:8080/api/metrics | jq
    ```

- **Prometheus scrape endpoint**
  - `GET /api/prometheus`
  - Example:
    ```bash
    curl -s http://localhost:8080/api/prometheus | head
    ```

---

## Metrics: execution time for GET hero by id

### HTTP server request timing

Spring Boot + Micrometer records request timing via `http.server.requests`.

Percentiles/histograms are enabled for this metric:

- p50, p90, p95, p99

Example query (after generating traffic):

```bash
curl -s "http://localhost:8080/api/metrics/http.server.requests?tag=method:GET" | jq
```

To filter by endpoint, try:

```bash
curl -s "http://localhost:8080/api/metrics/http.server.requests?tag=method:GET&tag=uri:/api/v1/heroes/{id}" | jq
```

> Depending on MVC mapping, the `uri` tag may appear as templated (`/api/v1/heroes/{id}`), raw, or `UNKNOWN`. If filtering yields nothing, query without tags first, inspect `availableTags`, then apply the tags returned by Actuator.

### External call timing (upstream hero API)

If the code includes a custom Micrometer `Timer` around the upstream call (recommended), you will also see a metric like:

- `hero.api.calls`

Example:

```bash
curl -s http://localhost:8080/api/metrics/hero.api.calls | jq
```

This metric helps answer:

- “How long do we spend calling the upstream hero service?”
- “Are slow responses due to us or the external dependency?”

---

## Configuration (`application.yml`) — what each key does

```yaml
spring:
  application:
    name: bpoc-api
  profiles:
    active: local

server:
  port: 8080

logging:
  level:
    root: INFO
    com.victorprocopio: DEBUG

hero:
  api:
    base-url: https://cdn.jsdelivr.net/gh/akabab/superhero-api@0.3.0/api

springdoc:
  swagger-ui:
    path: /api/docs

management:
  endpoints:
    web:
      base-path: /api
      exposure:
        include: health,info,metrics,prometheus
  observations:
    http:
      server:
        requests:
          enabled: true
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
      percentiles:
        http.server.requests: 0.5,0.9,0.95,0.99
```

### Breakdown

- `spring.application.name`: logical service name (useful for logs/metrics)
- `spring.profiles.active`: selects the active Spring profile (defaults to `local`)
- `server.port`: HTTP port (8080)
- `logging.level.*`:
  - `root: INFO` keeps logs sane
  - `com.victorprocopio: DEBUG` increases verbosity for our code only
- `hero.api.base-url`: upstream hero dataset base URL (CDN)
- `springdoc.swagger-ui.path`: Swagger UI mounted at `/api/docs`
- `management.endpoints.web.base-path`: Actuator base moved from `/actuator` to `/api`
- `management.endpoints.web.exposure.include`: only expose selected endpoints
- `management.observations.http.server.requests.enabled`: ensures HTTP observation instrumentation is enabled
- `management.metrics.distribution.*`: enables histograms + percentiles for `http.server.requests`

---

## Build & dependencies (Maven / `pom.xml`)

### Key versions / platform

- Spring Boot parent: **3.5.11**
- Java: **21**

### Main dependencies

- `spring-boot-starter-web` — Spring MVC REST API
- `spring-boot-starter-validation` — Bean Validation (Jakarta Validation)
- `spring-boot-starter-actuator` — health/metrics endpoints
- `springdoc-openapi-starter-webmvc-ui: 2.8.15` — Swagger UI + OpenAPI for Spring MVC
- `lombok` — optional compile-time boilerplate reduction

### Testing & coverage

- `spring-boot-starter-test` — includes **JUnit 5**, Mockito, AssertJ, Spring Test

#### Run unit tests

```bash
mvn test
```

#### Generate coverage report (JaCoCo)

JaCoCo runs automatically with `test` and generates:

- `target/site/jacoco/index.html`

Open it (macOS):

```bash
open target/site/jacoco/index.html
```

#### Enforce minimum coverage (coverage gate)

The JaCoCo `check` execution runs on `verify` and fails the build if coverage is below:

- **60% INSTRUCTION covered ratio**

Run with gate:

```bash
mvn verify
```

> If you want to temporarily disable the gate, you can run just `mvn test`, or remove/adjust the JaCoCo `check` rule.

---

## Smoke testing via VS Code `test.http`

If using the VS Code “REST Client” extension, add a `test.http` file like:

```http
### Hero by ID (our API)
GET http://localhost:8080/api/v1/heroes/1
Accept: application/json

###

### Swagger UI (open in browser)
GET http://localhost:8080/api/docs

###

### Actuator Health
GET http://localhost:8080/api/health
Accept: application/json

###

### Metrics list
GET http://localhost:8080/api/metrics
Accept: application/json
```

---

## Notes for DevOps / production hardening

For a POC, exposing metrics under `/api` is convenient. For production:

- Consider **separate management port** (e.g., `management.server.port=8081`)
- Restrict Actuator exposure (e.g., only `health` externally)
- Add authentication/authorization for management endpoints
- Add timeouts/retries/circuit breaker for the upstream hero dependency (Resilience4j)
- Add structured logging + correlation IDs (traceability)

---

## Quick reference URLs (local)

- Business API:
  - `GET http://localhost:8080/api/v1/heroes/{id}`
- Docs:
  - `http://localhost:8080/api/docs`
- Actuator:
  - `http://localhost:8080/api/health`
  - `http://localhost:8080/api/metrics`
  - `http://localhost:8080/api/prometheus`
