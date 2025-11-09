# Bill Payment API Specification

## Overview

This document provides the complete API specification for the Bill Payment feature, including request/response formats, error codes, and usage examples.

## Base URL

```
Frontend API Routes: /api/bill-payment
Backend API: {API_BASE_URL}/api/bill-payment
```

## Authentication

All API endpoints require authentication via Bearer token.

### Request Headers
```http
Authorization: Bearer {access_token}
Content-Type: application/json
```

### Token Storage
- Frontend: `localStorage.getItem('access_token')`
- Token is automatically included in all requests via `billPaymentService`

## Endpoints

---

## 1. Get Account Balance

Retrieves the current balance for a specified account.

### Endpoint
```http
GET /api/bill-payment/account/{accountId}/balance
```

### Path Parameters

| Parameter | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| accountId | string | Yes | Account identifier | Max 11 characters, alphanumeric |

### Request Example

```http
GET /api/bill-payment/account/12345678901/balance HTTP/1.1
Host: localhost:3000
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Response

#### Success Response (200 OK)

```json
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

**Response Schema:**
```typescript
interface AccountBalance {
  accountId: string;      // Account identifier (max 11 chars)
  currentBalance: number; // Current balance (2 decimal places)
}
```

#### Error Responses

**400 Bad Request - Empty Account ID**
```json
{
  "error": "Account ID cannot be empty"
}
```

**400 Bad Request - Invalid Account ID Length**
```json
{
  "error": "Account ID must be 11 characters or less"
}
```

**404 Not Found - Account Not Found**
```json
{
  "error": "Account ID NOT found..."
}
```

**500 Internal Server Error**
```json
{
  "error": "Failed to fetch account balance"
}
```

### Status Codes

| Code | Description |
|------|-------------|
| 200 | Success - Balance retrieved |
| 400 | Bad Request - Invalid input |
| 404 | Not Found - Account doesn't exist |
| 500 | Internal Server Error |

### Usage Example (TypeScript)

```typescript
import { billPaymentService } from '@/services/billPaymentService';

try {
  const balance = await billPaymentService.getAccountBalance('12345678901');
  console.log('Balance:', balance.currentBalance);
} catch (error) {
  console.error('Error:', error.message);
}
```

### cURL Example

```bash
curl -X GET \
  'http://localhost:3000/api/bill-payment/account/12345678901/balance' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json'
```

---

## 2. Process Bill Payment

Processes a bill payment for the specified account, paying the full balance.

### Endpoint
```http
POST /api/bill-payment/process
```

### Request Body

```json
{
  "accountId": "12345678901",
  "confirmPayment": true
}
```

**Request Schema:**
```typescript
interface BillPaymentRequest {
  accountId: string;      // Account identifier (required, max 11 chars)
  confirmPayment: boolean; // Payment confirmation (required, must be true)
}
```

### Request Example

```http
POST /api/bill-payment/process HTTP/1.1
Host: localhost:3000
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "accountId": "12345678901",
  "confirmPayment": true
}
```

### Response

#### Success Response (200 OK)

```json
{
  "transactionId": "0000000000000123",
  "message": "Payment successful. Your Transaction ID is 0000000000000123.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

**Response Schema:**
```typescript
interface BillPaymentResponse {
  transactionId: string;  // Generated transaction ID (max 16 chars)
  message: string;        // Success message
  accountId: string;      // Account identifier
  paymentAmount: number;  // Amount paid (2 decimal places)
  newBalance: number;     // New balance after payment (2 decimal places)
}
```

#### Error Responses

**400 Bad Request - Empty Account ID**
```json
{
  "error": "Acct ID can NOT be empty..."
}
```

**400 Bad Request - Account ID Too Long**
```json
{
  "error": "Account ID must be 11 characters or less"
}
```

**400 Bad Request - Invalid Confirmation**
```json
{
  "error": "Invalid value. Valid values are (Y/N)..."
}
```

**400 Bad Request - Payment Not Confirmed**
```json
{
  "error": "Confirm to make a bill payment..."
}
```

**400 Bad Request - Zero Balance**
```json
{
  "error": "You have nothing to pay..."
}
```

**404 Not Found - Account Not Found**
```json
{
  "error": "Account ID NOT found..."
}
```

**500 Internal Server Error - Transaction Failed**
```json
{
  "error": "Unable to Add Bill pay Transaction..."
}
```

**500 Internal Server Error - Generic**
```json
{
  "error": "Failed to process bill payment"
}
```

### Status Codes

| Code | Description |
|------|-------------|
| 200 | Success - Payment processed |
| 400 | Bad Request - Validation error or business rule violation |
| 404 | Not Found - Account doesn't exist |
| 500 | Internal Server Error - Transaction or update failed |

### Usage Example (TypeScript)

```typescript
import { billPaymentService } from '@/services/billPaymentService';

