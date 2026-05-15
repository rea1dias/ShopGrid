# Running Locally

ShopGrid is organized as a Gradle multi-module repository.

Each service is a separate Spring Boot application and should be started independently.

## Current Runnable Services

At the moment, these services are runnable:

- `auth-service` on port `8081`
- `user-service` on port `8082`

## Auth Service

Start PostgreSQL databases:

```powershell
docker compose -f infrastructure/docker-compose/postgres.yml up -d
```

This starts one local PostgreSQL container and creates separate databases for services:

```text
shopgrid_auth
shopgrid_user
shopgrid_product
shopgrid_order
shopgrid_payment
shopgrid_inventory
```

PostgreSQL init scripts run only when the Docker volume is created for the first time.
If the container was already started before this file existed, recreate the volume:

```powershell
docker compose -f infrastructure/docker-compose/postgres.yml down -v
docker compose -f infrastructure/docker-compose/postgres.yml up -d
```

Run tests:

```powershell
.\gradlew.bat :auth-service:test
```

Run the service:

```powershell
.\gradlew.bat :auth-service:bootRun
```

Auth endpoints:

```text
POST http://localhost:8081/api/auth/register
POST http://localhost:8081/api/auth/login
POST http://localhost:8081/api/auth/refresh
POST http://localhost:8081/api/auth/logout
GET  http://localhost:8081/api/auth/me
GET  http://localhost:8081/actuator/health
```

Ready-to-use HTTP requests:

```text
docs/http/auth-service.http
```

## User Service

Run from IntelliJ IDEA:

1. Open the root `ShopGrid` directory in IntelliJ IDEA.
2. Import the project as a Gradle project.
3. Open:

```text
user-service/src/main/java/com/shopgrid/user/UserServiceApplication.java
```

4. Run `UserServiceApplication`.

## Test Endpoint

After the service starts, open:

```text
http://localhost:8082/api/users/status
```

Expected response:

```json
{
  "service": "user-service",
  "status": "UP",
  "timestamp": "2026-05-14T00:00:00Z"
}
```

Actuator health endpoint:

```text
http://localhost:8082/actuator/health
```

## Gradle Wrapper

The project includes Gradle Wrapper files:

```text
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
```

Services can be started from the terminal:

```bash
./gradlew :user-service:bootRun
```

On Windows:

```powershell
.\gradlew.bat :user-service:bootRun
```
