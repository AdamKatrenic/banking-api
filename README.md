# Banking REST API

A production-ready banking platform built with Spring Boot, featuring secure JWT authentication, account management and financial transactions.

**Live API:** [banking-api-production-238c.up.railway.app/swagger-ui/index.html](https://banking-api-production-238c.up.railway.app/swagger-ui/index.html)

---

## Features

- **Authentication** — Register and login with JWT tokens
- **Account Management** — Create and manage bank accounts with unique account numbers
- **Transactions** — Deposit, withdraw and transfer funds between accounts
- **Transaction History** — Full history per account, sorted by date
- **Security** — BCrypt password hashing, stateless JWT authentication
- **API Documentation** — Interactive Swagger UI
- **Error Handling** — Global exception handler with descriptive error responses

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt) |
| Database | MySQL + Spring Data JPA + Hibernate |
| Documentation | Springdoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Deployment | Railway |

---

## Architecture

```
├── controller/        # REST endpoints (Auth, Account, Transaction)
├── service/           # Business logic
├── repository/        # JPA repositories
├── entity/            # JPA entities (User, Account, Transaction)
├── dto/               # Request/Response DTOs
├── security/          # JWT filter, Security config, UserDetailsService
└── exception/         # Global exception handler
```

Layered architecture: `Controller → Service → Repository → Database`

---

## API Endpoints

### Authentication
```
POST /api/auth/register    — Register new user, returns JWT token
POST /api/auth/login       — Login, returns JWT token
```

### Accounts
```
POST /api/accounts         — Create new bank account (requires auth)
GET  /api/accounts         — Get all accounts for logged-in user
```

### Transactions
```
POST /api/transactions/deposit      — Deposit funds
POST /api/transactions/withdraw     — Withdraw funds
POST /api/transactions/transfer     — Transfer between accounts
GET  /api/transactions/history/{accountNumber} — Transaction history
```

---

## Getting Started

### Prerequisites
- Java 21+
- MySQL 8+
- Maven

### Setup

```bash
# Clone the repository
git clone https://github.com/AdamKatrenic/banking-api.git
cd banking-api
```

Create a MySQL database:
```sql
CREATE DATABASE banking_api;
```

Configure `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/banking_api
spring.datasource.username=your_username
spring.datasource.password=your_password
jwt.secret=yourSecretKeyMinimum32CharactersLong
jwt.expiration=86400000
```

Run the application:
```bash
./mvnw spring-boot:run
```

Open Swagger UI:
```
http://localhost:8080/swagger-ui/index.html
```

---

## Usage Example

**1. Register**
```json
POST /api/auth/register
{
  "fullName": "Adam Katrenic",
  "email": "adam@example.com",
  "password": "password123"
}
```

**2. Use the token in all subsequent requests**
```
Authorization: Bearer eyJhbGciOiJIUzM4NCJ9...
```

**3. Create a bank account**
```
POST /api/accounts
```

**4. Deposit funds**
```json
POST /api/transactions/deposit
{
  "accountNumber": "SKB3594DB0404E4E80",
  "amount": 500.00
}
```

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key (min. 32 chars) |
| `JWT_EXPIRATION` | Token expiration in ms |
| `PORT` | Server port (default: 8080) |

---

## Author

**Adam Katrenič** — Junior Java Developer

[GitHub](https://github.com/AdamKatrenic) · [LinkedIn](https://linkedin.com/in/adam-katrenic-a730a5406) · [Live Demo](https://banking-api-production-238c.up.railway.app/swagger-ui/index.html)