try {
  const result = await billPaymentService.processBillPayment({
    accountId: '12345678901',
    confirmPayment: true
  });
  
  console.log('Transaction ID:', result.transactionId);
  console.log('Payment Amount:', result.paymentAmount);
  console.log('New Balance:', result.newBalance);
} catch (error) {
  console.error('Payment failed:', error.message);
}
```

### cURL Example

```bash
curl -X POST \
  'http://localhost:3000/api/bill-payment/process' \
  -H 'Authorization: Bearer YOUR_TOKEN_HERE' \
  -H 'Content-Type: application/json' \
  -d '{
    "accountId": "12345678901",
    "confirmPayment": true
  }'
```

---

## Data Models

### AccountBalance

Represents the current balance of an account.

```typescript
interface AccountBalance {
  accountId: string;      // Account identifier
  currentBalance: number; // Current balance with 2 decimal places
}
```

**Validation Rules:**
- `accountId`: Required, max 11 characters, alphanumeric
- `currentBalance`: Required, number with 2 decimal places

**Example:**
```json
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

### BillPaymentRequest

Request payload for processing a bill payment.

```typescript
interface BillPaymentRequest {
  accountId: string;      // Account identifier
  confirmPayment: boolean; // Payment confirmation flag
}
```

**Validation Rules:**
- `accountId`: Required, not blank, max 11 characters
- `confirmPayment`: Required, must be boolean, must be true to process

**Example:**
```json
{
  "accountId": "12345678901",
  "confirmPayment": true
}
```

### BillPaymentResponse

Response payload after processing a bill payment.

```typescript
interface BillPaymentResponse {
  transactionId: string;  // Generated transaction identifier
  message: string;        // Success message
  accountId: string;      // Account identifier
  paymentAmount: number;  // Amount paid
  newBalance: number;     // New balance after payment
}
```

**Field Descriptions:**
- `transactionId`: Auto-generated, max 16 characters, numeric
- `message`: Success message with transaction ID
- `accountId`: Original account identifier
- `paymentAmount`: Amount paid (equals original balance)
- `newBalance`: New balance (should be 0.00 for full payment)

