# Redis Server Project

This folder runs a standalone Redis server for JWT refresh token storage.

## 1. Prepare environment

Copy `.env.example` to `.env` and set a strong password.

PowerShell:

```powershell
Copy-Item .env.example .env
```

## 2. Start Redis

From [infra/redis](C:/Users/USER/Desktop/toy-project/studio-reservation-back/infra/redis):

```powershell
docker compose up -d
```

## 3. Connect the Spring app

Set these environment variables before starting the backend:

```powershell
$env:REDIS_HOST = "localhost"
$env:REDIS_PORT = "6379"
$env:REDIS_PASSWORD = "change-this-redis-password"
```

The backend already reads these values from [application.yml](C:/Users/USER/Desktop/toy-project/studio-reservation-back/src/main/resources/application.yml).

## 4. Stop Redis

```powershell
docker compose down
```

If you want to remove the Redis data volume too:

```powershell
docker compose down -v
```
