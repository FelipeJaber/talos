## Talos — Authentication API (Spring Boot)

### Overview
Talos is a Spring Boot authentication API that implements an access/refresh token model backed by a session registry. It exposes endpoints to register users, set passwords, login, refresh access tokens, and logout. Local development runs on H2 (file) database; production is intended to use PostgreSQL (driver present), though production DB configuration is not included in this repository.

### Stack
- Java 21
- Spring Boot 4.0.0
- Spring Security, Spring WebMVC/WebFlux (WebMVC is the UI/documentation entrypoint; WebFlux is also present)
- Spring Data JPA
- H2 (local development)
- PostgreSQL driver (runtime, for production)
- springdoc-openapi (API docs)
- Logback
- Build tool: Maven (wrapper included: `mvnw`)

### Requirements
- JDK 21+
- Maven 3.9+ (or use `./mvnw` wrapper)
- Optional: Docker (no ready Compose/Dockerfile in this repo — see TODOs)

### Project Profiles
- `local` (default; activated when `ACTIVE_PROFILE` is not set) — uses H2 file DB and runs on port 8083
- `prod` — reads JWT settings from environment; production DB is not configured here

Profiles are selected via `application.properties` with `spring.profiles.active=${ACTIVE_PROFILE:local}`.

### How to Run (Local)
1) Ensure JDK 21 is installed
2) Start the app

Using Maven wrapper:
```bash
./mvnw spring-boot:run
```

Using Maven directly:
```bash
mvn spring-boot:run
```

Build a runnable jar:
```bash
./mvnw -DskipTests package
java -jar target/talos-0.0.1-SNAPSHOT.jar
```

Server will start on:
- Local profile: `http://localhost:8083`

H2 console (local only): `http://localhost:8083/h2-console`

### Default Endpoints
- API base path (secured): `/api/**`
- Auth endpoints (controller base): `/api/auth/v1`
  - `POST /api/auth/v1/login`
  - `POST /api/auth/v1/register`
  - `POST /api/auth/v1/set-password/{token}`
  - `GET  /api/auth/v1/refresh` (expects `Authorization: Bearer <refreshToken>`)
  - `POST /api/auth/v1/logout` (expects `Authorization` header)

OpenAPI/Swagger (local):
- API docs: `http://localhost:8083/api-docs`
- Swagger UI: `http://localhost:8083/api/swagger`

Note: Security configuration permits access to the OpenAPI endpoints above without authentication in development. All other `/api/**` endpoints require authentication and (as currently configured) `ROLE_ADMIN`.

### Environment Variables

Below are environment variables read by the application. In `local` they have safe defaults; in `prod` they must be provided.

Local / Development
| Variable                 | Type  | Default                                  | Required | Description                              |
|--------------------------|-------|------------------------------------------|----------|------------------------------------------|
| ACTIVE_PROFILE           | str   | local                                    | no       | Active profile                           |
| JWT_SECRET               | str   | a-very-long-test-secret-value-1234567890 | no       | Secret for signing JWTs                  |
| JWT_EXPIRATION_IN_MILLIS | long  | 3600000                                  | no       | Access token validity in milliseconds    |

Production
| Variable                 | Type | Default | Required | Description                                           |
|--------------------------|------|---------|----------|-------------------------------------------------------|
| ACTIVE_PROFILE           | str  | —       | yes      | Should be set to `prod`                               |
| JWT_SECRET               | str  | —       | yes      | Strong, unpredictable secret                          |
| JWT_EXPIRATION_IN_MILLIS | long | —       | yes      | Must follow your security policy                      |

Database configuration for production is not present in this repository. You will need to supply `spring.datasource.*` and related settings via properties or environment when running with `prod`.

### Scripts and Common Commands
- Run app (dev): `./mvnw spring-boot:run`
- Run tests: `./mvnw test`
- Build jar: `./mvnw -DskipTests package`
- Format/verify (if applicable): use your IDE or Maven plugins you add

### Tests
This project includes tests for authentication and application context:
- JWT generation/validation
- Expiration handling
- Invalid token scenarios

Run:
```bash
./mvnw test
```

### Authentication Flow
The server uses a dual-token authentication model composed of access tokens (JWT) and refresh tokens, supported by a persistent session registry.

Access Token (JWT)
- Generated at login
- Contains essential claims (issuer, subject, issued-at, expiration)
- Signed with HMAC512 using `JWT_SECRET`
- Validation checks include signature, issuer (`talos`), expiration (`exp`), and UUID-formatted subject (`sub`)

Refresh Token
- UUID-formatted string
- Stored in the database as part of an `AuthSession` record
- Linked to a single `userId`
- Not parsed by the server; compared as an opaque value against stored value

Session Registry
Each login issues a new `AuthSession` entry with:
- user reference
- refreshToken
- lastUsedAt timestamp

The server does not maintain DB-driven expiration; refresh token validity is determined by session presence/match (logout/rotation/administrative invalidation ends it).

Refresh Flow
1. Client sends the refresh token
2. Server looks up the matching session
3. If found, a new access token is generated and `lastUsedAt` is updated
4. If not found, the refresh attempt is rejected

### Project Structure
```
.
├── pom.xml
├── mvnw / mvnw.cmd
├── src
│  ├── main
│  │  ├── java/com/felipejaber/talos/...
│  │  │  ├── TalosApplication.java               # Entry point
│  │  │  ├── presentation/controller/...         # REST controllers
│  │  │  ├── application/...                     # Services, DTOs, mappers
│  │  │  ├── data/...                            # Entities, repositories, enums
│  │  │  └── infra/config/security/...           # Security (JWT, filters, config)
│  │  └── resources
│  │     ├── application.properties              # profile selector
│  │     ├── application-local.properties        # local settings (H2, port 8083)
│  │     ├── application-prod.properties         # prod JWT settings (no DB cfg)
│  │     └── logback-spring.xml                  # logging
│  └── test/java/com/felipejaber/talos/...       # tests (JWT, context)
└── data/local-db.*                               # H2 files (created locally)
```

### Deployment
- Maven build produces a runnable jar: `target/talos-0.0.1-SNAPSHOT.jar`
- Java run example: `java -jar target/talos-0.0.1-SNAPSHOT.jar`

### Docker / Compose
- NOTE: `Dockerfile` and `docker-compose.yaml` are present but empty in this repository at the time of writing (2025-12-10).
- TODO: Provide a working `Dockerfile` and `docker-compose.yaml`, or remove these files if containerization is not intended.

### License
No license information is declared in `pom.xml` or this repository.
- TODO: Add a LICENSE file and update `pom.xml` license metadata accordingly.