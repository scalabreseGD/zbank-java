# zBANK - Java Migration

A modern Java/Spring Boot implementation of the zBANK mainframe banking application, migrated from COBOL/CICS/VSAM to contemporary technology stack.

## 📋 Overview

This project represents the migration of the zBANK educational banking system from IBM mainframe technologies to a modern Java-based architecture.

### Original Technology Stack (COBOL/Mainframe)
- **Presentation Layer**: BMS (Basic Mapping Support) - 3270 terminal screens
- **Application Layer**: COBOL programs running in CICS transaction server
- **Data Layer**: VSAM KSDS (Key-Sequenced Dataset) files

### Modern Technology Stack (Java/Spring Boot)
- **Presentation Layer**: RESTful API (replaces BMS screens)
- **Application Layer**: Spring Boot 3.2 with Java 21
- **Data Layer**: JPA/Hibernate with H2 (dev) / PostgreSQL (prod)

## 🏗️ Architecture

The project follows a multi-module Maven structure:

```
zbank-java/
├── zbank-data/         # Data/Persistence layer (JPA entities, repositories)
├── zbank-service/      # Business logic layer (services, DTOs)
├── zbank-web/          # Web/REST API layer (controllers, config)
└── pom.xml             # Parent POM
```

### Module Responsibilities

#### zbank-data
- JPA entities (replaces COBOL data structures)
- Spring Data repositories (replaces VSAM operations)
- Database schema management

#### zbank-service
- Business logic (replaces COBOL procedures)
- DTOs for data transfer
- Custom exceptions
- Transaction management

#### zbank-web
- REST controllers (replaces BMS screens)
- Global exception handling
- API documentation (Swagger/OpenAPI)
- Application configuration

## 🚀 Getting Started

### Prerequisites

- Java 21 or later
- Maven 3.8+
- (Optional) PostgreSQL 14+ for production deployment

### Building the Project

```bash
# Build all modules
mvn clean install

# Run tests
mvn test

# Build without tests
mvn clean install -DskipTests
```

### Running the Application

```bash
# Run with H2 in-memory database (development)
cd zbank-web
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

The application will start on `http://localhost:8080/zbank`

## 📚 API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/zbank/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/zbank/api-docs
- **H2 Console** (dev only): http://localhost:8080/zbank/h2-console

## 🔧 Configuration

### Development (H2 Database)

The application uses H2 in-memory database by default with sample data pre-loaded.

**Sample accounts:**
- Account: `1234567890`, PIN: `1234`, Balance: $1,000.00
- Account: `9876543210`, PIN: `5678`, Balance: $5,000.50
- Account: `5555555555`, PIN: `0000`, Balance: $250.75

### Production (PostgreSQL)

Set the following environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=zbank
export DB_USERNAME=zbank_user
export DB_PASSWORD=your_secure_password
```

Run with production profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 🔌 API Endpoints

### Account Operations

#### Authenticate Account
```bash
POST /api/accounts/authenticate
Content-Type: application/json

{
  "accountNumber": "1234567890",
  "pin": "1234"
}
```

#### Get Balance
```bash
GET /api/accounts/1234567890/balance
```

#### Deposit Funds
```bash
POST /api/accounts/deposit
Content-Type: application/json

{
  "accountNumber": "1234567890",
  "pin": "1234",
  "amount": 500.00,
  "type": "DEPOSIT"
}
```

#### Withdraw Funds
```bash
POST /api/accounts/withdraw
Content-Type: application/json

{
  "accountNumber": "1234567890",
  "pin": "1234",
  "amount": 300.00,
  "type": "WITHDRAWAL"
}
```

#### List All Accounts (Admin)
```bash
GET /api/accounts
```

## 🔄 COBOL to Java Mapping

| COBOL Component | Java Equivalent | Description |
|----------------|-----------------|-------------|
| ZBANK.cbl | AccountService.java | Main business logic |
| LOGIN-PROCESS | authenticateAccount() | User authentication |
| BALANCE-INQUIRY | getBalance() | Balance inquiry |
| DEPOSIT-PROCESS | deposit() | Deposit transaction |
| WITHDRAWAL-PROCESS | withdraw() | Withdrawal transaction |
| VSAM KSDS | Account entity + JPA | Data persistence |
| BMS screens | REST API endpoints | User interface |
| CICS transaction | Spring MVC request | Request handling |

## 🧪 Testing

### Unit Tests

```bash
# Run all tests
mvn test

# Run specific module tests
mvn test -pl zbank-service
```

### Integration Tests

```bash
# Run integration tests
mvn verify
```

### Manual Testing with curl

```bash
# Authenticate
curl -X POST http://localhost:8080/zbank/api/accounts/authenticate \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"1234567890","pin":"1234"}'

# Check balance
curl http://localhost:8080/zbank/api/accounts/1234567890/balance

# Deposit
curl -X POST http://localhost:8080/zbank/api/accounts/deposit \
  -H "Content-Type: application/json" \
  -d '{"accountNumber":"1234567890","pin":"1234","amount":500.00,"type":"DEPOSIT"}'
```

## 📊 Monitoring

The application includes Spring Boot Actuator for monitoring:

- **Health**: http://localhost:8080/zbank/actuator/health
- **Info**: http://localhost:8080/zbank/actuator/info
- **Metrics**: http://localhost:8080/zbank/actuator/metrics

## 🔐 Security Notes

⚠️ **Current Implementation** (Development Only):
- PINs are stored in plain text
- No authentication/authorization framework
- No rate limiting or brute force protection

🔒 **Production Requirements**:
- Implement BCrypt for PIN hashing
- Add Spring Security with JWT tokens
- Implement rate limiting
- Add audit logging
- Use HTTPS/TLS
- Implement proper session management

## 📝 Known Limitations

1. **Authentication**: Simplified PIN verification (not production-ready)
2. **Concurrency**: No optimistic locking for concurrent transactions
3. **Audit Trail**: No transaction history logging
4. **Validation**: Basic validation only
5. **Error Handling**: Simplified error messages

## 🔗 Related Documentation

- [zBANK Architecture](https://griddynamics.atlassian.net/wiki/spaces/~5bf588cac337150e0ae6cee9/pages/3632005124)
- [zBANK Components](https://griddynamics.atlassian.net/wiki/spaces/~5bf588cac337150e0ae6cee9/pages/3642196022)
- [zBANK Integration](https://griddynamics.atlassian.net/wiki/spaces/~5bf588cac337150e0ae6cee9/pages/3631808516)
- [Jira Epic: RNDDE-1227](https://griddynamics.atlassian.net/browse/RNDDE-1227)

## 📅 Migration Status

- [x] Project structure and technology stack setup
- [ ] Data model implementation
- [ ] Business logic migration
- [ ] API documentation
- [ ] Testing framework
- [ ] CI/CD pipeline
- [ ] Deployment infrastructure

## 👥 Authors

- Migration Team: Grid Dynamics R&D
- Original COBOL System: Benjamin Linnik, Nicklas V., Henrik G.

## 📄 License

This is an educational project for demonstrating mainframe-to-cloud migration patterns.

---

**Note**: This is a migration of an educational project. The original COBOL implementation is available at [zBANK GitHub](https://github.com/Nantero1/zBANK).




