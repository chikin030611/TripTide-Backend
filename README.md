# TripTide Backend Server

This is the backend server for **TripTide**, a travel planning application. The backend is built with **Spring Boot** and provides RESTful APIs to support user authentication, trip management, and integration with Google Places API.

## 📦 Technologies Used

- **Java 17+** with **Spring Boot 3.4.1**
- **PostgreSQL** for data persistence
- **Gradle** as the build system
- **Docker** (optional, for containerized development)

## 📋 Prerequisites

Ensure you have the following installed:

- Java SDK (compatible with Spring Boot 3.4.1)
- Gradle (or use the included Gradle wrapper)
- PostgreSQL (v12 or later recommended)
- Docker and Docker Compose (optional)

## 🛠️ Setup Instructions

### 1. Database Setup

1. Download and install PostgreSQL from: https://www.postgresql.org/download/
2. Ensure the PostgreSQL service is running
3. Create a new database manually
4. Restore data using:

```bash
pg_restore -U YOUR_USERNAME -d YOUR_DATABASE -p YOUR_PORT -F c database.dump
```
Replace `YOUR_USERNAME`, `YOUR_DATABASE`, and `YOUR_PORT` with your PostgreSQL credentials.

### 2. Environment Configuration
Copy the environment example file:
```bash
cp .env.example .env.dev-local
```

Edit .env.dev-local with the required values:
| Key                                                   | Description                                                                       |
| ----------------------------------------------------- | --------------------------------------------------------------------------------- |
| `SPRING_PROFILES_ACTIVE`                              | Profile to use (`development`, `production`, etc.)                                |
| `FRONTEND_URL`                                        | URL of the frontend client (e.g., [http://localhost:3000](http://localhost:3000)) |
| `GOOGLE_PLACES_API_KEY`                               | Your Google Places API key                                                        |
| `PROJECT`, `REGION`, `INSTANCE_NAME`                  | (Optional) Google Cloud Platform config                                           |
| `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASS` | PostgreSQL database credentials                                                   |
| `JWT_SECRET`                                          | Secret key for generating and validating JWT tokens                               |


### 3. Running the Server
Option 1 – Locally
```bash
./gradlew bootRun
```
Option 2 – With Docker
```
docker-compose --env-file .env.dev-local up --build
```
🛠️ Note: Docker support may require additional debugging if unused for a while.

## 🔐 Security
This server uses JWT (JSON Web Tokens) for secure authentication. Ensure your `JWT_SECRET` is stored securely and never committed to version control.

## 📁 Project Structure
```
backend/
├── src/
│   ├── main/
│   └── test/
├── .env.example
├── database.dump
├── build.gradle
└── docker-compose.yml
```
