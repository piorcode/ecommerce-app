# E-Commerce Microservices

Small e-commerce backend implemented as a Maven multi-module Spring Boot project.

The system consists of two independent services:

- **Product Service** - exposes a product catalog API.
- **Cart Service** - manages user cart operations and validates products through Product Service before adding them to the cart.

The project is implemented as a small but production-minded assignment, with separate service boundaries, separate H2 databases, JWT-protected cart endpoints, OpenAPI documentation and health check endpoints.

## Tech Stack

- **Java 17**
- **Spring Boot 4**
- **Maven multi-module project**
- **Spring Web MVC**
- **Spring Data JPA**
- **H2 in-memory database**
- **JUnit 5**
- **Mockito**
- **Spring Security**
- **JWT authentication**
- **Springdoc OpenAPI**
- **Spring Boot Actuator**
- **Docker**
- **Docker Compose**

## Architecture

The project follows a simple microservice-oriented architecture.

```text
Client
  |
  | HTTP
  v
Cart Service  ---- HTTP ---->  Product Service
  |                              |
  | H2 database                  | H2 database
  v                              v
cartdb                         productdb
```

### Product Service

Product Service owns the product catalog. It exposes endpoints for:

- listing products,
- retrieving product details by id.

It also seeds sample product data into an H2 database using a `data.sql` initialization script.

### Cart Service

Cart Service owns cart data. It exposes endpoints for:

- viewing the user's cart,
- adding an item to the user's cart,
- updating item quantity,
- removing an item from the cart.

Before adding an item to the cart, Cart Service calls Product Service to validate that the product exists and is available.

## Architectural Decisions

### REST communication between services

Cart Service communicates with Product Service using synchronous REST calls.

I have chosen this approach because the required flow is simple and request-driven: before adding an item to the cart, Cart Service needs to verify whether the product exists and is currently available. A REST call keeps this interaction explicit, easy to understand and easy to test manually.

I have previously developed a small microservices-based application using Kafka for training purposes: https://github.com/piorcode/Bookshop, so I am aware that introducing asynchronous messaging and a reliable Saga flow adds noticeable implementation and operational complexity. For this assignment, synchronous REST communication is a better fit because it is easy to set up, provides immediate interactions and is easy to understand.

In a larger production system, I would consider adding resilience patterns such as timeouts, retries and circuit breakers.

### Product validation before adding to cart

Cart Service validates the product through Product Service before saving a cart item.

This prevents users from adding unknown or unavailable products to the cart:
- if Product Service returns that the product does not exist, Cart Service does not modify the cart, 
- if the product exists but is unavailable, Cart Service rejects the operation.

This keeps Product Service as the source of truth for product data.

### Product snapshot in cart items

Cart Service stores a small snapshot of product data inside `CartItemEntity`:

- product id,
- product name,
- item price,
- quantity.

I used this approach because it avoids calling Product Service every time cart data needs to be displayed or processed. It also makes Cart Service the owner of its cart data after the item has been added.

If product data becomes outdated, for example because of a product name or price change, I would revalidate product before placing an order.

### JWT authentication for Cart Service

Cart endpoints are protected with JWT authentication.

I have chosen JWT because Cart Service needs a simple stateless way to identify the user making the request. The user id is stored in the token subject and extracted by the JWT filter. The controller then uses the authenticated principal name to resolve the user's cart.

For this assignment Cart Service exposes a simple local token endpoint to make testing easier. In production, JWTs are usually created by the system responsible for user authentication.

Product Service endpoints are public in this implementation. I chose this because product catalog browsing is usually a public operation in e-commerce systems, while cart operations are user-specific.

### Separate databases per service

Both services use separate H2 in-memory databases.

I chose H2 because it is lightweight and quick to configure. Since this assignment is time-boxed, I wanted to keep the local setup simple and focus on the required business flow. I had also used H2 before in a training project, so it was a familiar and practical choice for this scope.

This follows the microservice principle that each service owns its own data. Cart Service does not directly access the Product Service database. Instead, it uses Product Service API to validate product information.

In a production environment, I would use a persistent database such as PostgreSQL and manage schema changes with Liquibase.

### Unique cart item constraint

`CartItemEntity` uses a uniqueness constraint for the combination of cart and product.

The goal is to prevent storing duplicate rows for the same product in the same cart. Without this constraint, the database could contain multiple cart items with the same `productId` for one cart, which would make quantity updates and removals ambiguous.

### Service-layer focused tests

The current test coverage focuses on unit tests for service-layer business logic.

This was prioritized because the most important assignment-specific behavior is implemented in services:

- product lookup,
- cart item operations,
- product validation before adding to cart,
- unavailable product handling,
- missing cart item scenarios.

