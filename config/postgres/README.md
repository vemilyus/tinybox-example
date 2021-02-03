# Setup Postgres

- DB: `postgres`
- User: `postgres`
- Password: `postgres`

## Kubernetes (Minikube)

Run `kubectl create -f kubernetes.yml`

This creates a pod running PostgreSQL 13 (Alpine), and a service 
exposing port `5432` at hostname `postgres.default`.

## Locally (Docker)

Run `docker-compose up -d`

This creates a Docker container running PostgreSQL 13 (Alpine) and
exposes port `5432`.

It's recommended you create the following entry in `/etc/hosts` for 
parity with Kubernetes:

```text
postgres.default 127.0.0.1
```
