# Bill Payment System - Implementation Documentation

## Overview
This implementation provides a complete Bill Payment system based on the COBOL program COBIL00C from the CardDemo application. The system has been modernized using Spring Boot, following SOLID principles and best practices.

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: MySQL/PostgreSQL (configurable)
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **API Style**: RESTful

### Design Patterns
- **Repository Pattern**: Data access abstraction
- **Service Layer Pattern**: Business logic encapsulation
- **DTO Pattern**: Data transfer between layers
- **Exception Handling**: Centralized error handling with @RestControllerAdvice

## Project Structure

```
src/main/java/com/example/demo/
├── controller/
│   └── BillPaymentController.java
├── dto/
│   ├── AccountBalanceDTO.java
│   ├── AccountDTO.java
│   ├── BillPaymentRequestDTO.java
│   ├── BillPaymentResponseDTO.java
│   └── TransactionDTO.java
├── entity/
│   ├── Account.java
│   ├── CardCrossReference.java
│   └── Transaction.java
├── exception/
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── repository/
│   ├── AccountRepository.java
│   ├── CardCrossReferenceRepository.java
│   └── TransactionRepository.java
└── service/
    └── BillPaymentService.java

src/main/resources/
└── db/migration/
    └── V1__create_bill_payment_tables.sql
```

## Components Description

### Entities

#### Account
Represents customer account information.
- **Primary Key**: accountId (String, 11 characters)
- **Fields**: currentBalance, createdAt, updatedAt
- **Business Logic**: processPayment() method to update balance

#### Transaction
Represents bill payment transactions.
- **Primary Key**: transactionId (String, 16 characters)
- **Fields**: All transaction details including type, category, amount, merchant info, timestamps
- **Relationships**: Links to Account via accountId

#### CardCrossReference
Links card numbers to account IDs.
- **Primary Key**: id (Auto-generated Long)
- **Unique Index**: accountId
- **Purpose**: Retrieve card number for transaction recording

### DTOs

#### BillPaymentRequestDTO
- Validates account ID (not blank, max 11 chars)
- Requires payment confirmation (Boolean)

#### BillPaymentResponseDTO
- Returns transaction ID, success message, payment details
- Includes old and new balance information

#### AccountBalanceDTO
- Simple DTO for balance inquiry
- Contains accountId and currentBalance

### Repositories

All repositories extend `JpaRepository` providing:
- Standard CRUD operations
- Custom query methods
- Transaction management

#### TransactionRepository
- `findTopByOrderByTransactionIdDesc()`: Gets last transaction for ID generation
- `findByAccountId()`: Retrieves all transactions for an account

#### CardCrossReferenceRepository
- `findByAccountId()`: Looks up card number by account ID

### Service Layer

#### BillPaymentService
Implements core business logic:

1. **getAccountBalance()**
   - Validates account ID
   - Retrieves and returns current balance
   - Throws exception if account not found

2. **processBillPayment()**
   - Validates all inputs
   - Checks account balance > 0
   - Generates new transaction ID
   - Creates transaction record with predefined values
   - Updates account balance
   - Returns success response
   - All operations in single transaction (@Transactional)

3. **generateNewTransactionId()**
   - Reads last transaction
   - Increments ID by 1
   - Formats as 16-digit string with leading zeros
   - Returns "0000000000000001" if no transactions exist

### Controller Layer

#### BillPaymentController
Exposes REST endpoints:

1. **GET /api/bill-payment/account/{accountId}/balance**
   - Returns account balance
   - HTTP 200 on success, 404 if not found

2. **POST /api/bill-payment/process**
   - Processes bill payment
   - Validates request body
   - HTTP 200 on success, 400 for validation errors

### Exception Handling

#### GlobalExceptionHandler
Centralized exception handling:
- `IllegalArgumentException`: Returns 400 Bad Request
- `MethodArgumentNotValidException`: Returns validation errors
- `Exception`: Returns 500 Internal Server Error

## Business Rules Implementation

### Rule 1: Process-Enter-Key
Implemented in `BillPaymentService.processBillPayment()`
- Validates account ID
- Checks payment confirmation
- Validates balance > 0
- Processes payment if all checks pass

### Rule 2: GET-CURRENT-TIMESTAMP
Implemented using `LocalDateTime.now()`
- Sets both originTimestamp and processTimestamp
- Java's LocalDateTime provides microsecond precision

### Rule 3: READ-ACCTDAT-FILE
Implemented in `AccountRepository.findById()`
- Uses JPA to read account data
- Throws exception if not found
- Supports UPDATE mode via @Transactional

### Rule 4: UPDATE-ACCTDAT-FILE
Implemented in `Account.processPayment()` and repository save
- Updates balance atomically
- Transaction ensures consistency
- Returns success message with transaction ID

