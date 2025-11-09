# Bill Payment Feature Documentation

## Overview

The Bill Payment feature is a comprehensive online bill payment processing system that allows users to pay their account balance in full. This feature is part of the CardDemo application and follows the COBOL program COBIL00C business rules.

## Features

### Core Functionality

1. **Account Balance Retrieval**
   - Users can enter their account ID to retrieve current balance
   - Real-time balance display with formatted currency
   - Account validation and error handling

2. **Payment Processing**
   - Pay account balance in full
   - Payment confirmation requirement
   - Transaction ID generation
   - Success/error message display

3. **Business Rules Implementation**
   - Account ID validation (max 11 characters)
   - Balance validation (must be positive to process payment)
   - Payment confirmation required
   - Automatic balance update after payment
   - Transaction recording with timestamp

## File Structure

```
src/
├── types/
│   └── billPayment.ts              # TypeScript interfaces and utility functions
├── services/
│   └── billPaymentService.ts       # API client service
├── app/
│   ├── api/
│   │   └── bill-payment/
│   │       ├── account/
│   │       │   └── [accountId]/
│   │       │       └── balance/
│   │       │           └── route.ts    # GET balance endpoint
│   │       └── process/
│   │           └── route.ts            # POST payment endpoint
│   └── bill-payment/
│       └── page.tsx                    # Main bill payment page
```

## API Endpoints

### 1. Get Account Balance

**Endpoint:** `GET /api/bill-payment/account/{accountId}/balance`

**Description:** Retrieves the current balance for a specified account.

**Parameters:**
- `accountId` (path parameter, required): Account identifier (max 11 characters)

**Response (200 OK):**
```json
{
  "accountId": "12345678901",
  "currentBalance": 1500.50
}
```

**Error Responses:**
- `400 Bad Request`: Invalid account ID
- `404 Not Found`: Account not found
- `500 Internal Server Error`: Server error

### 2. Process Bill Payment

**Endpoint:** `POST /api/bill-payment/process`

**Description:** Processes a bill payment for the specified account.

**Request Body:**
```json
{
  "accountId": "12345678901",
  "confirmPayment": true
}
```

**Response (200 OK):**
```json
{
  "transactionId": "0000000000000123",
  "message": "Payment successful. Your Transaction ID is 0000000000000123.",
  "accountId": "12345678901",
  "paymentAmount": 1500.50,
  "newBalance": 0.00
}
```

**Error Responses:**
- `400 Bad Request`: Validation error or business rule violation
- `404 Not Found`: Account not found
- `500 Internal Server Error`: Server error

## Business Rules

### Account Validation
1. Account ID must not be empty
2. Account ID must be 11 characters or less
3. Account must exist in the system

### Balance Validation
1. Balance must be greater than zero to process payment
2. If balance is zero or negative, display "You have nothing to pay"

### Payment Confirmation
1. User must confirm payment by checking the confirmation checkbox
2. If not confirmed, display "Confirm to make a bill payment..."

### Transaction Processing
1. Transaction ID is auto-generated
2. Transaction type code: '02'
3. Transaction category code: 2
4. Transaction source: 'POS TERM'
5. Transaction description: 'BILL PAYMENT - ONLINE'
6. Payment amount equals full current balance
7. Merchant ID: 999999999
8. Merchant name: 'BILL PAYMENT'
9. Both origin and process timestamps set to current timestamp

### Balance Update
1. After successful transaction, account balance is reduced by payment amount
2. New balance should be zero after paying full balance
3. Transaction and account update are atomic

## User Interface

### Main Page Components

1. **Header Section**
   - Program name (COBIL00C)
   - Current date and time display
   - Page title and description

2. **Account Information Form**
   - Account ID input field
   - Retrieve Balance button
   - Real-time validation

3. **Balance Display**
   - Account ID (formatted)
   - Current balance (formatted as currency)
   - Balance status indicator

4. **Payment Confirmation**
   - Payment amount summary
   - Confirmation checkbox
   - Process Payment button
   - Cancel button

5. **Success Message**
   - Transaction ID
   - Payment details
   - New balance
   - Success message
   - Option to make another payment

