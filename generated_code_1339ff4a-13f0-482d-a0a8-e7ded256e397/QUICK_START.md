# Bill Payment System - Quick Start Guide

## 🚀 Get Started in 5 Minutes

This guide will help you quickly set up and run the Bill Payment system.

---

## Prerequisites

Before you begin, ensure you have:

- ✅ Java 17 or higher installed
- ✅ Maven 3.6+ installed
- ✅ MySQL 8.0 or PostgreSQL 12+ running
- ✅ Git (optional, for cloning)

### Check Your Environment

```bash
# Check Java version
java -version
# Should show: java version "17" or higher

# Check Maven version
mvn -version
# Should show: Apache Maven 3.6.x or higher

# Check MySQL
mysql --version
# Should show: mysql Ver 8.0.x or higher
```

---

## Step 1: Database Setup (2 minutes)

### Create Database

```sql
-- Connect to MySQL
mysql -u root -p

-- Create database
CREATE DATABASE billpayment;

-- Create user (optional)
CREATE USER 'billpay_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON billpayment.* TO 'billpay_user'@'localhost';
FLUSH PRIVILEGES;

-- Exit MySQL
exit;
```

---

## Step 2: Configure Application (1 minute)

### Create application.properties

```bash
# Copy template
cp src/main/resources/application.properties.template src/main/resources/application.properties

# Edit with your settings
nano src/main/resources/application.properties
```

### Minimal Configuration

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/billpayment
spring.datasource.username=root
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=validate

# Flyway
spring.flyway.enabled=true
```

---

## Step 3: Build Application (1 minute)

```bash
# Clean and build
mvn clean package

# Skip tests for quick start
mvn clean package -DskipTests
```

Expected output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 30 s
```

---

## Step 4: Run Application (30 seconds)

```bash
# Run the application
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Or use Maven
mvn spring-boot:run
```

Expected output:
```
Started DemoApplication in 5.234 seconds
```

---

## Step 5: Test the API (30 seconds)

### Insert Test Data

```sql
-- Connect to database
mysql -u root -p billpayment

-- Insert test account
INSERT INTO accounts (account_id, current_balance, created_at, updated_at) 
VALUES ('12345678901', 1500.50, NOW(), NOW());

-- Insert card cross-reference
INSERT INTO card_cross_reference (account_id, card_number, created_at, updated_at) 
VALUES ('12345678901', '4111111111111111', NOW(), NOW());
```

### Test Balance Endpoint

```bash
curl http://localhost:8080/api/bill-payment/account/12345678901/balance
```

Expected response:
```json
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

### Test Payment Endpoint

```bash
curl -X POST http://localhost:8080/api/bill-payment/process \
  -H "Content-Type: application/json" \
  -d '{
    "accountId": "12345678901",
    "confirmPayment": true
  }'
```

Expected response:
```json
{
  "transactionId": "0000000000000001",
  "message": "Payment successful. Your Transaction ID is 0000000000000001.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

---

## 🎉 Success!

Your Bill Payment system is now running!

---

## Common Issues & Solutions

### Issue 1: Port 8080 Already in Use

**Solution**: Change port in application.properties
```properties
server.port=8081
```

### Issue 2: Database Connection Failed

**Solution**: Check database is running
```bash
# Check MySQL status
sudo systemctl status mysql

# Start MySQL if needed
sudo systemctl start mysql
```

### Issue 3: Flyway Migration Failed

**Solution**: Reset database
```sql
DROP DATABASE billpayment;
CREATE DATABASE billpayment;
```

### Issue 4: Java Version Mismatch

**Solution**: Set JAVA_HOME
```bash
# Linux/Mac
export JAVA_HOME=/path/to/java17

# Windows
set JAVA_HOME=C:\path\to\java17
```

---

## Next Steps

### 1. Explore the API
- Read the OpenAPI documentation: `openapi-summary.md`
- Test all endpoints with Postman or curl

### 2. Review the Code
- Check the implementation guide: `BILL_PAYMENT_README.md`
- Understand the architecture and design patterns

### 3. Add More Test Data
```sql
-- Add more accounts
INSERT INTO accounts (account_id, current_balance, created_at, updated_at) 
VALUES 
  ('11111111111', 2500.00, NOW(), NOW()),
  ('22222222222', 3750.25, NOW(), NOW()),
  ('33333333333', 1000.00, NOW(), NOW());

-- Add corresponding card references
INSERT INTO card_cross_reference (account_id, card_number, created_at, updated_at) 
VALUES 
  ('11111111111', '4222222222222222', NOW(), NOW()),
  ('22222222222', '4333333333333333', NOW(), NOW()),
  ('33333333333', '4444444444444444', NOW(), NOW());
```

### 4. Enable Monitoring
```properties
# Add to application.properties
management.endpoints.web.exposure.include=health,info,metrics
```

Access health endpoint:
```bash
curl http://localhost:8080/actuator/health
```

### 5. Run Tests (when available)
```bash
mvn test
```

---

## Useful Commands

### Application Management
```bash
# Build without tests
mvn clean package -DskipTests

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run with debug
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### Database Management
```bash
# Backup database
mysqldump -u root -p billpayment > backup.sql

# Restore database
mysql -u root -p billpayment < backup.sql

# View tables
mysql -u root -p billpayment -e "SHOW TABLES;"

# View account data
mysql -u root -p billpayment -e "SELECT * FROM accounts;"
```

### Log Management
```bash
# View logs
tail -f logs/bill-payment.log

# Search logs
grep "ERROR" logs/bill-payment.log

# Clear logs
> logs/bill-payment.log
```

---

## API Quick Reference

### Get Account Balance
```bash
GET /api/bill-payment/account/{accountId}/balance
```

### Process Bill Payment
```bash
POST /api/bill-payment/process
Content-Type: application/json

{
  "accountId": "string",
  "confirmPayment": boolean
}
```

---

## Development Tips

### 1. Hot Reload (Spring Boot DevTools)
Add to pom.xml:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
</dependency>
```

### 2. Database GUI Tools
- **MySQL Workbench**: Visual database management
- **DBeaver**: Universal database tool
- **phpMyAdmin**: Web-based MySQL admin

### 3. API Testing Tools
- **Postman**: API testing and documentation
- **Insomnia**: REST client
- **curl**: Command-line tool

### 4. IDE Setup
- **IntelliJ IDEA**: Import as Maven project
- **Eclipse**: Import as existing Maven project
- **VS Code**: Install Java Extension Pack

---

## Production Checklist

Before deploying to production:

- [ ] Change default passwords
- [ ] Enable HTTPS
- [ ] Add authentication/authorization
- [ ] Configure proper logging
- [ ] Set up monitoring
- [ ] Configure backup strategy
- [ ] Review security settings
- [ ] Load test the application
- [ ] Document deployment process
- [ ] Set up CI/CD pipeline

---

## Getting Help

### Documentation
- **Implementation Guide**: `BILL_PAYMENT_README.md`
- **API Documentation**: `openapi-summary.md`
- **Project Summary**: `IMPLEMENTATION_SUMMARY.md`
- **Completion Report**: `PROJECT_COMPLETION_REPORT.md`

### Support
- Check the documentation first
- Review error logs
- Search for similar issues online
- Contact the development team

---

## Summary

You've successfully:
- ✅ Set up the database
- ✅ Configured the application
- ✅ Built and ran the service
- ✅ Tested the API endpoints

**Your Bill Payment system is ready to use!**

---

**Happy Coding! 🚀**
