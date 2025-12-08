# VNPay Payment Integration Setup

## Overview

This project integrates VNPay payment gateway for processing online payments.

## Prerequisites

### 1. Database Setup

Run the following SQL to create the transaction_info table:

```sql
CREATE TABLE transaction_info (
    transaction_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    payment_method VARCHAR(100) NOT NULL,
    content VARCHAR(500),
    transaction_date DATE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);
```

### 2. Email Configuration

Update `application.properties` with your Gmail app password:

```properties
spring.mail.password=your_gmail_app_password_here
```

**To get Gmail App Password:**

1. Go to Google Account → Security
2. Enable 2-Step Verification
3. Generate App Password for "Mail"
4. Copy the 16-character password

Or set environment variable:

```bash
export MAIL_PASSWORD=your_app_password
```

### 3. VNPay Configuration

The VNPay sandbox credentials are already configured in `VNPayConfig.java`:

- Terminal Code: `1TSJU6WP`
- Secret Key: `4WXQRIXMN0B69JM5QS7DTYS2IEFE1OZB`
- Payment URL: `https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`

## API Endpoints

### 1. Create Payment URL

**POST** `/api/payment/create`

Request Body:

```json
{
  "amount": 100000,
  "orderId": "ORDER123",
  "customerEmail": "customer@example.com"
}
```

Response:

```json
{
  "paymentUrl": "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?...",
  "orderId": "ORDER123",
  "message": "Payment URL created successfully"
}
```

### 2. Process VNPay Return

**POST** `/api/payment/process-vnpay-return`

Request Body (from VNPay callback):

```json
{
  "vnp_Amount": "10000000",
  "vnp_BankCode": "NCB",
  "vnp_CardType": "ATM",
  "vnp_OrderInfo": "customer@example.com pay for orderORDER123",
  "vnp_PayDate": "20231207162830",
  "vnp_ResponseCode": "00",
  "vnp_TransactionNo": "14123456",
  "vnp_TransactionStatus": "00",
  "vnp_TxnRef": "12345678",
  "vnp_SecureHash": "..."
}
```

Response:

```json
{
  "success": true,
  "transactionStatus": "00",
  "responseCode": "00",
  "orderId": "ORDER123",
  "customerEmail": "customer@example.com",
  "amount": "10000000",
  "transactionNo": "14123456",
  "transactionDate": "20231207162830",
  "message": "Payment processed successfully"
}
```

## Payment Flow

1. **Frontend** calls `/api/payment/create` to get payment URL
2. **Frontend** redirects user to VNPay payment URL
3. User completes payment on VNPay
4. **VNPay** redirects back to frontend with payment result parameters
5. **Frontend** calls `/api/payment/process-vnpay-return` with the parameters
6. **Backend** processes the payment:
    - Saves transaction info to database
    - Sends confirmation email to customer
7. **Frontend** displays payment result to user

## Email Templates

### Success Email

- Subject: "Confirming Order #{orderId}"
- Body: Confirmation of successful payment

### Failed Email

- Subject: "Confirming Order #{orderId}"
- Body: Notification of payment failure with instructions

## Testing with VNPay Sandbox

Use VNPay sandbox test cards:

- **Card Number**: 9704198526191432198
- **Card Holder**: NGUYEN VAN A
- **Expiry Date**: 07/15
- **OTP**: 123456

## Troubleshooting

### Email not sending

- Verify Gmail app password is correct
- Check `spring.mail.*` properties in application.properties
- Ensure 2-Step Verification is enabled in Google Account

### Transaction not saved

- Verify user exists with the provided email
- Check database connection
- Review console logs for errors

### Payment URL generation fails

- Verify VNPayConfig values are correct
- Check network connectivity
- Review VNPay documentation for any API changes

## Security Notes

1. **Production**: Replace sandbox credentials with production credentials from VNPay
2. **Email**: Store email password as environment variable, not in properties file
3. **HTTPS**: Always use HTTPS in production
4. **Validation**: Verify vnp_SecureHash to prevent tampering
