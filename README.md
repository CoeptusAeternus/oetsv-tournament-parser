# OeTSV Tournament Parser

This service parses ÖTSV tournament calendar and detail pages and exposes the data as a small Spring Boot REST API.

It provides:

- `GET /health` for a quick parser health check
- `GET /tournaments` for the current tournament list
- `GET /tournaments/{tournamentId}` for a single tournament by ID

The application also exposes OpenAPI metadata and Swagger UI for interactive API exploration.

## How To Run The App

### Local development

Requirements:

- Java 21
- A shell with execute permission for `gradlew`

Start the app with:

```bash
./gradlew bootRun
```

By default the app listens on port `12001`.

### Run the packaged JAR

Build the application:

```bash
./gradlew bootJar
```

Then run the generated JAR:

```bash
java -jar build/libs/*.jar
```

## Runtime Config Options

These runtime settings are available through `src/main/resources/application.properties`:

- `SERVER_PORT` sets the HTTP port. Default: `12001`.
  - Spring resolves this into `server.port=${SERVER_PORT:12001}`.
- `logging.level.root` controls root log verbosity. Default: `INFO`.
- `logging.level.ch.seiberte.tournamentParser` controls application package logging. Default: `INFO`.

Example:

```bash
SERVER_PORT=8080 ./gradlew bootRun
```

The OpenAPI definition advertises `http://localhost:{serverPort}` as the primary server URL, with `serverPort` defaulting to `12001` and matching the `SERVER_PORT` environment variable. If you change the port, Swagger UI will show the same value.

## Docker Containers

### Application container

The top-level `Dockerfile` builds a two-stage image:

1. A Gradle build stage compiles the app and creates the boot JAR.
2. A slim Eclipse Temurin 21 JRE stage runs the JAR.

Build the image:

```bash
docker build -t tournament-parser .
```

Run the container:

```bash
docker run --rm -p 12001:12001 -e SERVER_PORT=12001 tournament-parser
```

If you want a different host port, keep the container port aligned with `SERVER_PORT`:

```bash
docker run --rm -p 8080:8080 -e SERVER_PORT=8080 tournament-parser
```

### Dev container

This repository includes a VS Code Dev Container in [`.devcontainer/devcontainer.json`](.devcontainer/devcontainer.json).

To use it:

1. Open the repository in VS Code.
2. Choose `Dev Containers: Reopen in Container`.
3. Wait for the container to build and the post-create task to finish.

The dev container:

- Uses a Java 21 Bookworm base image
- Forwards port `12001`
- Installs Java/Spring Boot tooling extensions
- Runs `./gradlew build --no-daemon` after creation

## How To Run Tests

Run the test suite with:

```bash
./gradlew test
```

The project uses JUnit Platform through Spring Boot’s test starter.

## API Docs And Swagger UI

Once the API is running, the documentation is available at:

- Swagger UI: `http://localhost:12001/swagger-ui/index.html`
- OpenAPI spec: `http://localhost:12001/v3/api-docs`

If you run the app on a different port, replace `12001` with your configured `SERVER_PORT` value. In Swagger UI, the advertised server entry will use the same port value via the OpenAPI server variable.