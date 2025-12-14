package com.travel_agent.controllers.payment;

import java.util.Map;
import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.travel_agent.dto.ResponseObject;
import com.travel_agent.services.vnpay.IPaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private IPaymentService paymentService;

    /**
     * POST /payment/create
     * Create VNPay payment URL for order
     */
    @PostMapping("/create")
    public ResponseEntity<?> createPayment(@RequestBody CreatePaymentRequest request) {
        try {
            String paymentUrl = paymentService.createPayment(
                    request.getAmount(),
                    request.getOrderId(),
                    request.getCustomerEmail(),
                    request.getBookingType());

            CreatePaymentResponse response = new CreatePaymentResponse(
                    paymentUrl,
                    request.getOrderId(),
                    "Payment URL created successfully");

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Payment URL encoding error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(
                    ResponseObject.builder()
                            .responseCode(500)
                            .message("Failed to create payment URL: " + e.getMessage())
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            System.err.println("Payment creation error: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(
                    ResponseObject.builder()
                            .responseCode(500)
                            .message("Failed to create payment: " + e.getMessage())
                            .data(null)
                            .build(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * POST /payment/process-vnpay-return
     * Process VNPay payment return parameters (called from frontend)
     */
    @PostMapping("/process-vnpay-return")
    public ResponseEntity<?> processVNPayReturn(@RequestBody Map<String, String> params) {
        try {
            String errorCode = params.get("vnp_TransactionStatus");
            String responseCode = params.get("vnp_ResponseCode");
            String transactionDate = params.get("vnp_PayDate");
            String transactionContent = params.get("vnp_OrderInfo");
            String amount = params.get("vnp_Amount");
            String transactionNo = params.get("vnp_TransactionNo");

            if (transactionContent == null || !transactionContent.contains("|order")) {
                throw new IllegalArgumentException("Invalid order info format");
            }

            String[] parts = transactionContent.split("\\|order");
            String[] infoParts = parts[0].split("\\|");
            String customerEmail = infoParts[0];
            String bookingType = infoParts.length > 1 ? infoParts[1] : "unknown";
            String orderId = parts[1];

            boolean isSuccess = "00".equals(responseCode);

            if (isSuccess) {
                paymentService.handlePaymentSuccess(transactionDate, transactionContent, customerEmail, orderId);
            } else {
                paymentService.handlePaymentFail(customerEmail, orderId);
            }

            // Return processed result to frontend
            Map<String, Object> result = new java.util.HashMap<>();
            result.put("success", isSuccess);
            result.put("transactionStatus", errorCode);
            result.put("responseCode", responseCode);
            result.put("orderId", orderId);
            result.put("customerEmail", customerEmail);
            result.put("amount", amount);
            result.put("transactionNo", transactionNo);
            result.put("transactionDate", transactionDate);
            result.put("message", isSuccess ? "Payment processed successfully" : "Payment failed");

            return new ResponseEntity<>(result, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error processing VNPay return: " + e.getMessage());
            e.printStackTrace();

            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("success", false);
            errorResult.put("message", "Failed to process payment return: " + e.getMessage());

            return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
