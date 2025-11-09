# Bill Payment Feature - Testing Guide

## Overview

This document outlines the testing strategy and test cases for the Bill Payment feature. It covers unit tests, integration tests, and end-to-end testing scenarios.

## Test Coverage Areas

1. Type Definitions and Utilities
2. API Routes
3. Service Layer
4. UI Components
5. User Flows
6. Error Handling
7. Edge Cases

## Test Cases

### 1. Type Definitions (`billPayment.ts`)

#### Test: formatCurrency
```typescript
// Test cases:
- formatCurrency(1500.50) → "$1,500.50"
- formatCurrency(0) → "$0.00"
- formatCurrency(1000000) → "$1,000,000.00"
- formatCurrency(-500.25) → "-$500.25"
```

#### Test: formatAccountId
```typescript
// Test cases:
- formatAccountId("12345678901") → "123-456-789-01"
- formatAccountId("123") → "123"
- formatAccountId("123456") → "123-456"
- formatAccountId("abc123def456") → "123-456" (strips non-digits)
```

#### Test: canProcessPayment
```typescript
// Test cases:
- canProcessPayment(1500.50) → true
- canProcessPayment(0) → false
- canProcessPayment(-100) → false
- canProcessPayment(0.01) → true
```

### 2. API Routes

#### Test: GET /api/bill-payment/account/[accountId]/balance

