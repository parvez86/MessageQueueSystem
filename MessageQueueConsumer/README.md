# Message Queue Management Consumer Portal

A robust Spring Boot application for processing and managing orders and users through RabbitMQ message queues with PostgreSQL persistence.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Requirements](#requirements)
- [Setup and Installation](#setup-and-installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Message Queue Integration](#message-queue-integration)
- [Database Schema](#database-schema)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

## Overview

The Message Queue Management Consumer Portal is a microservice that consumes order and user data from RabbitMQ message queues and provides a RESTful API for managing orders and users. It serves as a critical component in a distributed system architecture, enabling asynchronous processing of information.

## Features

- **Message Queue Integration**: Consumes messages from RabbitMQ queues for order and user processing
- **RESTful API**: Comprehensive endpoints for CRUD operations on orders and users
- **Database Persistence**: Stores all data in PostgreSQL with proper transaction management
- **OpenAPI Documentation**: Auto-generated API documentation using Swagger
- **Validation**: Input validation for all API requests
- **Error Handling**: Consistent error responses and proper exception handling
- **Soft Delete**: Support for soft deletion of data
- **Pagination**: Support for paginated results
- **Search Functionality**: API endpoints for searching orders and users

## Technology Stack

- **Spring Boot 3.4.3**: Application framework
- **Spring Data JPA**: Database access and ORM
- **Spring AMQP**: RabbitMQ integration
- **PostgreSQL**: Relational database
- **Swagger/OpenAPI**: API documentation
- **Lombok**: Reduces boilerplate code
- **Jackson**: JSON processing
- **JUnit & Mockito**: Testing framework

## System Architecture

```
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│                 │      │                 │      │                 │
│  Client Apps    │──────►  RabbitMQ       │──────►  Message Queue  │
│  (Producers)    │      │  Message Broker │      │  Management     │
│                 │      │                 │      │  Consumer       │
└─────────────────┘      └─────────────────┘      └────────┬────────┘
                                                          │
                                                          │
                                                          ▼
                                               ┌─────────────────────┐
                                               │                     │
                                               │  PostgreSQL         │
                                               │  Database           │
                                               │                     │
                                               └─────────────────────┘
```

## Requirements

- JDK 21 or higher
- Maven 3.8+ or Gradle 8.0+
- PostgreSQL 12+
- RabbitMQ 3.8+
- Docker (optional, for containerization)

## Setup and Installation

### Database Setup

1. Ensure PostgreSQL is running and accessible
2. Create a database named `messagequeue`:
   ```sql
   CREATE DATABASE messagequeue;
   ```
3. Create a user with appropriate permissions:
   ```sql
   CREATE USER test WITH PASSWORD '12345sp';
   GRANT ALL PRIVILEGES ON DATABASE messagequeue TO test;
   ```

### RabbitMQ Setup

1. Ensure RabbitMQ is running with management plugin enabled
2. Create a user with appropriate permissions:
   ```
   rabbitmqctl add_user admin password
   rabbitmqctl set_user_tags admin administrator
   rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"
   ```

### Application Setup

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd MessageQueueSystem/MessageQueueConsumer
   ```

2. Configure application properties (if needed):
   - Edit `src/main/resources/application.properties` with your specific environment settings

3. Build the application:
   ```bash
   # If using Maven
   ./mvnw clean package

   # If using Gradle
   ./gradlew clean build
   ```

## Running the Application

### Running locally

```bash
# If using Maven
./mvnw spring-boot:run

# If using Gradle
./gradlew bootRun
```

### Running with Docker

```bash
# Build Docker image
docker build -t message-queue-management-consumer .

# Run Docker container
docker run -p 10002:10002 --name message-queue-consumer \
  --network=host \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/messagequeue \
  -e SPRING_RABBITMQ_HOST=localhost \
  message-queue-management-consumer
```

### Using Docker Compose

```bash
# Start all services
docker-compose up -d
```

## API Documentation

Once the application is running, the Swagger UI is available at:

```
http://localhost:10002/message-queue-management-consumer-portal/v1/swagger-ui.html
```

The OpenAPI specification is available at:

```
http://localhost:10002/message-queue-management-consumer-portal/v1/api-docs
```

### Key API Endpoints

#### User Management

| Method | URL                          | Description            |
|--------|------------------------------|------------------------|
| GET    | /api/users                   | Get all users (paginated) |
| GET    | /api/users/{uid}             | Get user by ID         |
| POST   | /api/users                   | Create new user        |
| PUT    | /api/users/{uid}             | Update user            |
| DELETE | /api/users/{uid}             | Delete user            |
| GET    | /api/users/status/{status}   | Get users by status    |
| GET    | /api/users/search?term={term}| Search users           |
| PATCH  | /api/users/{uid}/status      | Update user status     |

#### Order Management

| Method | URL                             | Description              |
|--------|--------------------------------|--------------------------|
| GET    | /api/orders                    | Get all orders (paginated) |
| GET    | /api/orders/{orderId}          | Get order by ID          |
| POST   | /api/orders                    | Create new order         |
| PUT    | /api/orders/{orderId}          | Update order             |
| DELETE | /api/orders/{orderId}          | Delete order             |
| GET    | /api/orders/status/{status}    | Get orders by status     |
| GET    | /api/orders/customer/{customerId} | Get orders by customer |
| PATCH  | /api/orders/{orderId}/status   | Update order status      |
| GET    | /api/orders/amount/greater-than?amount={amount} | Get orders by minimum amount |

## Message Queue Integration

### Queue Configuration

The application listens to the following queues:

- `user_queue`: For user-related messages
- `order.queue`: For order-related messages

### Exchange Configuration

The application uses the following exchanges:

- `user_direct_exchange`: Direct exchange for user messages
- `user_topic_exchange`: Topic exchange for user notifications
- `user_fanout_exchange`: Fanout exchange for broadcasting user events
- `order.fanout.exchange`: Fanout exchange for broadcasting order events

### Routing Keys

- `user_routing_key`: For routing user messages

### Message Structure

#### User Message Format

```json
{
  "userName": "john_doe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "123-456-7890"
}
```

#### Order Message Format

```json
{
  "orderName": "Sample Order #123",
  "orderAmount": 99.99,
  "customerName": "John Doe"
}
```

### Publishing Messages

To publish messages to the queues, use:

```java
// Inside a service with RabbitTemplate injected
rabbitTemplate.convertAndSend(MessageUtil.DIRECT_EXCHANGE_USER, 
                            MessageUtil.USER_ROUTING_KEY, userDto);

// For orders (using fanout exchange)
rabbitTemplate.convertAndSend(MessageUtil.FANOUT_EXCHANGE_ORDER, "", orderDto);
```

## Database Schema

### Users Table

| Column       | Type            | Description                       |
|--------------|-----------------|-----------------------------------|
| id           | BIGINT          | Primary key                       |
| uid          | VARCHAR(255)    | Unique identifier (UUID)          |
| user_name    | VARCHAR(255)    | Username (unique)                 |
| email        | VARCHAR(255)    | Email address (unique)            |
| first_name   | VARCHAR(255)    | First name                        |
| last_name    | VARCHAR(255)    | Last name                         |
| phone_number | VARCHAR(255)    | Phone number                      |
| status       | INTEGER         | User status (0=ACTIVE, etc.)      |
| created_at   | TIMESTAMP       | Creation timestamp                |
| updated_at   | TIMESTAMP       | Last update timestamp             |
| version      | INTEGER         | Optimistic locking version        |
| created_by   | VARCHAR(255)    | User who created this record      |
| updated_by   | VARCHAR(255)    | User who last updated this record |

### Orders Table

| Column       | Type            | Description                       |
|--------------|-----------------|-----------------------------------|
| id           | BIGINT          | Primary key                       |
| order_id     | VARCHAR(255)    | Unique order identifier (UUID)    |
| order_name   | VARCHAR(255)    | Order name or description         |
| order_amount | DECIMAL         | Order amount                      |
| customer_id  | VARCHAR(255)    | Foreign key to users(uid)         |
| order_date   | TIMESTAMP       | Order date and time               |
| order_status | VARCHAR(255)    | Order status (PENDING, etc.)      |
| status       | INTEGER         | Record status (0=ACTIVE, etc.)    |
| created_at   | TIMESTAMP       | Creation timestamp                |
| updated_at   | TIMESTAMP       | Last update timestamp             |
| version      | INTEGER         | Optimistic locking version        |
| created_by   | VARCHAR(255)    | User who created this record      |
| updated_by   | VARCHAR(255)    | User who last updated this record |

## Troubleshooting

### Common Issues

1. **Connection Issues with RabbitMQ**
   - Verify RabbitMQ is running: `rabbitmqctl status`
   - Check the credentials in `application.properties`
   - Ensure the queues and exchanges exist

2. **Database Connection Problems**
   - Verify PostgreSQL is running: `pg_isready -h localhost -p 5432`
   - Check the database URL, username, and password in `application.properties`
   - Ensure the database and user exist with proper permissions

3. **Application Startup Issues**
   - Check for port conflicts (default: 10002)
   - Review logs for detailed error messages
   - Verify all required dependencies are available

### Date/Time Serialization Issues

If you encounter JSON serialization issues with date/time fields, ensure:

1. The `JacksonConfig` class is properly configured
2. All `Instant` fields have proper `@JsonFormat` annotations
3. The JavaTimeModule is registered with the ObjectMapper

### Application Properties

Here are the key application properties:

```properties
server.port=10002
spring.application.name=Message Queue Management Consumer Portal
server.servlet.context-path=/message-queue-management-consumer-portal/v1

# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=password

# PostgreSQL
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/messagequeue
spring.datasource.username=test
spring.datasource.password=12345sp

# JPA/Hibernate
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=none
```

## Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.