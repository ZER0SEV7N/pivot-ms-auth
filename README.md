# Pivot Banking System - Authentication Microservice (`pivot-ms-auth`)

## Overview
Reactive authentication and authorization microservice responsible for user registration, credential verification, and **stateless HS256 JWT issuance and validation** for the **Pivot Banking System** (Phase III). It secures credentials using **BCrypt password hashing**, assigns **RBAC roles** (`ROLE_USER`, `ROLE_ADMIN`), signs tokens with secrets managed in **HashiCorp Vault** (`secret/pivot-bank`), and operates as a fully autonomous service with **zero outbound REST calls** to other microservices.

## Architecture, Design Patterns & Quality Gates
- **Architectural Style:** Hexagonal Architecture (Ports and Adapters) with strict separation of `domain`, `application`, and `infrastructure` layers, developed **API-First** from `src/main/resources/openapi/auth-api.yml`.
- **Reactive Stack:** Java 17 + Spring Boot `4.1.2` + Spring Cloud `2025.1.4` + Spring WebFlux (Netty) + **RxJava 3 (`3.1.12`)** (`Single`, `Maybe`) + `reactor-adapter` + Reactive Spring Data MongoDB + Netflix Eureka Client + JJWT (`0.12.6`) + `spring-security-crypto` (BCrypt).
- **SOLID & Design Patterns (Zero God Classes):**
  - **Single Responsibility Principle (SRP):** Dedicated reactive use cases (`RegisterUserUseCaseImpl`, `LoginUserUseCaseImpl`, `ValidateTokenUseCaseImpl`) acting as pure orchestrators.
  - **Domain Support Separation:** `AuthUserDomainInitializer` encapsulates default role assignment, BCrypt hashing coordination, and audit timestamp initialization outside the use case.
  - **Clean Outbound Ports:** `AuthUserRepositoryPort`, `PasswordEncoderPort` (`BcryptPasswordEncoderAdapter`), and `JwtTokenPort` (`JjwtTokenAdapter` emitting RFC 7519 numeric `iat`/`exp` claims).
- **Code Quality & Testing:**
  - **Google Java Style (`maven-checkstyle-plugin`):** Enforced during Maven `validate` phase.
  - **Unit Testing (`JUnit 5` + `Mockito` + `RxJava 3 TestObserver` + `StepVerifier`):** AAA pattern and FIRST principles achieving **100% instruction coverage (604/604) and 100% branch coverage (18/18)** verified by **JaCoCo (`0.8.15`)** (minimum gate: 90%).

## Security & Secrets (`HashiCorp Vault`)
- **Vault Path:** `secret/pivot-bank` (configured as `optional:vault://` with Config Server fallback for local/cluster resilience).
- **Required Secret Keys:**
  - `jwt-secret-key`: Base64-encoded HMAC-SHA256 signing key shared with `ms-gateway`.
  - `jwt-expiration-ms`: Token validity window in milliseconds (default: `3600000` ms / 1 hour).
- **Password Storage:** Passwords are never stored or logged in plain text; `BcryptPasswordEncoderAdapter` hashes and verifies credentials using `BCryptPasswordEncoder`.
- **Gateway RBAC Integration:** `ms-gateway` (`:8080`) allows public access to `/api/v1/auth/**`, verifies the HS256 signature on all protected routes, enforces `ROLE_ADMIN` on sensitive operations (`DELETE`, `/reactivate`, `/reverse`), and propagates `X-Auth-User`, `X-Auth-Role`, and `X-Auth-Customer-Id` headers downstream.

## API Endpoints (`/api/v1/auth`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/register` | Public | Register a new user account (`ROLE_USER` or `ROLE_ADMIN`, optional `customerId`) and return a signed JWT (`201 Created`) |
| `POST` | `/api/v1/auth/login` | Public | Validate credentials against BCrypt hash and active status, issuing a fresh Bearer JWT (`200 OK`) |
| `GET` | `/api/v1/auth/validate` | Bearer JWT | Validate an `Authorization: Bearer <token>` header and return decoded claims (`valid`, `username`, `role`, `expiresAt`) |

## Inter-Service Communication & Observability
- **`pivot-ms-config` (`:8888`):** Fetches externalized YAML configuration at startup (`optional:configserver:${CONFIG_SERVER_URI:http://localhost:8888}`).
- **`pivot-ms-eureka` (`:8761`):** Registers service instance as `MS-AUTH` for dynamic discovery.
- **`pivot-ms-gateway` (`:8080`):** Exposes `/api/v1/auth/**` publicly and shares the JWT signing secret to validate tokens at the edge.
- **Zero Outbound REST Calls:** Strictly autonomous per Phase III architectural rules; never calls `ms-customer` or any other domain microservice over HTTP.
- **Observability:** Exposes Spring Boot Actuator health probes (`/actuator/health/liveness`, `/actuator/health/readiness`) and ships structured logs via **Promtail -> Loki -> Grafana (`:3000`)**.

## Database (Database per Service Pattern)
- **Engine:** MongoDB 7.0 (Reactive Spring Data MongoDB using derived query methods, zero `@Query`, configured via `spring.mongodb.uri`).
- **Database Name:** `db_pivot_auth`
- **Collection:** `auth_users` (`AuthUserDocument` with unique `username` and `email` validation).

## How to Run (Local, Docker & Kubernetes)
1. Ensure MongoDB (`:27017`), HashiCorp Vault (`:8200`), `ms-config` (`:8888`), and `ms-eureka` (`:8761`) are running.
2. Run unit tests, Checkstyle validation, and generate the JaCoCo coverage report:
   ```powershell
   mvn clean test checkstyle:check jacoco:report
   ```
3. Start the microservice locally (port `8085`):
   ```powershell
   mvn spring-boot:run
   ```
4. Build and run as a Docker container (or deploy via `ENVIROMENT/k8s/05-microservices.yaml` in namespace `pivot-banking`):
   ```powershell
   docker build -t pivot/ms-auth:1.0.0 .
   docker run -d -p 8085:8085 --name pivot-ms-auth pivot/ms-auth:1.0.0
   ```
