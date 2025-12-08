package com.travel_agent.services.vnpay;

import java.io.UnsupportedEncodingException;

public interface IPaymentService {
    void handlePaymentSuccess(String date, String content, String customerEmail, String orderId);
    void handlePaymentFail(String customerEmail, String orderId);
    String createPayment(long amount, String orderID, String customerEmail, String bookingType) throws UnsupportedEncodingException;
    void saveTransactionInfo(String paymentMethod, String content, String date, String userEmail);
}