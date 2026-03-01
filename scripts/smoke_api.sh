#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"

echo "[1/4] Health check"
curl -fsS "${BASE_URL}/health" >/dev/null

echo "[2/4] Swagger check"
curl -fsS "${BASE_URL}/swagger" >/dev/null

echo "[3/4] Register check"
curl -fsS -X POST "${BASE_URL}/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"email":"smoke-user@example.com","password":"strongPass123"}' >/dev/null || true

echo "[4/4] Login check"
LOGIN_RESPONSE="$(curl -fsS -X POST "${BASE_URL}/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"smoke-user@example.com","password":"strongPass123"}')"

echo "${LOGIN_RESPONSE}" | grep -q "accessToken"

echo "Smoke checks passed for ${BASE_URL}"
