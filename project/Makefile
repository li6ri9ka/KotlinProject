SHELL := /bin/zsh

.PHONY: test up down smoke ci-local

test:
	./gradlew clean test

up:
	docker compose --env-file .env up -d --build

down:
	docker compose down -v

smoke:
	./scripts/smoke_api.sh

ci-local:
	./gradlew clean test assemble
	docker build -t shop-service:local .
	docker build -f Dockerfile.worker -t shop-worker:local .
