# Sigil

Self-contained JWT authentication portal: user management, token issuance, JWKS exposure, token introspection, and audit logging.

## Architecture

A **modular monolith** — one Spring Boot deployable with strict package-level boundaries between three modules. The Angular SPA communicates with the backend over REST. PostgreSQL is the single datastore, with each module owning its own JPA entities and repositories.

```
Angular SPA  →  Spring Boot (auth | user | audit)  →  PostgreSQL
```

### Project Structure

```
sigil/
├── frontend/                        # Angular + Tailwind SPA
│   └── src/app/
│       ├── auth/                    # Login page, token storage
│       ├── users/                   # User management UI
│       ├── tokens/                  # Token explorer & JWKS viewer
│       └── audit/                   # Audit log viewer
└── backend/                         # Spring Boot application
    └── src/main/java/dev/sigil/
        ├── auth/                    # JWT issuance, RS256 keypair, JWKS, introspection
        ├── user/                    # Users, roles, password encoding
        └── audit/                   # Append-only event log, async vthread writes
```

## Build & Run

### Prerequisites

- Java 25
- Node 24
- Docker (for PostgreSQL)

### Local Development

```sh
make setup   # install tools and dependencies
make run     # run locally (starts PostgreSQL via Docker Compose + backend + frontend)
make dev     # run with local Kubernetes (Skaffold)
```

### Tests

```sh
make test                # unit tests
make test-integration    # integration tests (Testcontainers)
```

## Configuration

| Variable             | Description                                                    | Default                                  |
| -------------------- | -------------------------------------------------------------- | ---------------------------------------- |
| `PORT`               | HTTP port                                                      | `8080`                                   |
| `LOG_LEVEL`          | Log level (`debug`, `info`, `warn`, `error`)                   | `info`                                   |
| `DB_URL`             | PostgreSQL JDBC URL                                            | `jdbc:postgresql://localhost:5432/sigil` |
| `DB_USER`            | PostgreSQL username                                            | `sigil`                                  |
| `DB_PASSWORD`        | PostgreSQL password                                            | —                                        |
| `JWT_KEYPAIR_SOURCE` | `generated` (startup) or `config` (load from env)              | `generated`                              |
| `JWT_PRIVATE_KEY`    | PEM-encoded RS256 private key (if `JWT_KEYPAIR_SOURCE=config`) | —                                        |
| `JWT_EXPIRY_SECONDS` | Token lifetime                                                 | `3600`                                   |

Secrets are managed via Vault in non-local environments. See `.env.example` for local dev.

## API

Base URL: `https://{host}/api/v1/`

| Method | Path                      | Description                           |
| ------ | ------------------------- | ------------------------------------- |
| `GET`  | `/api/v1/health`          | Health check                          |
| `POST` | `/api/v1/auth/login`      | Authenticate and receive a signed JWT |
| `POST` | `/api/v1/auth/introspect` | Validate and decode a JWT             |
| `GET`  | `/.well-known/jwks.json`  | Public key set (JWKS)                 |
| `GET`  | `/api/v1/users`           | List users                            |
| `POST` | `/api/v1/users`           | Create user                           |
| `PUT`  | `/api/v1/users/{id}`      | Update user (roles, status)           |
| `GET`  | `/api/v1/audit`           | Paginated audit event log             |

For full API docs, see Swagger UI at `/swagger-ui.html` (local only).

## ADRs

Architectural decisions are documented in [`docs/decisions/`](docs/decisions/).
