# Shop Service (Ktor + PostgreSQL + Redis + RabbitMQ)

Backend-сервис интернет-магазина с JWT авторизацией, заказами, кэшем, очередью и worker-процессом.

## Tech stack

- Kotlin 2.0 + Ktor
- PostgreSQL + Exposed ORM
- Flyway migrations
- JWT auth (user/admin)
- Redis cache
- RabbitMQ events + Worker consumer
- OpenAPI/Swagger
- Unit + Integration + E2E tests
- Docker + docker-compose
- GitHub Actions CI

## Project structure

- `app` - HTTP API (routes/controllers/security/openapi)
- `data` - repositories, DB config, redis/rabbit integrations
- `domain` - business services, models, errors
- `worker` - RabbitMQ consumer + fake email sender
- `common` - shared DTO

## API

- Swagger UI: `GET /swagger`
- OpenAPI YAML: `GET /openapi`
- Healthcheck: `GET /health`

Main endpoints:

- `POST /auth/register`
- `POST /auth/login`
- `GET /products`
- `GET /products/{id}`
- `POST /orders`
- `GET /orders`
- `DELETE /orders/{id}`
- `POST /products` (admin)
- `PUT /products/{id}` (admin)
- `DELETE /products/{id}` (admin)
- `GET /stats/orders` (admin)

## Environment variables

Minimal:

- `PORT` (default: `8080`)
- `DB_JDBC_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_RUN_MIGRATIONS` (`true/false`)
- `JWT_SECRET`
- `JWT_ISSUER`
- `JWT_AUDIENCE`
- `JWT_REALM`
- `JWT_EXPIRES_IN_SECONDS`
- `ADMIN_BOOTSTRAP_EMAIL` (optional)
- `ADMIN_BOOTSTRAP_PASSWORD` (optional)

Cache:

- `REDIS_ENABLED`
- `REDIS_HOST`
- `REDIS_PORT`
- `REDIS_PRODUCT_TTL_SECONDS`
- `REDIS_ORDER_TTL_SECONDS`

Queue:

- `RABBITMQ_ENABLED`
- `RABBITMQ_HOST`
- `RABBITMQ_PORT`
- `RABBITMQ_USERNAME`
- `RABBITMQ_PASSWORD`
- `RABBITMQ_VHOST`

## Run locally (Gradle)

```bash
./gradlew :app:run
```

Worker:

```bash
./gradlew :worker:run
```

## Run with Docker Compose

```bash
cp .env.example .env
docker compose up --build
```

Or with helper command:

```bash
cp .env.example .env
make up
```

Services:

- API: `http://localhost:8080`
- Swagger: `http://localhost:8080/swagger`
- RabbitMQ UI: `http://localhost:15672` (`guest/guest`)
- PostgreSQL: `localhost:5432`
- Redis: `localhost:6379`

Default admin for `docker compose`:

- email: `admin@example.com`
- password: `admin123`

## Tests

```bash
./gradlew clean test
```

Smoke check of deployed/local API:

```bash
BASE_URL=http://localhost:8080 ./scripts/smoke_api.sh
```

## CI

GitHub Actions workflow: `.github/workflows/ci.yml`

Pipeline steps:

- `clean test assemble`
- build app Docker image
- build worker Docker image
- docker-compose smoke check (`/health`)

## Deploy

### Railway

1. Create new project from repository.
2. Railway detects `Dockerfile` (or use `railway.toml`).
3. Add PostgreSQL, Redis, RabbitMQ services.
4. Set env vars from section above, including `ADMIN_BOOTSTRAP_EMAIL` and `ADMIN_BOOTSTRAP_PASSWORD`.
5. Set healthcheck `/health`.

### Render

1. Create Web Service from repository.
2. Runtime: Docker (`Dockerfile`), or use `render.yaml`.
3. Add managed PostgreSQL/Redis/RabbitMQ.
4. Configure same env vars, including admin bootstrap variables.
5. Deploy and verify `/health` and `/swagger`.

## Public URL

Add deployed URL here after publish:

- App: `<PUT_PUBLIC_URL_HERE>`