**Example:**
```json
{
  "transactionId": "0000000000000123",
  "message": "Payment successful. Your Transaction ID is 0000000000000123.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

---

## Business Rules

### Account Validation

1. **Account ID Format**
   - Must not be empty or null
   - Maximum length: 11 characters
   - Alphanumeric characters allowed
   - Leading/trailing spaces are trimmed

2. **Account Existence**
   - Account must exist in the system
   - Returns 404 if account not found

### Balance Validation

1. **Payment Eligibility**
   - Balance must be greater than zero
   - Zero or negative balance: "You have nothing to pay..."
   - Only positive balances can be paid

2. **Balance Format**
   - Decimal number with 2 decimal places
   - Formatted as currency in UI
   - Example: 1500.50 → $1,500.50

### Payment Confirmation

1. **Confirmation Required**
   - `confirmPayment` must be true
   - If false: "Confirm to make a bill payment..."
   - User must explicitly confirm payment

2. **Confirmation Workflow**
   - User reviews balance
   - User checks confirmation checkbox
   - User clicks process payment button
   - System validates confirmation

### Transaction Processing

1. **Transaction Details**
   - Type Code: '02'
   - Category Code: 2
   - Source: 'POS TERM'
   - Description: 'BILL PAYMENT - ONLINE'
   - Merchant ID: 999999999
   - Merchant Name: 'BILL PAYMENT'
   - Merchant City: 'N/A'
   - Merchant ZIP: 'N/A'

2. **Transaction ID Generation**
   - Auto-generated by backend
   - Increments from last transaction
   - Max 16 characters
   - Numeric format

3. **Timestamps**
   - Origin timestamp: Current timestamp
   - Process timestamp: Current timestamp
   - Format: ISO 8601 (YYYY-MM-DDTHH:mm:ss.sssZ)

### Balance Update

1. **Update Process**
   - Subtract payment amount from current balance
   - New balance = Current balance - Payment amount
   - For full payment: New balance = 0.00

2. **Atomicity**
   - Transaction creation and balance update are atomic
   - If either fails, both are rolled back
   - Ensures data consistency

---

## Error Handling

### Error Response Format

All errors follow a consistent format:

```json
{
  "error": "Error message description"
}
```

### Error Categories

#### Validation Errors (400)
- Empty or invalid account ID
- Payment not confirmed
- Invalid request format
- Business rule violations

#### Not Found Errors (404)
- Account doesn't exist
- Card cross-reference not found

#### Server Errors (500)
- Database connection failed
- Transaction creation failed
- Balance update failed
- Unexpected server error

### Error Messages

| Error Message | Cause | Resolution |
|--------------|-------|------------|
| "Account ID cannot be empty" | Empty account ID | Provide valid account ID |
| "Account ID must be 11 characters or less" | Account ID too long | Use valid account ID |
| "Account ID NOT found..." | Account doesn't exist | Verify account ID |
| "You have nothing to pay..." | Zero or negative balance | No action needed |
| "Confirm to make a bill payment..." | Payment not confirmed | Check confirmation |
| "Invalid value. Valid values are (Y/N)..." | Invalid confirmation value | Use boolean true/false |
| "Unable to Add Bill pay Transaction..." | Transaction creation failed | Retry or contact support |
| "Failed to fetch account balance" | Network or server error | Check connection, retry |
| "Failed to process bill payment" | Generic processing error | Retry or contact support |

---

## Rate Limiting

### Limits (Recommended)

- **Balance Retrieval:** 10 requests per minute per user
- **Payment Processing:** 5 requests per minute per user
- **Global:** 100 requests per minute per IP

### Rate Limit Headers

```http
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 9
X-RateLimit-Reset: 1640000000
```

### Rate Limit Exceeded Response

```json
{
  "error": "Rate limit exceeded. Please try again later.",
  "retryAfter": 60
}
```

---

## Security

### Authentication

- **Method:** Bearer Token
- **Header:** `Authorization: Bearer {token}`
- **Storage:** localStorage (development), httpOnly cookie (production)
- **Expiration:** Configurable (default: 1 hour)

### Authorization

- Users can only access their own accounts
- Admin users can access all accounts
- Role-based access control (RBAC)

### Data Protection

- All communication over HTTPS
- Input validation and sanitization
- SQL injection prevention
- XSS prevention
- CSRF protection

### Audit Logging

All payment transactions are logged with:
- User ID
- Account ID
- Transaction ID
- Amount
- Timestamp
- IP address
- User agent

---

## Testing

### Test Accounts

```json
[
  {
    "accountId": "12345678901",
    "currentBalance": 1500.50,
    "description": "Valid account with balance"
  },
  {
    "accountId": "98765432109",
    "currentBalance": 2750.00,
    "description": "Valid account with higher balance"
  },
  {
    "accountId": "11111111111",
    "currentBalance": 0.00,
    "description": "Account with zero balance"
  },
  {
    "accountId": "22222222222",
    "currentBalance": -100.00,
    "description": "Account with negative balance"
  },
  {
    "accountId": "99999999999",
    "description": "Non-existent account (404)"
  }
]
```

### Postman Collection

Import the following collection for testing:

```json
{
  "info": {
    "name": "Bill Payment API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get Account Balance",
      "request": {
        "method": "GET",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{access_token}}"
          }
        ],
        "url": {
          "raw": "{{base_url}}/api/bill-payment/account/12345678901/balance",
          "host": ["{{base_url}}"],
          "path": ["api", "bill-payment", "account", "12345678901", "balance"]
        }
      }
    },
    {
      "name": "Process Bill Payment",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Authorization",
            "value": "Bearer {{access_token}}"
          },
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"accountId\": \"12345678901\",\n  \"confirmPayment\": true\n}"
        },
        "url": {
          "raw": "{{base_url}}/api/bill-payment/process",
          "host": ["{{base_url}}"],
          "path": ["api", "bill-payment", "process"]
        }
      }
    }
  ]
}
```

---

## Changelog

### Version 1.0.0 (2024)
- Initial release
- Get account balance endpoint
- Process bill payment endpoint
- Complete error handling
- Business rules implementation

---

## Support

For API support or questions:
- **Email:** api-support@company.com
- **Documentation:** See BILL_PAYMENT_README.md
- **Issues:** Submit via issue tracker

---

**API Version:** 1.0.0  
**Last Updated:** 2024  
**Maintained By:** Wynxx System Modernization Team
