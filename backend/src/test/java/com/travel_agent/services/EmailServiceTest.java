package com.travel_agent.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.travel_agent.services.email.EmailService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Email Service Unit Tests")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should send success order confirmation email successfully")
    void testSendSuccessOrderConfirmationEmail_Success() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(toEmail, orderId);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle exception when sending success email fails")
    void testSendSuccessOrderConfirmationEmail_MailException() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doThrow(new MailSendException("Failed to send email"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> emailService.sendSuccessOrderConfirmationEmail(toEmail, orderId));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send failed order confirmation email successfully")
    void testSendFailedOrderConfirmationEmail_Success() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendFailedOrderConfirmationEmail(toEmail, orderId);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle exception when sending failed email fails")
    void testSendFailedOrderConfirmationEmail_MailException() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doThrow(new MailSendException("Failed to send email"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        // When & Then - should not throw exception
        assertDoesNotThrow(() -> emailService.sendFailedOrderConfirmationEmail(toEmail, orderId));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send email with correct content for success notification")
    void testSendSuccessOrderConfirmationEmail_CorrectContent() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);

            // Verify recipient
            assertNotNull(message.getTo());
            assertEquals(toEmail, message.getTo()[0]);

            // Verify sender
            assertEquals("projectmosd20251@gmail.com", message.getFrom());

            // Verify subject contains order ID and Vietnamese text
            assertNotNull(message.getSubject());
            assertTrue(message.getSubject().contains(orderId));
            assertTrue(message.getSubject().contains("Xác nhận đơn đặt phòng/du thuyền mã"));

            // Verify body contains Vietnamese success message and order ID
            assertNotNull(message.getText());
            assertTrue(message.getText().contains(orderId));
            assertTrue(message.getText().contains("đã được đặt thành công"));
            assertTrue(message.getText().contains("MoSD Team"));

            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(toEmail, orderId);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should send email with correct content for failure notification")
    void testSendFailedOrderConfirmationEmail_CorrectContent() {
        // Given
        String toEmail = "customer@example.com";
        String orderId = "ORDER123";

        doAnswer(invocation -> {
            SimpleMailMessage message = invocation.getArgument(0);

            // Verify recipient
            assertNotNull(message.getTo());
            assertEquals(toEmail, message.getTo()[0]);

            // Verify sender
            assertEquals("projectmosd20251@gmail.com", message.getFrom());

            // Verify subject contains order ID
            assertNotNull(message.getSubject());
            assertTrue(message.getSubject().contains(orderId));

            // Verify body contains Vietnamese failure message and order ID
            assertNotNull(message.getText());
            assertTrue(message.getText().contains(orderId));
            assertTrue(message.getText().contains("không thể được đặt"));
            assertTrue(message.getText().contains("lỗi trong quá trình thanh toán"));
            assertTrue(message.getText().contains("MoSD Team"));

            return null;
        }).when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendFailedOrderConfirmationEmail(toEmail, orderId);

        // Then
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("Should handle multiple different recipients")
    void testSendEmail_MultipleRecipients() {
        // Given
        String toEmail1 = "customer1@example.com";
        String toEmail2 = "customer2@example.com";
        String orderId = "ORDER123";

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // When
        emailService.sendSuccessOrderConfirmationEmail(toEmail1, orderId);
        emailService.sendFailedOrderConfirmationEmail(toEmail2, orderId);

        // Then
        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
    }
}
