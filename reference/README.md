# Reference solution — read this first

**This is the finished project. It contains the answer to every task in the curriculum, including all tests.**

Use it as a last resort only, after you have worked through the hints of a lesson and tried a Copilot conversation. Looking at it early takes away exactly the part of the lesson that teaches you something. If you must look, look for one thing (one annotation, one test name), close it, and write the code yourself.

It is the state at the **end of Lesson 19**, not a snapshot per lesson. Opening it for an early lesson also shows you code from later lessons.

## What is in here

| Path | What |
|---|---|
| `src/main/java/alpacas/` | the domain (`Alpaca`, `Herd`, `Pen`, ...), the `PenRepository` interface and the in-memory and JDBC implementations |
| `src/main/java/alpacas/web/` | controllers, `WebMapping`, `ApiExceptionHandler`, `ShearingService` |
| `src/main/resources/api/openapi.yaml` | the API contract (Lesson 19); the interfaces and records in `alpacas.web.api` and `alpacas.web.model` are generated from it at build time |
| `src/main/resources/application.properties`, `schema.sql` | PostgreSQL settings and the tables |
| `src/test/java/` | all tests, mirroring the packages |
| `compose.yml` | PostgreSQL in a container (user, password and database `farm`, for local use only) |

## Run it

Java 25 and Maven are needed.

```bash
mvn test                        # all tests; they use an embedded H2 database, no container needed
podman compose up -d            # PostgreSQL for the real application
mvn spring-boot:run             # starts on http://localhost:8080
```

In your IDE, reload the Maven project after the first build so it sees `target/generated-sources`.
