# Development
## Local 
### Run the application
```
./gradlew bootRun
```
### Access the database
```
psql -h localhost -p 5433 -U postgres -d triptide
```

## Docker
### Run the application with Docker
```
docker-compose --env-file .env.dev-hosted up --build  
```
### Access the database
```
docker exec -it triptide-backend-db-1 psql -U postgres -d triptide
```

# Staging
### Run the application
```
./scripts/deploy-staging.sh
```

