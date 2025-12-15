package com.travel_agent.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import com.travel_agent.services.email.EmailService;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Email Service Unit Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    @DisplayName("Should send success order confirmation email successfully")
    void testSendSuccessOrderConfirmationEmail_Success() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String phone = "0123456789";
        LocalDate startDate = LocalDate.of(2024, 12, 20);
        LocalDate endDate = LocalDate.of(2024, 12, 25);
        Integer adults = 2;
        Integer children = 1;
        Integer totalAmount = 5000000;
        String bookingType = "hotel";
        String itemName = "Grand Hotel";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(
                toEmail, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        );

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    @DisplayName("Should handle exception when sending success email fails")
    void testSendSuccessOrderConfirmationEmail_MailException() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String phone = "0123456789";
        LocalDate startDate = LocalDate.of(2024, 12, 20);
        LocalDate endDate = LocalDate.of(2024, 12, 25);
        Integer adults = 2;
        Integer children = 1;
        Integer totalAmount = 5000000;
        String bookingType = "hotel";
        String itemName = "Grand Hotel";

        doThrow(new MailSendException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> emailService.sendSuccessOrderConfirmationEmail(
                toEmail, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        ));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should send failed order confirmation email successfully")
    void testSendFailedOrderConfirmationEmail_Success() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String bookingType = "hotel";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendFailedOrderConfirmationEmail(toEmail, orderId, customerName, bookingType);

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
        verify(mailSender, times(1)).createMimeMessage();
    }

    @Test
    @DisplayName("Should handle exception when sending failed email fails")
    void testSendFailedOrderConfirmationEmail_MailException() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String bookingType = "hotel";

        doThrow(new MailSendException("Failed to send email"))
                .when(mailSender).send(any(MimeMessage.class));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> emailService.sendFailedOrderConfirmationEmail(
                toEmail, orderId, customerName, bookingType
        ));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle null values gracefully in success email")
    void testSendSuccessOrderConfirmationEmail_WithNullValues() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String phone = "0123456789";
        LocalDate startDate = LocalDate.of(2024, 12, 20);
        LocalDate endDate = null; // null end date
        Integer adults = null; // null adults
        Integer children = null; // null children
        Integer totalAmount = 5000000;
        String bookingType = "ship";
        String itemName = "Luxury Cruise";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When & Then - should not throw exception with null values
        assertDoesNotThrow(() -> emailService.sendSuccessOrderConfirmationEmail(
                toEmail, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        ));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle multiple different recipients")
    void testSendEmail_MultipleRecipients() {
        // Given
        String toEmail1 = "customer1@example.com";
        String toEmail2 = "customer2@example.com";
        String orderId = "123";
        String customerName = "John Doe";
        String phone = "0123456789";
        LocalDate startDate = LocalDate.of(2024, 12, 20);
        LocalDate endDate = LocalDate.of(2024, 12, 25);
        Integer adults = 2;
        Integer children = 1;
        Integer totalAmount = 5000000;
        String bookingType = "hotel";
        String itemName = "Grand Hotel";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(
                toEmail1, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        );
        emailService.sendFailedOrderConfirmationEmail(toEmail2, orderId, customerName, bookingType);

        // Then
        verify(mailSender, times(2)).send(any(MimeMessage.class));
        verify(mailSender, times(2)).createMimeMessage();
    }

    @Test
    @DisplayName("Should handle hotel booking type correctly")
    void testSendEmail_HotelBookingType() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "123";
        String customerName = "Jane Smith";
        String phone = "0987654321";
        LocalDate startDate = LocalDate.of(2024, 12, 15);
        LocalDate endDate = LocalDate.of(2024, 12, 18);
        Integer adults = 2;
        Integer children = 0;
        Integer totalAmount = 3000000;
        String bookingType = "hotel";
        String itemName = "Beach Resort";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(
                toEmail, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        );

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle ship booking type correctly")
    void testSendEmail_ShipBookingType() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "456";
        String customerName = "Bob Johnson";
        String phone = "0112233445";
        LocalDate startDate = LocalDate.of(2024, 12, 25);
        LocalDate endDate = LocalDate.of(2024, 12, 30);
        Integer adults = 4;
        Integer children = 2;
        Integer totalAmount = 10000000;
        String bookingType = "ship";
        String itemName = "Ocean Liner";

        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(
                toEmail, orderId, customerName, phone, startDate, endDate,
                adults, children, totalAmount, bookingType, itemName
        );

        // Then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
