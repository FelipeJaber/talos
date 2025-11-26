## Stack
- Java 21
- Spring Boot 4
- Docker / Docker Compose
- H2 (local)
- Logback

## How to run
1. Copy the `.env.example` file to `.env`
2. Fill all required environment variables
3. Run:

   docker-compose up --build

## Profiles
- local (default)
- prod

## Default endpoints
- `http://localhost:8080/api` — Base path for all APIs
- `http://localhost:8080/swagger` — Swagger UI

## Environment Variables

Below are required variables per environment.

### Local / Development

| Variable                   | Type   | Default              | Required | Description                    |
|----------------------------|--------|----------------------|----------|--------------------------------|
| ACTIVE_PROFILE             | String | local                | no       | Active project profile         |
| JWT_SECRET                 | String | dev-secret-test-only | no       | Secret key for JWT signing     |
| JWT_EXPIRATION_IN_MILLIS   |  Long  | 3600000              | no       | Token validity in milliseconds |

### Production

| Variable                   | Type   | Default      | Required                                       | Description                               |
|----------------------------|--------|--------------|------------------------------------------------|-------------------------------------------|
| ACTIVE_PROFILE             | String | local        | yes (Missing value forces non-production mode) | Active profile (e.g., `prod`)             |
| JWT_SECRET                 | String | **_n/a_**    | yes                                            | MUST be a secure, unpredictable value     |
| JWT_EXPIRATION_IN_MILLIS   | Long   | **_n/a_**    | yes                                            | Must follow company security guidelines   |
