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

| Variable                   | Type   | Default                                  | Required | Description                    |
|----------------------------|--------|------------------------------------------|----------|--------------------------------|
| ACTIVE_PROFILE             | String | local                                    | no       | Active project profile         |
| JWT_SECRET                 | String | a-very-long-test-secret-value-1234567890 | no       | Secret key for JWT signing     |
| JWT_EXPIRATION_IN_MILLIS   |  Long  | 3600000                                  | no       | Token validity in milliseconds |

### Production

| Variable                   | Type   | Default      | Required                                       | Description                               |
|----------------------------|--------|--------------|------------------------------------------------|-------------------------------------------|
| ACTIVE_PROFILE             | String | local        | yes (Missing value forces non-production mode) | Active profile (e.g., `prod`)             |
| JWT_SECRET                 | String | **_n/a_**    | yes                                            | MUST be a secure, unpredictable value     |
| JWT_EXPIRATION_IN_MILLIS   | Long   | **_n/a_**    | yes                                            | Must follow company security guidelines   |

## Tests

The project includes a complete test suite for authentication, including:
- JWT generation and validation
- Secret strength checks
- Expiration handling
- Issuer validation
- Invalid token scenarios

To run the tests:

```bash 
mvn test
```
## Authentication Flow

The server uses a dual-token authentication model composed of access tokens (JWT) and refresh tokens, supported by a persistent session registry.

### Access Token (JWT)

- Generated at login.
- Contains only essential claims (issuer, subject, issued-at, expiration).
- Signed with HMAC512 using JWT_SECRET.
- Validation checks:
  - Signature correctness
  - Issuer (talos)
  - Expiration (exp)
  - Subject (sub) in UUID format

All expiration logic relies exclusively on JWT data. No database fields are used to determine token validity.

### Refresh Token

- Random, opaque string.
- Stored in the database as part of an AuthSession record.
- Linked to a single userId.
- Not parsed or interpreted by the server, only matched against stored values.

### Session Registry

Each login issues a new AuthSession entry with:

- user reference
- refreshToken
- lastUsedAt timestamp
  
The server does not maintain expiration logic on the database.

A refresh token is treated as valid while it exists and matches userId + refreshToken for an active session.

### Refresh Flow

1. Client sends the refresh token.

2. Server looks up the matching session.

3. If found:
   - A new access token is generated.
   - lastUsedAt is updated.

4. If not found:
   - The refresh attempt is rejected.

The refresh token itself has no cryptographic expiration; its lifecycle is controlled by session persistence (e.g., user logout, rotation, or administrative invalidation).