6. **Error Handling**
   - Clear error messages
   - Contextual error display
   - User-friendly error descriptions

## Usage Flow

1. **User enters account ID**
   - Input validation occurs
   - Click "Retrieve Balance" button

2. **System retrieves balance**
   - Loading state displayed
   - Balance information shown
   - Error handling if account not found

3. **User reviews balance**
   - If balance > 0: Payment confirmation section appears
   - If balance ≤ 0: "Nothing to pay" message shown

4. **User confirms payment**
   - Check confirmation checkbox
   - Review payment details
   - Click "Process Payment" button

5. **System processes payment**
   - Loading state during processing
   - Transaction created
   - Account balance updated

6. **Success confirmation**
   - Transaction ID displayed
   - Payment details shown
   - Option to make another payment

## Error Handling

### Client-Side Validation
- Empty account ID
- Invalid account ID format
- Missing payment confirmation

### Server-Side Errors
- Account not found (404)
- Invalid account ID (400)
- Zero or negative balance (400)
- Payment not confirmed (400)
- Transaction processing failure (500)
- Account update failure (500)

## Styling and UX

### Color Scheme
- Success: Green (green-50, green-500, green-600)
- Error: Red (red-50, red-500, red-600)
- Warning: Yellow (yellow-50, yellow-300, yellow-600)
- Info: Blue (blue-50, blue-200, blue-600)
- Neutral: Gray (gray-50, gray-200, gray-600)

### Responsive Design
- Mobile-first approach
- Responsive grid layout
- Touch-friendly buttons
- Accessible form controls

### Loading States
- Button disabled during loading
- Loading text indicators
- Prevent duplicate submissions

### Accessibility
- Semantic HTML
- ARIA labels where needed
- Keyboard navigation support
- Screen reader friendly

## Testing Scenarios

### Happy Path
1. Enter valid account ID
2. Retrieve balance successfully
3. Confirm payment
4. Process payment successfully
5. Display success message

### Error Scenarios
1. Empty account ID
2. Invalid account ID format
3. Account not found
4. Zero balance
5. Negative balance
6. Payment not confirmed
7. Network error
8. Server error

### Edge Cases
1. Very long account ID
2. Special characters in account ID
3. Multiple rapid submissions
4. Browser back button
5. Session timeout

## Integration Points

### Backend API
- Base URL: Configured in environment variables
- Authentication: Bearer token from localStorage
- Content-Type: application/json

### Authentication
- Uses AuthContext for user session
- Token forwarding via auth-middleware
- Automatic token refresh handling

## Future Enhancements

1. **Payment History**
   - View past transactions
   - Transaction search and filtering
   - Export transaction history

2. **Partial Payments**
   - Allow partial balance payments
   - Minimum payment calculation
   - Payment scheduling

3. **Multiple Payment Methods**
   - Credit card payment
   - Bank account payment
   - Digital wallet integration

4. **Payment Notifications**
   - Email confirmation
   - SMS notifications
   - Push notifications

5. **Recurring Payments**
   - Auto-pay setup
   - Payment reminders
   - Payment scheduling

## Maintenance Notes

### Code Organization
- Follow archetype patterns
- Use existing UI components
- Maintain type safety
- Document complex logic

### Performance Considerations
- Optimize API calls
- Implement caching where appropriate
- Minimize re-renders
- Lazy load components

### Security Considerations
- Validate all inputs
- Sanitize user data
- Use HTTPS only
- Implement rate limiting
- Log security events

## Support and Troubleshooting

### Common Issues

**Issue:** Account balance not loading
**Solution:** Check network connection, verify account ID format, check API endpoint availability

**Issue:** Payment not processing
**Solution:** Verify confirmation checkbox is checked, ensure balance is positive, check server logs

**Issue:** Transaction ID not displayed
**Solution:** Check API response format, verify transaction was created, check console for errors

### Debug Mode
Enable debug logging by setting `DEBUG=true` in environment variables.

### Logging
- Client-side errors logged to console
- Server-side errors logged to application logs
- Transaction events logged for audit trail

## Contact

For questions or issues related to the Bill Payment feature, contact the development team or refer to the main application documentation.

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**Maintained By:** Wynxx System Modernization Team