**Test Case 1: Valid Account ID**
```
Request: GET /api/bill-payment/account/12345678901/balance
Expected Response: 200 OK
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

**Test Case 2: Empty Account ID**
```
Request: GET /api/bill-payment/account//balance
Expected Response: 400 Bad Request
{
  "error": "Account ID cannot be empty"
}
```

**Test Case 3: Account ID Too Long**
```
Request: GET /api/bill-payment/account/123456789012/balance
Expected Response: 400 Bad Request
{
  "error": "Account ID must be 11 characters or less"
}
```

**Test Case 4: Account Not Found**
```
Request: GET /api/bill-payment/account/99999999999/balance
Expected Response: 404 Not Found
{
  "error": "Account ID NOT found..."
}
```

**Test Case 5: Server Error**
```
Request: GET /api/bill-payment/account/12345678901/balance
Backend Down
Expected Response: 500 Internal Server Error
{
  "error": "Failed to fetch account balance"
}
```

#### Test: POST /api/bill-payment/process

**Test Case 1: Successful Payment**
```
Request: POST /api/bill-payment/process
Body: {
  "accountId": "12345678901",
  "confirmPayment": true
}
Expected Response: 200 OK
{
  "transactionId": "0000000000000123",
  "message": "Payment successful. Your Transaction ID is 0000000000000123.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

**Test Case 2: Empty Account ID**
```
Request: POST /api/bill-payment/process
Body: {
  "accountId": "",
  "confirmPayment": true
}
Expected Response: 400 Bad Request
{
  "error": "Acct ID can NOT be empty..."
}
```

**Test Case 3: Payment Not Confirmed**
```
Request: POST /api/bill-payment/process
Body: {
  "accountId": "12345678901",
  "confirmPayment": false
}
Expected Response: 400 Bad Request
{
  "error": "Confirm to make a bill payment..."
}
```

**Test Case 4: Zero Balance**
```
Request: POST /api/bill-payment/process
Body: {
  "accountId": "12345678901",
  "confirmPayment": true
}
Backend returns: Balance is 0
Expected Response: 400 Bad Request
{
  "error": "You have nothing to pay..."
}
```

**Test Case 5: Account Not Found**
```
Request: POST /api/bill-payment/process
Body: {
  "accountId": "99999999999",
  "confirmPayment": true
}
Expected Response: 404 Not Found
{
  "error": "Account ID NOT found..."
}
```

### 3. Service Layer (`billPaymentService.ts`)

#### Test: getAccountBalance

**Test Case 1: Successful Retrieval**
```typescript
const accountId = "12345678901";
const result = await billPaymentService.getAccountBalance(accountId);
expect(result).toEqual({
  accountId: "12345678901",
  currentBalance: 1500.50
});
```

**Test Case 2: Network Error**
```typescript
const accountId = "12345678901";
// Mock fetch to throw error
await expect(
  billPaymentService.getAccountBalance(accountId)
).rejects.toThrow("Failed to fetch account balance");
```

**Test Case 3: 404 Response**
```typescript
const accountId = "99999999999";
// Mock fetch to return 404
await expect(
  billPaymentService.getAccountBalance(accountId)
).rejects.toThrow("Account ID NOT found...");
```

#### Test: processBillPayment

**Test Case 1: Successful Payment**
```typescript
const request = {
  accountId: "12345678901",
  confirmPayment: true
};
const result = await billPaymentService.processBillPayment(request);
expect(result).toHaveProperty("transactionId");
expect(result).toHaveProperty("message");
expect(result.newBalance).toBe(0);
```

**Test Case 2: Payment Not Confirmed**
```typescript
const request = {
  accountId: "12345678901",
  confirmPayment: false
};
await expect(
  billPaymentService.processBillPayment(request)
).rejects.toThrow("Confirm to make a bill payment...");
```

### 4. UI Component Testing

#### Test: Bill Payment Page

**Test Case 1: Initial Render**
```typescript
- Page title is displayed
- Account ID input is visible
- Retrieve Balance button is enabled
- No error messages shown
- No balance information displayed
```

**Test Case 2: Account ID Input**
```typescript
- User can type in account ID field
- Input accepts alphanumeric characters
- Input is limited to 11 characters
- Form validation triggers on submit
```

**Test Case 3: Retrieve Balance - Success**
```typescript
- Enter valid account ID
- Click Retrieve Balance
- Loading state is shown
- Balance information is displayed
- Payment confirmation section appears (if balance > 0)
```

**Test Case 4: Retrieve Balance - Error**
```typescript
- Enter invalid account ID
- Click Retrieve Balance
- Error message is displayed
- Balance information is not shown
- Payment section is not shown
```

**Test Case 5: Payment Confirmation**
```typescript
- Balance is displayed
- Confirmation checkbox is visible
- Process Payment button is disabled initially
- Check confirmation checkbox
- Process Payment button becomes enabled
```

**Test Case 6: Process Payment - Success**
```typescript
- Confirm payment
- Click Process Payment
- Loading state is shown
- Success message is displayed
- Transaction ID is shown
- Form is reset
```

**Test Case 7: Process Payment - Error**
```typescript
- Confirm payment
- Click Process Payment
- Error occurs
- Error message is displayed
- Form state is preserved
- User can retry
```

**Test Case 8: Zero Balance**
```typescript
- Retrieve balance for account with $0 balance
- "You have nothing to pay" message is shown
- Payment confirmation section is not shown
- Process Payment button is not available
```

**Test Case 9: Cancel Payment**
```typescript
- Retrieve balance
- Click Cancel button
- Form is reset
- Balance information is cleared
- User can start over
```

**Test Case 10: Make Another Payment**
```typescript
- Complete successful payment
- Click "Make Another Payment" button
- Form is reset
- Success message is cleared
- User can enter new account ID
```

### 5. Integration Tests

#### Test: End-to-End Payment Flow

**Scenario 1: Happy Path**
```
1. User navigates to /bill-payment
2. User enters account ID "12345678901"
3. User clicks "Retrieve Balance"
4. System displays balance $1,500.50
5. User checks confirmation checkbox
6. User clicks "Process Payment"
7. System processes payment
8. System displays success message with transaction ID
9. New balance is $0.00
```

**Scenario 2: Account Not Found**
```
1. User navigates to /bill-payment
2. User enters account ID "99999999999"
3. User clicks "Retrieve Balance"
4. System displays error "Account ID NOT found..."
5. Balance information is not shown
6. User can try different account ID
```

**Scenario 3: Zero Balance**
```
1. User navigates to /bill-payment
2. User enters account ID with $0 balance
3. User clicks "Retrieve Balance"
4. System displays balance $0.00
5. System displays "You have nothing to pay..."
6. Payment confirmation is not available
```

**Scenario 4: Payment Without Confirmation**
```
1. User navigates to /bill-payment
2. User enters account ID and retrieves balance
3. User clicks "Process Payment" without checking confirmation
4. System displays error "Confirm to make a bill payment..."
5. Payment is not processed
```

### 6. Error Handling Tests

#### Test: Network Errors

**Test Case 1: API Timeout**
```
- Simulate API timeout
- Verify error message is displayed
- Verify user can retry
```

**Test Case 2: Connection Lost**
```
- Simulate network disconnection
- Verify appropriate error message
- Verify graceful degradation
```

**Test Case 3: Invalid Response**
```
- Simulate malformed API response
- Verify error handling
- Verify no application crash
```

#### Test: Validation Errors

**Test Case 1: Client-Side Validation**
```
- Empty account ID
- Invalid characters
- Account ID too long
- Verify validation messages
```

**Test Case 2: Server-Side Validation**
```
- Invalid account format
- Account not found
- Business rule violations
- Verify error messages
```

### 7. Edge Cases

#### Test: Boundary Values

**Test Case 1: Maximum Account ID Length**
```
Account ID: "12345678901" (11 characters)
Expected: Valid
```

**Test Case 2: Minimum Balance**
```
Balance: $0.01
Expected: Payment can be processed
```

**Test Case 3: Large Balance**
```
Balance: $999,999,999.99
Expected: Formatted correctly, payment processes
```

**Test Case 4: Negative Balance**
```
Balance: -$100.00
Expected: "You have nothing to pay" message
```

#### Test: Concurrent Operations

**Test Case 1: Multiple Rapid Clicks**
```
- Click "Process Payment" multiple times rapidly
- Verify only one payment is processed
- Verify button is disabled during processing
```

**Test Case 2: Browser Back Button**
```
- Complete payment
- Click browser back button
- Verify state is handled correctly
- Verify no duplicate payment
```

#### Test: Session Management

**Test Case 1: Token Expiration**
```
- Start payment process
- Token expires
- Verify user is redirected to login
- Verify payment is not lost
```

**Test Case 2: Logout During Payment**
```
- Start payment process
- User logs out
- Verify session is cleared
- Verify secure cleanup
```

### 8. Performance Tests

#### Test: Load Time

**Test Case 1: Initial Page Load**
```
- Measure time to interactive
- Target: < 2 seconds
```

**Test Case 2: API Response Time**
```
- Measure balance retrieval time
- Target: < 1 second
- Measure payment processing time
- Target: < 2 seconds
```

#### Test: Stress Testing

**Test Case 1: Multiple Concurrent Users**
```
- Simulate 100 concurrent users
- Verify system stability
- Verify response times
```

**Test Case 2: Rapid Successive Requests**
```
- Submit multiple requests in quick succession
- Verify rate limiting
- Verify no data corruption
```

### 9. Accessibility Tests

#### Test: Keyboard Navigation

**Test Case 1: Tab Order**
```
- Verify logical tab order
- All interactive elements are reachable
- Focus indicators are visible
```

**Test Case 2: Keyboard Shortcuts**
```
- Enter key submits form
- Escape key cancels
- Space toggles checkbox
```

#### Test: Screen Reader

**Test Case 1: Form Labels**
```
- All inputs have labels
- Error messages are announced
- Success messages are announced
```

**Test Case 2: ARIA Attributes**
```
- Proper ARIA roles
- ARIA live regions for dynamic content
- ARIA labels for icons
```

### 10. Security Tests

#### Test: Input Sanitization

**Test Case 1: XSS Prevention**
```
- Inject script tags in account ID
- Verify scripts are not executed
- Verify input is sanitized
```

**Test Case 2: SQL Injection**
```
- Inject SQL commands in account ID
- Verify commands are not executed
- Verify input is sanitized
```

#### Test: Authentication

**Test Case 1: Unauthorized Access**
```
- Access without token
- Verify redirect to login
- Verify no data exposure
```

**Test Case 2: Token Validation**
```
- Use invalid token
- Verify request is rejected
- Verify appropriate error message
```

## Test Execution

### Manual Testing Checklist

- [ ] All happy path scenarios work
- [ ] All error scenarios are handled
- [ ] All edge cases are covered
- [ ] UI is responsive on all devices
- [ ] Accessibility requirements are met
- [ ] Performance targets are met
- [ ] Security measures are in place

### Automated Testing

```bash
# Run unit tests
npm run test

# Run integration tests
npm run test:integration

# Run e2e tests
npm run test:e2e

# Run all tests with coverage
npm run test:coverage
```

### Test Data

#### Valid Test Accounts
```
Account ID: 12345678901, Balance: $1,500.50
Account ID: 98765432109, Balance: $2,750.00
Account ID: 11111111111, Balance: $0.00
Account ID: 22222222222, Balance: -$100.00
```

#### Invalid Test Accounts
```
Account ID: 99999999999 (Not Found)
Account ID: 00000000000 (Invalid)
```

## Continuous Integration

### CI/CD Pipeline

1. **Pre-commit Hooks**
   - Lint code
   - Format code
   - Run unit tests

2. **Pull Request Checks**
   - Run all tests
   - Check code coverage
   - Verify build succeeds

3. **Deployment Pipeline**
   - Run integration tests
   - Run e2e tests
   - Deploy to staging
   - Run smoke tests
   - Deploy to production

## Bug Reporting

### Bug Report Template

```
Title: [Brief description]

Environment:
- Browser: [Chrome/Firefox/Safari/Edge]
- Version: [Browser version]
- OS: [Windows/Mac/Linux]
- Device: [Desktop/Mobile/Tablet]

Steps to Reproduce:
1. [Step 1]
2. [Step 2]
3. [Step 3]

Expected Result:
[What should happen]

Actual Result:
[What actually happened]

Screenshots:
[Attach screenshots if applicable]

Console Errors:
[Paste any console errors]

Additional Notes:
[Any other relevant information]
```

## Test Metrics

### Coverage Targets
- Unit Tests: > 80%
- Integration Tests: > 70%
- E2E Tests: > 60%
- Overall Coverage: > 75%

### Quality Gates
- All tests must pass
- No critical bugs
- No security vulnerabilities
- Performance targets met
- Accessibility standards met

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**Maintained By:** QA Team