Given more time, I would add controller tests with MockMvc, JWT security tests and integration tests covering communication between Cart Service and Product Service.

### Docker-based local setup

Dockerfiles and Docker Compose configuration are included to simplify local development and service startup.

Both services can be started together using a single Docker Compose command. Since both services use H2 in-memory databases, no additional infrastructure containers are required.

## Services

### Product Service

Product Service runs on port:

```text
8081
```

Main responsibilities:
- expose product catalog endpoints,
- return product details by id,
- initialize sample products from `data.sql`,
- manage product data in its own H2 database

Example endpoints:

```http
GET /api/v1/products
GET /api/v1/products/{productId}
```

### Cart Service

Cart Service runs on port:

```text
8082
```

Main responsibilities:
- cart item operations,
- product validation through Product Service,
- JWT-protected cart API,
- cart persistence.

Example endpoints:

```http
GET    /api/v1/cart
POST   /api/v1/cart/items
PATCH  /api/v1/cart/items/{productId}
DELETE /api/v1/cart/items/{productId}
```

Cart endpoints require a JWT token.

## Data Model

### Product Service

Product Service stores products in the `products` table.

Main fields:

- `id` - product identifier,
- `name` - product name,
- `description` - product description,
- `price` - product price,
- `available` - product availability flag,
- `created_at` - creation timestamp.

Sample products are loaded from:

```text
product-service/src/main/resources/data.sql
```

### Cart Service

Cart Service stores carts and cart items in two tables:
- `carts`
- `cart_items`

A cart belongs to a single user and contains multiple cart items.

Main `carts` fields:

- `id` - cart identifier,
- `user_id` - authenticated user identifier,
- `created_at` - creation timestamp.

Main `cart_items` fields:

- `id` - cart item identifier,
- `cart_id` - reference to the owning cart,
- `product_id` - product identifier from Product Service,
- `product_name` - product name snapshot,
- `price` - product price snapshot,
- `quantity` - item quantity.

CartItemEntity has a uniqueness constraint on the combination of cart and product. This prevents storing duplicate rows for the same product in the same cart.

## Running the application

### Prerequisites

Make sure you have installed:
- Java 17 or newer,
- Maven,
- Docker.

### Running with Docker

The project includes Dockerfiles for both services and can be started using Docker Compose.

### Build application artifacts

From the root directory:

```bash
mvn clean package
```

### Start services with Docker Compose

From the root directory:

```bash
docker compose up --build
```

This starts:
- Product Service on port 8081,
- Cart Service on port 8082.

Docker Compose was implemented as an additional improvement to simplify local startup and allow both services to be launched with a single command.

### Running Locally

### Build the project

From the root directory:

```bash
mvn clean install
```

### Start Product Service

From the root directory:

```bash
mvn spring-boot:run -pl product-service
```

Product Service should start on:

```md
http://localhost:8081
```

### Start Cart Service

Open a second terminal and run:

```bash
mvn spring-boot:run -pl cart-service
```

Cart Service should start on:

```md
http://localhost:8082
```

## Useful URLs

### Product Service

Swagger UI:

```md
http://localhost:8081/swagger-ui.html
```

OpenAPI JSON:

```md
http://localhost:8081/v3/api-docs
```

Health check:

```md
http://localhost:8081/actuator/health
```

H2 Console:

```md
http://localhost:8081/h2-console
```

H2 connection details:

```text
JDBC URL: jdbc:h2:mem:productdb
User Name: sa
Password:
```

### Cart Service

Swagger UI:

```md
http://localhost:8082/swagger-ui.html
```

OpenAPI JSON:

```md
http://localhost:8082/v3/api-docs
```

Health check:

```md
http://localhost:8082/actuator/health
```

H2 Console:

```md
http://localhost:8082/h2-console
```

H2 connection details:

```text
JDBC URL: jdbc:h2:mem:cartdb
User Name: sa
Password:
```

## Authentication

Cart Service endpoints are protected with JWT.
For local testing, Cart Service exposes a simple token endpoint:

```md
POST /api/v1/auth/token
```

Example request body:

```json
{
  "userId": "user-1"
}
```

Example response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

The returned token must be sent in the `Authorization` header:

```http
Authorization: Bearer <TOKEN>
```

The user id is extracted from the JWT subject and used to resolve the user's cart.
The local token endpoint is included only to make the assignment easy to run and test.

## API Examples

The examples below use Windows PowerShell 'curl.exe'.

## Product Service API

### List products

```bash
curl.exe -i http://localhost:8081/api/v1/products
```

