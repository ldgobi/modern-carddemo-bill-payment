# Bill Payment Feature - Quick Start Guide

## 🚀 Getting Started

### Prerequisites
- Node.js 18+ installed
- npm or yarn package manager
- Backend API running (default: http://localhost:8080)
- Valid authentication token

### Installation

1. **Clone the repository** (if not already done)
```bash
git clone <repository-url>
cd archetype-nextjs
```

2. **Install dependencies**
```bash
npm install
```

3. **Configure environment variables**
```bash
cp .example.env .env
```

Edit `.env` file:
```env
API_BASE_URL=http://localhost:8080
```

4. **Run the development server**
```bash
npm run dev
```

5. **Access the application**
```
http://localhost:3000
```

6. **Navigate to Bill Payment**
```
http://localhost:3000/bill-payment
```

## 📁 File Structure

```
src/
├── types/
│   └── billPayment.ts                 # Type definitions
├── services/
│   └── billPaymentService.ts          # API client
├── app/
│   ├── api/
│   │   └── bill-payment/
│   │       ├── account/[accountId]/balance/route.ts
│   │       └── process/route.ts
│   └── bill-payment/
│       └── page.tsx                   # Main page
```

## 🔧 Quick Configuration

### Backend API Endpoint
Update in `.env`:
```env
API_BASE_URL=https://your-backend-api.com
```

### Authentication
The app uses localStorage for token storage:
```javascript
localStorage.setItem('access_token', 'your-token-here');
```

## 📝 Usage Examples

### 1. Test with Valid Account

```javascript
// Account ID: 12345678901
// Expected Balance: $1,500.50
```

**Steps:**
1. Navigate to `/bill-payment`
2. Enter account ID: `12345678901`
3. Click "Retrieve Balance"
4. Review balance information
5. Check confirmation checkbox
6. Click "Process Payment"
7. View success message with transaction ID

### 2. Test with Zero Balance

```javascript
// Account ID: 11111111111
// Expected Balance: $0.00
```

**Expected Result:**
- Message: "You have nothing to pay..."
- Payment button not available

### 3. Test with Invalid Account

```javascript
// Account ID: 99999999999
// Expected: Not Found
```

**Expected Result:**
- Error: "Account ID NOT found..."
- Balance not displayed

## 🎨 UI Components Used

### From Archetype
- `Button` - Primary, secondary, danger variants
- `Input` - Text input with label and validation

### Custom Styling
- TailwindCSS utility classes
- Responsive grid layout
- Color-coded status indicators

## 🔌 API Integration

### Get Account Balance
```typescript
import { billPaymentService } from '@/services/billPaymentService';

const balance = await billPaymentService.getAccountBalance('12345678901');
// Returns: { accountId: string, currentBalance: number }
```

### Process Payment
```typescript
import { billPaymentService } from '@/services/billPaymentService';

const result = await billPaymentService.processBillPayment({
  accountId: '12345678901',
  confirmPayment: true
});
// Returns: { transactionId, message, accountId, paymentAmount, newBalance }
```

## 🐛 Common Issues & Solutions

### Issue 1: "Failed to fetch account balance"
**Cause:** Backend API not running or incorrect URL  
**Solution:** 
- Check if backend is running
- Verify `API_BASE_URL` in `.env`
- Check network tab in browser DevTools

### Issue 2: "Unauthorized access"
**Cause:** Missing or invalid authentication token  
**Solution:**
- Ensure token is stored in localStorage
- Check token expiration
- Re-authenticate if needed

### Issue 3: "Account ID NOT found"
**Cause:** Account doesn't exist in database  
**Solution:**
- Verify account ID is correct
- Check if account exists in backend
- Use test account IDs

### Issue 4: Page not loading
**Cause:** Build or runtime error  
**Solution:**
```bash
# Clear cache and rebuild
rm -rf .next
npm run build
npm run dev
```

## 🧪 Quick Testing

### Manual Test Checklist
- [ ] Page loads without errors
- [ ] Can enter account ID
- [ ] Can retrieve balance
- [ ] Balance displays correctly
- [ ] Can confirm payment
- [ ] Payment processes successfully
- [ ] Success message displays
- [ ] Can make another payment
- [ ] Error messages display correctly
- [ ] Form validation works

### Test Accounts
```
Valid Accounts:
- 12345678901 (Balance: $1,500.50)
- 98765432109 (Balance: $2,750.00)

Zero Balance:
- 11111111111 (Balance: $0.00)

Invalid:
- 99999999999 (Not Found)
```

## 📊 Monitoring

### Browser Console
Check for errors:
```javascript
// Open DevTools (F12)
// Check Console tab for errors
// Check Network tab for API calls
```

### API Calls
Monitor in Network tab:
```
GET /api/bill-payment/account/12345678901/balance
POST /api/bill-payment/process
```

## 🔐 Security Notes

### Important
- Never commit `.env` file
- Keep authentication tokens secure
- Use HTTPS in production
- Validate all user inputs
- Sanitize error messages

### Best Practices
- Store tokens in httpOnly cookies (production)
- Implement rate limiting
- Log security events
- Monitor for suspicious activity

## 🚢 Deployment

### Build for Production
```bash
npm run build
```

### Start Production Server
```bash
npm run start
```

### Environment Variables (Production)
```env
API_BASE_URL=https://api.production.com
NODE_ENV=production
```

### Deployment Checklist
- [ ] Environment variables configured
- [ ] Backend API accessible
- [ ] SSL certificate installed
- [ ] Authentication working
- [ ] All tests passing
- [ ] Performance optimized
- [ ] Error tracking enabled
- [ ] Monitoring configured

## 📚 Additional Resources

### Documentation
- [Full Feature Documentation](./BILL_PAYMENT_README.md)
- [Testing Guide](./BILL_PAYMENT_TESTING.md)
- [Implementation Summary](./IMPLEMENTATION_SUMMARY.md)
- [Archetype Guide](./archetype.md)

### API Documentation
- Backend API: `/api-docs` (if available)
- OpenAPI Spec: Check with backend team

### Support
- Technical Issues: Contact development team
- Business Questions: Contact product owner
- Security Concerns: Contact security team

## 💡 Tips & Tricks

### Development
```bash
# Watch mode with auto-reload
npm run dev

# Type checking
npm run type-check

# Linting
npm run lint

# Format code
npm run format
```

### Debugging
```javascript
// Enable debug mode
localStorage.setItem('debug', 'true');

// View component state
console.log('Account Balance:', accountBalance);
console.log('Form State:', { accountId, confirmPayment });
```

### Performance
```javascript
// Measure component render time
console.time('BillPaymentPage');
// ... component code ...
console.timeEnd('BillPaymentPage');
```

## 🎯 Quick Commands

```bash
# Start development
npm run dev

# Build for production
npm run build

# Start production server
npm run start

# Run tests
npm run test

# Check types
npm run type-check

# Lint code
npm run lint

# Format code
npm run format

# Clean build
rm -rf .next && npm run build
```

## 📞 Getting Help

### Resources
1. Check documentation files
2. Review code comments
3. Check browser console
4. Review network requests
5. Check backend logs

### Contact
- Development Team: dev-team@company.com
- Technical Lead: tech-lead@company.com
- Support: support@company.com

## ✅ Success Criteria

Your implementation is working correctly if:
- ✅ Page loads without errors
- ✅ Can retrieve account balance
- ✅ Can process payment successfully
- ✅ Error messages display correctly
- ✅ Success message shows transaction ID
- ✅ Form resets after payment
- ✅ All validation works
- ✅ Loading states display
- ✅ Responsive on all devices
- ✅ Accessible via keyboard

## 🎉 You're Ready!

The Bill Payment feature is now set up and ready to use. Follow the usage examples above to test the functionality.

For detailed information, refer to the comprehensive documentation files:
- `BILL_PAYMENT_README.md` - Complete feature documentation
- `BILL_PAYMENT_TESTING.md` - Testing guide
- `IMPLEMENTATION_SUMMARY.md` - Implementation details

---

**Version:** 1.0.0  
**Last Updated:** 2024  
**Quick Start Guide By:** Wynxx System Modernization Team
