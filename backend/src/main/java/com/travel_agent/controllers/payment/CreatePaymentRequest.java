package com.travel_agent.controllers.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private long amount;
    private String orderId;
    private String customerEmail;
    private String bookingType; // "hotel" or "ship"
}
