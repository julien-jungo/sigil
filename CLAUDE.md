# Guidelines

## Code Style

- No comments unless the WHY is non-obvious — well-named code is self-documenting
- Prefer functional composition over inheritance — use interfaces to abstract implementation
- Prefer immutability (record classes in Java, readonly in TypeScript)
- Keep functions, classes, and files short and focused
- Prefer early returns, keep nesting to a minimum
- Avoid magic numbers or strings — use named constants
- Use Go-like naming conventions (e.g. URL not Url, ID not Id)
- Prefer TypeScript over JavaScript; Python with type hints over Bash
- Auto-format: Prettier for TypeScript, gofmt for Go

## Error Handling

- Fail fast — don't silently swallow errors
- Use explicit, typed result/error types — no unchecked runtime exceptions
- Define custom error types when it improves readability
- Log errors with context (relevant state/identifiers)
- Use structured logging (JSON) with appropriate log levels
- Prefer idempotent operations

## Testing

- 80%+ test coverage with adequate branch coverage
- Test on all levels: unit, integration, API, and UI
- Avoid flaky tests — keep them deterministic, independent, and fast
- Test edge cases and error paths, not just happy paths
- Use mocks for external dependencies in unit tests
- Use test containers for integration tests

## Dependencies

- Prefer stdlib; otherwise use battle-tested libraries compatible with existing dependencies
- Vet dependencies before adding (maintenance, security, size)
- Pin versions — don't use latest
- Avoid duplicate dependencies

## Architecture

- Modular structure — keep related logic together (Go-like)
- Separate configuration from code — environment variables or config files
- K8s-ready from the start — include a local dev Kubernetes config using Skaffold

## API Design

- REST for CRUD-heavy APIs, gRPC for internal high-throughput services
- Version APIs from day one — URL path versioning (`/v1/`) for REST
- Cursor-based pagination for unbounded collections
- Return problem detail objects for errors (RFC 9457)

## Async & Concurrency

- Prefer async/await over raw threads or callbacks
- Avoid shared mutable state — communicate via channels or message queues
- Always set timeouts and cancellation contexts on I/O operations

## Observability

- OpenTelemetry for distributed tracing — instrument at service boundaries
- Expose `/metrics` (Prometheus-compatible) on all services
- Include correlation/trace IDs in all log entries
- Define SLOs and alert on them, not on raw resource metrics

## Git

- Conventional commits — include GitHub issue as scope if applicable (e.g. `feat(#100): add foo`)
- Branch naming: `feat/foo` or `feat/100_foo` (with GitHub issue)

## Security

- Never hardcode secrets — environment variables in local dev, Vault everywhere else
- Validate all external input; sanitize outputs to prevent XSS/injection
- Use parameterized queries — never concatenate SQL
- Keep dependencies patched for CVEs

## Documentation

- Keep READMEs short — include: description, architecture overview, build & run, configuration, public APIs
- Use ADRs for important architectural decisions — follow the MADR template

## CI/CD

- Pipeline sequence: lint → test → build → release → push to container registry
- Generate CHANGELOG from conventional commits
- Multi-stage Dockerfiles for container builds
- Scan container images for vulnerabilities