Example response:

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "Laptop",
    "price": 3499,
    "available": true
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Smartphone",
    "price": 2499,
    "available": true
  },
  ...
]
```

### Get product details

```bash
curl.exe -i http://localhost:8081/api/v1/products/<PRODUCT_ID>
```

Example:

```bash
curl http://localhost:8081/api/v1/products/550e8400-e29b-41d4-a716-446655440000
```

Example response:
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Laptop",
  "description": "Basic laptop for everyday work.",
  "price": 3499,
  "available": true,
  "createdAt": "2026-08-11T13:16:57.132912Z"
}
```

## Cart Service API

### Generate JWT token

```bash
curl.exe -i -X POST http://localhost:8082/api/v1/auth/token -H "Content-Type: application/json" -d '{"userId":"user-1"}'
```

Copy the returned token and use it in the next requests.

### Get Cart

```bash
curl.exe -i -X GET http://localhost:8082/api/v1/cart -H "Authorization: Bearer <TOKEN>"
```

Example response:
```json
{
  "cartId": "46a3e5a5-f9c0-4d25-be29-c0eaaa48b183",
  "items": [
    {
      "productId": "550e8400-e29b-41d4-a716-446655440001",
      "productName": "Smartphone",
      "price": 2499,
      "quantity": 1
    },
    {
      "productId": "550e8400-e29b-41d4-a716-446655440002",
      "productName": "Headphones",
      "price": 499,
      "quantity": 1
    }
  ]
}
```

### Add item to cart

```bash
  curl.exe -i -X POST http://localhost:8082/api/v1/cart/items -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"productId":"<PRODUCT_ID>","quantity":1}'
```

Expected response:

```http
201 Created
```

Possible error responses:

- `400 Bad Request` - invalid request body, quantity > 50, quantity < 1
- `401 Unauthorized` - missing or invalid JWT token
- `404 Not Found` - product, cart or item not found
- `409 Conflict` - product exists but is unavailable
- `503 Service Unavailable` - Product Service is unavailable

Sending a `POST` request with a product that is already in the cart increases the quantity of the existing cart item.

### Update cart item quantity

```bash
  curl.exe -i -X PATCH http://localhost:8082/api/v1/cart/items/<PRODUCT_ID> -H "Authorization: Bearer <TOKEN>" -H "Content-Type: application/json" -d '{"quantity":7}'
```

Expected response:

```http
204 No Content
```

### Remove item from cart

```bash
curl.exe -i -X DELETE http://localhost:8082/api/v1/cart/items/<PRODUCT_ID> -H "Authorization: Bearer <TOKEN>"
```

Expected response:

```http
204 No Content
```

### Clearing the whole cart

For clearing the whole cart, I would expose:

```http
DELETE /api/v1/cart
```

This endpoint would remove all items from the authenticated user's cart.
I would keep the cart record and remove only its items. This keeps the user's cart available as an empty cart for future operations.

## Health Checks

Both services expose Spring Boot Actuator health endpoints.

Product Service:

```md
http://localhost:8081/actuator/health
```

Cart Service:

```md
http://localhost:8082/actuator/health
```

Example response:

```json
{
  "status": "UP"
}
```

## Data Storage

Both services use separate H2 in-memory databases.

### Product Service database

```text
jdbc:h2:mem:productdb
```

### Cart Service database

```text
jdbc:h2:mem:cartdb
```

This keeps local setup simple and allows both services to be started without external infrastructure.

## Testing

The project includes unit tests for core service-layer logic.

### Product Service tests

Product Service tests cover:

- finding product by id,
- listing products,
- product not found scenarios.

### Cart Service tests

Cart Service tests cover:

- viewing cart,
- adding items to cart,
- updating item quantity,
- removing items from cart,
- product validation through Product Service client,
- unavailable product scenarios,
- missing cart item scenarios.

I intentionally focused the available time on delivering the required business flow and clear service boundaries. Controller and integration tests were left as future improvements to avoid adding partially implemented test coverage late in the assignment.

## Future Improvements

Due to the assignment timebox, I focused mainly on delivering the required business flow, service boundaries and basic production-minded features. There are several areas I would improve next:

- add MockMvc tests for REST controllers,
- add JWT security tests for protected Cart Service endpoints,
- add integration tests for communication between Cart Service and Product Service,
- add `DELETE /api/v1/cart` endpoint to clear all items from the authenticated user's cart,
- improve Docker setup with multi-stage builds,
- add pagination in Product Service,
- add Redis caching for retrieving single product details,
- improve logging with structured logs and correlation ids.

I am open to implementing these improvements after the review to make the system more secure, reliable and closer to a production-ready setup. I would also use this as a learning opportunity to gain more hands-on experience with Docker.