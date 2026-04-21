# Agent Instructions for `auth-api`

This document provides high-signal guidance for OpenCode agents working with the `auth-api` repository.

## Project Overview
*   **Type**: Spring Boot REST API
*   **Build Tool**: Maven
*   **Language**: Java 17
*   **Main Entry Point**: `com.compadres.na.AuthApiApplication`

## Developer Commands

### Build
*   Compile and package the application:
    ```bash
    mvn clean install
    ```

### Run
*   Run the application directly using Maven:
    ```bash
    mvn spring-boot:run
    ```
*   Run the packaged JAR (after `mvn clean install`):
    ```bash
    java -jar target/auth-api-0.0.1-SNAPSHOT.jar
    ```
    *Note: The exact JAR name might include the version (e.g., `auth-api-0.0.1-SNAPSHOT.jar`).*

### Test
*   Run all unit and integration tests:
    ```bash
    mvn test
    ```
*   Test files are located under `src/test/java/`.

## Environment & Setup
*   **Database**: This project uses PostgreSQL. A running PostgreSQL instance is required for full development and testing. Database connection details are typically configured in `application.properties` or `application.yml` within `src/main/resources/`.

## Architectural Notes
*   **Authentication**: The API implements authentication and authorization using Spring Security and JSON Web Tokens (JWT). Key components will involve JWT generation, validation, and security filters.