### Rule 5: READ-CXACAIX-FILE
Implemented in `CardCrossReferenceRepository.findByAccountId()`
- Retrieves card number for account
- Throws exception if not found

### Rule 6: WRITE-TRANSACT-FILE
Implemented in `TransactionRepository.save()`
- Creates new transaction record
- All fields populated per business rules
- Transaction type: '02'
- Category: 2
- Merchant ID: 999999999
- Merchant name: 'BILL PAYMENT'

## Database Schema

### Tables Created
1. **accounts**: Stores account information
2. **card_cross_reference**: Links cards to accounts
3. **transactions**: Records all bill payment transactions

### Relationships
- card_cross_reference.account_id → accounts.account_id
- transactions.account_id → accounts.account_id

### Indexes
- Primary keys on all tables
- Unique index on card_cross_reference.account_id
- Index on transactions.account_id for query performance

## API Usage Examples

### Get Account Balance
```bash
curl -X GET http://localhost:8080/api/bill-payment/account/12345678901/balance
```

Response:
```json
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

### Process Bill Payment
```bash
curl -X POST http://localhost:8080/api/bill-payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "12345678901",
    "confirmPayment": true
  }'
```

Response:
```json
{
  "transactionId": "0000000000000123",
  "message": "Payment successful. Your Transaction ID is 0000000000000123.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

## Error Scenarios

### Account Not Found
```json
{
  "status": 400,
  "message": "Account ID NOT found..."
}
```

### Nothing to Pay
```json
{
  "status": 400,
  "message": "You have nothing to pay..."
}
```

### Payment Not Confirmed
```json
{
  "status": 400,
  "message": "Confirm to make a bill payment..."
}
```

### Validation Error
```json
{
  "accountId": "Account ID cannot be empty",
  "confirmPayment": "Payment confirmation is required"
}
```

## Testing

### Unit Testing
Test each component independently:
- Service layer: Mock repositories
- Controller layer: Mock service
- Repository layer: Use @DataJpaTest

### Integration Testing
Test complete flow:
- Use @SpringBootTest
- Test database with test containers
- Verify transaction atomicity

### Sample Test Data
```sql
-- Insert test account
INSERT INTO accounts (account_id, current_balance) 
VALUES ('12345678901', 1500.50);

-- Insert card cross-reference
INSERT INTO card_cross_reference (account_id, card_number) 
VALUES ('12345678901', '4111111111111111');
```

## Configuration

### Application Properties
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/billpayment
spring.datasource.username=root
spring.datasource.password=password

# JPA Configuration
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway Migration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

## Deployment

### Prerequisites
- Java 17 or higher
- MySQL 8.0 or PostgreSQL 12+
- Maven 3.6+

### Build
```bash
mvn clean package
```

### Run
```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Docker
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

## Security Considerations

### To Be Implemented
1. **Authentication**: Add Spring Security with JWT
2. **Authorization**: Role-based access control
3. **Input Validation**: Enhanced validation rules
4. **Rate Limiting**: Prevent abuse
5. **Audit Logging**: Track all payment operations
6. **Encryption**: Encrypt sensitive data (card numbers)

## Performance Optimization

### Current Implementation
- Database indexes on foreign keys
- Transaction management for consistency
- DTO pattern to minimize data transfer

### Future Enhancements
1. **Caching**: Cache account lookups
2. **Connection Pooling**: Configure HikariCP
3. **Async Processing**: For non-critical operations
4. **Database Partitioning**: For large transaction tables

## Monitoring and Logging

### Recommended Tools
- **Spring Boot Actuator**: Health checks and metrics
- **Prometheus + Grafana**: Monitoring
- **ELK Stack**: Centralized logging
- **Sentry**: Error tracking

### Key Metrics to Monitor
- Payment success rate
- Average processing time
- Database connection pool usage
- Error rates by type

## Maintenance

### Database Migrations
Use Flyway for version control:
- All changes in versioned SQL files
- Automatic migration on startup
- Rollback support

### Code Quality
- SonarQube for code analysis
- JaCoCo for test coverage
- Checkstyle for code standards

## SOLID Principles Applied

1. **Single Responsibility**: Each class has one reason to change
2. **Open/Closed**: Extensible without modification
3. **Liskov Substitution**: Interfaces properly implemented
4. **Interface Segregation**: Focused repository interfaces
5. **Dependency Inversion**: Depend on abstractions (interfaces)

## Conclusion

This implementation successfully modernizes the COBOL bill payment system while maintaining all business rules and functionality. The architecture is scalable, maintainable, and follows industry best practices.

For questions or issues, please refer to the OpenAPI documentation or contact the development team.
