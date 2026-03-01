# Deploy Guide (Render / Railway)

## 1) Pre-deploy checks

```bash
./gradlew clean test
cp .env.example .env
docker compose --env-file .env up -d --build
BASE_URL=http://localhost:8080 ./scripts/smoke_api.sh
docker compose down -v
```

## 2) Required environment variables

Set these in cloud service:

- `PORT` (Render usually uses `10000`)
- `DB_JDBC_URL`, `DB_USERNAME`, `DB_PASSWORD`, `DB_RUN_MIGRATIONS=true`
- `JWT_SECRET`, `JWT_ISSUER`, `JWT_AUDIENCE`, `JWT_REALM`, `JWT_EXPIRES_IN_SECONDS`
- `ADMIN_BOOTSTRAP_EMAIL`, `ADMIN_BOOTSTRAP_PASSWORD`
- `REDIS_ENABLED=true`, `REDIS_HOST`, `REDIS_PORT`
- `RABBITMQ_ENABLED=true`, `RABBITMQ_HOST`, `RABBITMQ_PORT`, `RABBITMQ_USERNAME`, `RABBITMQ_PASSWORD`, `RABBITMQ_VHOST`

## 3) Render deploy

1. Create `Web Service` from GitHub repository.
2. Runtime: `Docker`.
3. Dockerfile path: `./Dockerfile` (or use `render.yaml`).
4. Add managed PostgreSQL, Redis, RabbitMQ.
5. Fill env vars from section above.
6. Health check path: `/health`.
7. Deploy.

## 4) Railway deploy

1. Create project from GitHub repository.
2. Use `Dockerfile` build (`railway.toml` is already included).
3. Add PostgreSQL, Redis, RabbitMQ services.
4. Fill env vars from section above.
5. Set health check path `/health`.
6. Deploy.

## 5) Post-deploy verification

After service is up:

```bash
./scripts/post_deploy_check.sh https://<your-service-domain>
```

Then update `README.md`:

- `Public URL` section with real link
