package com.travel_agent.services.vnpay;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.travel_agent.configs.VNPayConfig;
import com.travel_agent.models.entity.TransactionInfo;
import com.travel_agent.models.entity.UserEntity;
import com.travel_agent.repositories.TransactionInfoRepository;
import com.travel_agent.repositories.UserRepository;
import com.travel_agent.repositories.booking.BookingHotelRepository;
import com.travel_agent.repositories.booking.BookingShipRepository;
import com.travel_agent.services.email.EmailService;

@Service
public class VNPayService implements IPaymentService {
    @Autowired
    private EmailService emailService;

    @Autowired
    private TransactionInfoRepository transactionInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingHotelRepository bookingHotelRepository;

    @Autowired
    private BookingShipRepository bookingShipRepository;

    @Override
    public void saveTransactionInfo(String paymentMethod, String content, String date, String userEmail) {
        // Logic to save transaction information
        // This could involve saving to a database or logging the transaction
        Optional<UserEntity> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isPresent()) {
            TransactionInfo transactionInfo = new TransactionInfo();
            transactionInfo.setUser(userOpt.get());
            transactionInfo.setPaymentMethod(paymentMethod);
            transactionInfo.setContent(content);

            try {
                // VNPay returns date in format "yyyyMMddHHmmss", we need to parse it and convert to LocalDate
                DateTimeFormatter vnpayFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(date, vnpayFormatter);
                LocalDate localDate = dateTime.toLocalDate();
                transactionInfo.setDates(localDate);
            } catch (Exception e) {
                // Fallback: if date parsing fails, use current date
                System.err.println("Error parsing VNPay date '" + date + "', using current date: " + e.getMessage());
                transactionInfo.setDates(LocalDate.now());
            }

            transactionInfoRepository.save(transactionInfo);
        } else {
            System.err.println("User not found: " + userEmail);
        }
    }

    @Override
    public void handlePaymentSuccess(String date, String content, String customerEmail, String orderId) {
        saveTransactionInfo("VNPay", content, date, customerEmail);

        String bookingType = "unknown";
        if (content != null && content.contains("|")) {
            String[] parts = content.split("\\|order");
            if (parts.length > 0) {
                String[] infoParts = parts[0].split("\\|");
                if (infoParts.length > 1) {
                    bookingType = infoParts[1];
                }
            }
        }

        updateBookingStatus(orderId, "PAID", bookingType);
        sendSuccessEmailWithDetails(customerEmail, orderId, bookingType);
        System.out.println("✅ Payment processed successfully for order #" + orderId);
    }

    private void sendSuccessEmailWithDetails(String customerEmail, String orderId, String bookingType) {
        if (emailService == null) {
            System.err.println("❌ EmailService is NULL!");
            return;
        }

        try {
            Integer bookingId = Integer.parseInt(orderId);

            if ("hotel".equalsIgnoreCase(bookingType)) {
                var hotelBookingOpt = bookingHotelRepository.findById(bookingId);
                if (hotelBookingOpt.isEmpty()) {
                    System.err.println("❌ Booking not found: " + bookingId);
                    return;
                }
                hotelBookingOpt.ifPresent(booking -> {
                    String itemName = booking.getHotel() != null ? booking.getHotel().getName() : "N/A";
                    emailService.sendSuccessOrderConfirmationEmail(
                            customerEmail,
                            orderId,
                            booking.getCustomerName(),
                            booking.getPhone(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getAdults(),
                            booking.getChildren(),
                            booking.getTotalAmount(),
                            bookingType,
                            itemName
                    );
                });
            } else if ("ship".equalsIgnoreCase(bookingType)) {
                var shipBookingOpt = bookingShipRepository.findById(bookingId);
                if (shipBookingOpt.isEmpty()) {
                    System.err.println("❌ Booking not found: " + bookingId);
                    return;
                }
                shipBookingOpt.ifPresent(booking -> {
                    String itemName = booking.getShip() != null ? booking.getShip().getName() : "N/A";
                    emailService.sendSuccessOrderConfirmationEmail(
                            customerEmail,
                            orderId,
                            booking.getCustomerName(),
                            booking.getPhone(),
                            booking.getStartDate(),
                            booking.getEndDate(),
                            booking.getAdults(),
                            booking.getChildren(),
                            booking.getTotalAmount(),
                            bookingType,
                            itemName
                    );
                });
            }
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid booking ID: " + orderId);
        } catch (Exception e) {
            System.err.println("❌ Error sending email for order #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateBookingStatus(String orderId, String status, String bookingType) {
        try {
            Integer bookingId = Integer.parseInt(orderId);

            if ("hotel".equalsIgnoreCase(bookingType)) {
                bookingHotelRepository.findById(bookingId).ifPresent(booking -> {
                    booking.setState(status);
                    bookingHotelRepository.save(booking);
                });
            } else if ("ship".equalsIgnoreCase(bookingType)) {
                bookingShipRepository.findById(bookingId).ifPresent(booking -> {
                    booking.setState(status);
                    bookingShipRepository.save(booking);
                });
            }
        } catch (NumberFormatException e) {
            System.err.println("Invalid booking ID format: " + orderId);
        }
    }

    @Override
    public void handlePaymentFail(String customerEmail, String orderId) {
        // Logic to handle failed payment
        sendFailureEmailWithDetails(customerEmail, orderId);
    }

    private void sendFailureEmailWithDetails(String customerEmail, String orderId) {
        try {
            Integer bookingId = Integer.parseInt(orderId);

            // Try hotel first
            var hotelBooking = bookingHotelRepository.findById(bookingId);
            if (hotelBooking.isPresent()) {
                emailService.sendFailedOrderConfirmationEmail(
                        customerEmail,
                        orderId,
                        hotelBooking.get().getCustomerName(),
                        "hotel"
                );
                return;
            }

            // Try ship
            var shipBooking = bookingShipRepository.findById(bookingId);
            if (shipBooking.isPresent()) {
                emailService.sendFailedOrderConfirmationEmail(
                        customerEmail,
                        orderId,
                        shipBooking.get().getCustomerName(),
                        "ship"
                );
                return;
            }

            // Fallback if booking not found
            emailService.sendFailedOrderConfirmationEmail(customerEmail, orderId, "Quý khách", "unknown");
        } catch (NumberFormatException e) {
            System.err.println("Invalid booking ID format: " + orderId);
            emailService.sendFailedOrderConfirmationEmail(customerEmail, orderId, "Quý khách", "unknown");
        }
    }

    @Override
    public String createPayment(long money, String orderID, String customerEmail, String bookingType) throws UnsupportedEncodingException {
        // Logic to create payment using VNPayConfig
        // This is a placeholder for the actual implementation
        String orderType = "other";
        long amount = money * 100; // Convert to VND (VNPay uses cents)
        String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
        String vnp_IpAddr = "127.0.0.1:50387";
        String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_BankCode", "NCB");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", customerEmail + "|" + bookingType + "|order" + orderID);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

        return paymentUrl;
    }


}

