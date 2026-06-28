# {Project Name}

{One sentence describing what this service does and why it exists.}

## Architecture

{Brief component overview — what are the main pieces and how do they relate.}

```
{ascii diagram or component list}
```

### Project Structure

```
{project structure tree}
```

## Build & Run

### Prerequisites

- {e.g. Go 1.24+, Node 22+, Docker}

### Local Development

```sh
make setup   # install tools and dependencies
make run     # run locally
make dev     # run with local Kubernetes (Skaffold)
```

### Tests

```sh
make test                # unit tests
make test-integration    # integration tests
```

## Configuration

| Variable    | Description                                  | Default |
| ----------- | -------------------------------------------- | ------- |
| `PORT`      | HTTP port                                    | `8080`  |
| `LOG_LEVEL` | Log level (`debug`, `info`, `warn`, `error`) | `info`  |

Secrets are managed via Vault in non-local environments. See `.env.example` for local dev.

## API

Base URL: `https://{host}/v1/`

| Method | Path         | Description  |
| ------ | ------------ | ------------ |
| `GET`  | `/v1/health` | Health check |

For full API docs, see [{link}]({link}).

## ADRs

Architectural decisions are documented in [`docs/decisions/`](docs/decisions/).
