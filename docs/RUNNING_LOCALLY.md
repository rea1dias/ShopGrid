# Running Locally

ShopGrid is organized as a Gradle multi-module repository.

Each service is a separate Spring Boot application and should be started independently.

## Current Runnable Service

At the moment, `user-service` is the first runnable service.

It uses port `8082`.

## Run From IntelliJ IDEA

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

The project should eventually include Gradle Wrapper files:

```text
gradlew
gradlew.bat
gradle/wrapper/gradle-wrapper.jar
gradle/wrapper/gradle-wrapper.properties
```

After Gradle Wrapper is added, services can be started from the terminal:

```bash
./gradlew :user-service:bootRun
```

On Windows:

```powershell
.\gradlew.bat :user-service:bootRun
```
