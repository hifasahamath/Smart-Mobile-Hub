# Smart Mobile Hub

An AI-powered e-commerce platform built with Next.js, Java 21, Spring Boot, PostgreSQL, Kafka, and Redis.

## Features

- AI-powered shopping assistant (Gemini API)
- E-commerce operations (Products, Orders, Inventory, Payments)
- Event-driven architecture with Kafka
- Observability with Prometheus, Grafana, and Jaeger

## Project Structure

- `frontend/`: Next.js frontend application
- `backend/`: Spring Boot microservices
- `infrastructure/`: Docker Compose and configuration for backing services
- `docs/`: Documentation and API specifications

## Local Development

### Requirements

- Docker and Docker Compose
- Java 21
- Node.js 18+
- Maven

### Running Infrastructure

Start the supporting services (PostgreSQL, Redis, Kafka, Observability):

```bash
docker-compose up -d
```

### Backend Services

The backend uses a Maven multi-module structure.

```bash
cd backend
mvn clean install -DskipTests
```

You can start each service using your IDE or Maven:

```bash
cd backend/api-gateway
mvn spring-boot:run
```

### Frontend

Start the Next.js development server:

```bash
cd frontend
npm install
npm run dev
```

## Environment Variables

Copy `.env.example` to `.env` and fill in your configuration before running the applications.
