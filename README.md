# Banking REST API

A production-ready banking platform built with Java and Spring Boot, featuring JWT authentication, account management, financial transactions and email notifications.

**Live API:** [banking-api-production-238c.up.railway.app/swagger-ui/index.html](https://banking-api-production-238c.up.railway.app/swagger-ui/index.html)  
**Frontend:** [banking-frontend-steel.vercel.app](https://banking-frontend-steel.vercel.app)

---

## Features

- **Authentication** — Register and login with JWT tokens, BCrypt password hashing
- **Account Management** — Create and manage bank accounts with unique account numbers (€10,000 starting balance)
- **Transactions** — Deposit, withdraw and transfer funds between accounts
- **Transaction History** — Full history per account with CSV export
- **Email Notifications** — Async email notifications on every transaction via Gmail SMTP
- **Rate Limiting** — Bucket4j rate limiting (20 requests/min per IP) to prevent brute force attacks
- **Security** — Spring Security, stateless JWT, BCrypt, CORS configuration
- **API Documentation** — Interactive Swagger UI
- **Error Handling** — Global exception handler with descriptive error responses
- **Testing** — 33 unit and integration tests (JUnit 5, Mockito, MockMvc)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT (jjwt 0.12.3) |
| Database | MySQL + Spring Data JPA + Hibernate |
| Rate Limiting | Bucket4j |
| Email | Spring Mail + Gmail SMTP |
| Documentation | Springdoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + MockMvc |
| Build Tool | Maven |
| Containerization | Docker + Docker Compose |
| Deployment | Railway |

---

## Architecture

```
├── controller/        # REST endpoints (Auth, Account, Transaction, Export)
├── service/           # Business logic (Auth, Account, Transaction, Email, Export)
├── repository/        # JPA repositories
├── entity/            # JPA entities (User, Account, Transaction)
├── dto/               # Request/Response DTOs
├── security/          # JWT filter, Security config, UserDetailsService, RateLimitFilter
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

### Export
```
GET  /api/export/transactions/{accountNumber} — Export transactions as CSV
```

---

## Testing

33 tests covering the full application:

```
AccountServiceTest         — 3 tests
TransactionServiceTest     — 5 tests
AuthServiceTest            — 4 tests
JwtUtilTest                — 4 tests
RateLimitFilterTest        — 3 tests
GlobalExceptionHandlerTest — 3 tests
AuthControllerTest         — 4 tests
AccountControllerTest      — 3 tests
TransactionControllerTest  — 4 tests
```

Run all tests:
```bash
./mvnw test
```

---

## Getting Started

### Prerequisites
- Java 21+
- MySQL 8+
- Maven

### Setup

```bash
git clone https://github.com/AdamKatrenic/banking-api.git
cd banking-api
```

Create a MySQL database:
```sql
CREATE DATABASE banking_api;
```

Configure environment variables:
```
DB_URL=jdbc:mysql://localhost:3306/banking_api
DB_USERNAME=your_username
DB_PASSWORD=your_password
JWT_SECRET=yourSecretKeyMinimum32CharactersLong
JWT_EXPIRATION=86400000
MAIL_USERNAME=your@gmail.com
MAIL_PASSWORD=your_app_password
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

### Docker Compose

Run the full stack with one command:

```bash
docker-compose up
```

This starts both the application and MySQL database.

---

## Environment Variables

| Variable | Description |
|---|---|
| `DB_URL` | JDBC connection URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Secret key (min. 32 chars) |
| `JWT_EXPIRATION` | Token expiration in ms |
| `MAIL_USERNAME` | Gmail address for notifications |
| `MAIL_PASSWORD` | Gmail App Password |
| `PORT` | Server port (default: 8080) |

---

## Author

**Adam Katrenič** — Junior Java & Fullstack Developer

[GitHub](https://github.com/AdamKatrenic) · [LinkedIn](https://linkedin.com/in/adam-katrenic-a730a5406) · [Live Demo](https://banking-api-production-238c.up.railway.app/swagger-ui/index.html)
