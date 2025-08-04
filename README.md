# 🛍️ Product Manager API

A Spring Boot RESTful API for managing products, with full support for authentication, authorization, PostgreSQL (Dockerized), and standardized responses using a generic wrapper.

---

## 🚀 Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Security with JWT
- PostgreSQL
- Spring Data JPA
- Maven
- Lombok
- Jakarta Validation
- TestRestTemplate + JUnit 5
- Swagger 3 (OpenAPI)
- Docker Compose

## 📦 Project Structure

```text
src/
├── main/
│   ├── java/com/product/manager/
│   │   ├── controller/        # REST Controllers
│   │   ├── dto/               # DTOs (ProductDto, UpdateProductDto, GenericResponse, AuthRequest, etc.)
│   │   ├── entity/            # JPA Entities
│   │   ├── repository/        # Spring Data JPA Repositories
│   │   ├── service/           # Business Logic
│   │   ├── config/            # Security and application config
│   │   └── exception/         # Global error handling
│
└── test/
    └── java/com/product/manager/
        └── controller/        # Integration tests using TestRestTemplate
        └── service/           # Unit tests for services
```


---

### 🔐 Block 3 — Authentication & Authorization


## 🔐 Authentication & Authorization

This application uses **JWT-based authentication** with **role-based authorization** via Spring Security.

JWT is expected in the `Authorization` header as:

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjpbIkFETUlOIl0sImlhdCI6MTc1NDMyOTM4MCwiZXhwIjoxNzU0NDE1
NzgwfQ.piWUWPySFe83OgnBHeTDa5QcJNzMQB0RVIxIMiA8Lck`

### 🔑 Endpoints

| Endpoint                   | Access       |
|----------------------------|--------------|
| `POST /auth/register`      | Public       |
| `POST /auth/login`         | Public       |
| `GET /api/v1/product/list` | `ROLE_USER` or `ROLE_ADMIN` |
| `POST /api/v1/product/add` | `ROLE_ADMIN` |
| ... and others             | Role-based   |


## 📊 API Endpoints

- `POST   /api/v1/product/add`    – Add new product
- `GET    /api/v1/product/{code}` – Get product by code
- `PUT    /api/v1/product/change` – Fully update product
- `PATCH  /api/v1/product/{code}/update` – Partial update
- `DELETE /api/v1/product/{code}` – Delete product
- `GET    /api/v1/product/list`   – List all products
- `PUT    /api/v1/product/{code}/change/{price}` – Change product price

## 🔁 Standard Response Wrapper

All API responses use the same structure, defined by:

```java
public record GenericResponse<T>(
    @Min(100) @Max(599) int statusCode,
    @NotBlank String message,
    T data,
    boolean success
) {}
```

This ensures consistency across all endpoints, making it easier for clients to handle responses.



```json
✅Success Response:

{
    "statusCode": 200,
    "success": true
    "message": "Product added successfully",
    "data": {
        "code": "P12345",
        "name": "Sample Product",
        "price": 99.99
    }
}

❌Error Response:

{
  "status": 404,
  "success": false,
  "message": "Product with code 1122334 not found.",
  "data": null
}
```
## 🐳 Docker Setup
This project includes a `docker-compose.yml` file to run PostgreSQL in a Docker container. To start the database, run:

```bash
docker-compose up -d
```

Make sure you have Docker installed and running. The application will connect to the PostgreSQL database using the credentials defined in `application.yml`.

## 🧪 Testing

Integration tests are written with:

- `TestRestTemplate`
- `PostgreSQL` (Docker container)
- `@AfterEach` cleanup logic
- `GenericResponse<T>` verification
- Full role-based security context

## 📫 Postman Collection

A Postman collection is provided to test all endpoints. You can import it into Postman to quickly start testing the API.
You can find the collection in source directory with name: product_postman_collection.json .

## ▶️ Running the App

To run the application, you need to add in VM options the following:

```text
-Djwt.secret-key=your_secret_key
```

## 🤝 Author
Made by Gigi Florin Ganesanu