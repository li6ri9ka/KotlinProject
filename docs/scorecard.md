# Project Scorecard (100/100 checklist)

## 1) Architecture + structure (15)

- Multi-module: `app`, `data`, `domain`, `worker`, `common`
- Layer split:
  - controllers/routes: `app/src/main/kotlin/org/example/app/controller`, `.../routes`
  - services: `data/src/main/kotlin/org/example/data/service/impl`
  - repositories: `data/src/main/kotlin/org/example/data/repository/impl`
  - domain models/contracts: `domain/src/main/kotlin/org/example/domain`

## 2) Database + migrations (15)

- Flyway migration:
  - `data/src/main/resources/db/migration/V1__init_schema.sql`
- Tables included: `users`, `products`, `orders`, `order_items`, `audit_logs`
- FKs + indexes: present in migration SQL.

## 3) Business logic (15)

- Order flow in:
  - `data/src/main/kotlin/org/example/data/service/impl/OrderServiceImpl.kt`
- Includes:
  - stock validation
  - stock decrement/increment
  - audit log writing
  - queue event publish

## 4) JWT auth/authz (10)

- Register/login routes:
  - `app/src/main/kotlin/org/example/app/routes/AuthRoutes.kt`
- JWT setup:
  - `app/src/main/kotlin/org/example/app/auth/SecurityConfig.kt`
  - `app/src/main/kotlin/org/example/app/auth/JwtTokenService.kt`
- Admin guard:
  - `app/src/main/kotlin/org/example/app/auth/RoleGuard.kt`
  - `app/src/main/kotlin/org/example/app/routes/AdminRoutes.kt`

## 5) Redis cache (10)

- Cache facade and Redis implementation:
  - `data/src/main/kotlin/org/example/data/cache/CacheFacade.kt`
  - `data/src/main/kotlin/org/example/data/cache/RedisCacheFacade.kt`
- Cache usage + invalidation:
  - `app/src/main/kotlin/org/example/app/controller/ProductController.kt`
  - `app/src/main/kotlin/org/example/app/controller/OrderController.kt`
  - `app/src/main/kotlin/org/example/app/controller/AdminController.kt`

## 6) Queue RabbitMQ (10)

- Producer:
  - `data/src/main/kotlin/org/example/data/queue/RabbitMqOrderEventPublisher.kt`
- Consumer/worker:
  - `worker/src/main/kotlin/org/example/worker/consumer/OrderEventsConsumer.kt`
  - `worker/src/main/kotlin/org/example/worker/email/FakeEmailSender.kt`

## 7) Swagger docs (5)

- OpenAPI + Swagger UI:
  - `app/src/main/kotlin/org/example/app/plugins/DocumentationConfig.kt`
  - `app/src/main/resources/openapi/documentation.yaml`

## 8) Testing (10)

- Unit:
  - `app/src/test/kotlin/org/example/app/controller/*Test.kt`
  - `data/src/test/kotlin/org/example/data/service/impl/OrderServiceImplTest.kt`
  - `app/src/test/kotlin/org/example/app/bootstrap/AdminBootstrapperTest.kt`
- Integration (Testcontainers):
  - `app/src/test/kotlin/org/example/app/integration/*IntegrationTest.kt`
  - `data/src/test/kotlin/org/example/data/queue/RabbitMqOrderEventPublisherIntegrationTest.kt`
- E2E:
  - `app/src/test/kotlin/org/example/app/e2e/*E2ETest.kt`

## 9) Docker + compose (5)

- Docker images:
  - `Dockerfile`
  - `Dockerfile.worker`
- Compose stack:
  - `docker-compose.yml`

## 10) Cloud deploy (5)

- Render config:
  - `render.yaml`
- Railway config:
  - `railway.toml`
- Health endpoint:
  - `GET /health`

## 11) Git quality + README (5)

- README:
  - `README.md`
- CI:
  - `.github/workflows/ci.yml`

## Verification commands

```bash
./gradlew clean test
docker compose --env-file .env up -d --build
BASE_URL=http://localhost:8080 ./scripts/smoke_api.sh
docker compose down -v
```
