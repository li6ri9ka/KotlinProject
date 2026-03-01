# KotlinProject (Backend магазина)

Простой учебный backend на Kotlin + Ktor.  
Сервис умеет регистрировать пользователей, выдавать JWT, показывать товары, создавать/отменять заказы и отдавать статистику для администратора.

## Что реализовано

- регистрация и логин (`JWT`)
- список товаров и товар по id
- создание и отмена заказа
- история заказов пользователя
- админские маршруты:
  - добавление/изменение/удаление товара
  - статистика по заказам
- PostgreSQL + миграции Flyway
- Redis (кэш, можно отключить)
- RabbitMQ (очередь событий, можно отключить)
- Swagger / OpenAPI
- Unit + Integration + E2E тесты
- Docker + docker-compose

## Стек

- Kotlin 2.0
- Ktor
- Exposed ORM
- PostgreSQL
- Flyway
- Redis
- RabbitMQ
- Testcontainers

## Запуск локально (через Docker)

Самый удобный вариант:

```bash
cp .env.example .env
docker compose up --build
```

Остановка:

```bash
docker compose down -v
```

## Запуск локально (без Docker)

```bash
./gradlew :app:run
```

В отдельном терминале (если нужен worker):

```bash
./gradlew :worker:run
```

## Тесты

```bash
./gradlew clean test
```

## URL для проверки

### Локально

- API base: `http://localhost:8080`
- Health: `http://localhost:8080/health`
- Swagger: `http://localhost:8080/swagger`
- OpenAPI: `http://localhost:8080/openapi`

### Прод (Render)

- API base: `https://kotlinproject-wuxy.onrender.com`
- Health: `https://kotlinproject-wuxy.onrender.com/health`
- Swagger: `https://kotlinproject-wuxy.onrender.com/swagger`
- OpenAPI: `https://kotlinproject-wuxy.onrender.com/openapi`

Примечание: на `/` может быть `404`, это нормально (корневой маршрут не используется).

## Основные API ручки

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

## Переменные окружения (минимум)

- `DB_JDBC_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `DB_RUN_MIGRATIONS=true`
- `JWT_SECRET`
- `JWT_ISSUER`
- `JWT_AUDIENCE`
- `JWT_REALM`
- `JWT_EXPIRES_IN_SECONDS`

Если Redis/RabbitMQ не используются:

- `REDIS_ENABLED=false`
- `RABBITMQ_ENABLED=false`

## Структура модулей

- `app` — роуты, контроллеры, конфиг Ktor
- `data` — репозитории, БД, кэш, очередь
- `domain` — бизнес-модели и интерфейсы
- `worker` — обработчик сообщений из очереди
- `common` — общие DTO
