package com.travel_agent.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    @Autowired
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSuccessOrderConfirmationEmail(String toEmail, String orderId) {
        try {
            EmailContentBuilder content = new EmailContentBuilder();
            mailSender.send(content.getSuccessEmailContent(toEmail, orderId));
        } catch (MailException e) {
            System.err.println("Error sending confirmation email #" + orderId + " to " + toEmail + ": " + e.getMessage());
        }
    }

    public void sendFailedOrderConfirmationEmail(String toEmail, String orderId) {
        try {
            EmailContentBuilder content = new EmailContentBuilder();
            mailSender.send(content.getFailedEmailContent(toEmail, orderId));
            System.out.println("Confirmation email for #" + orderId + " is sent " + toEmail);

        } catch (MailException e) {
            System.err.println("Error sending confirmation email #" + orderId + " to " + toEmail + ": " + e.getMessage());
            // throw new RuntimeException("Failed to send email", e); // Có thể ném ngoại lệ để xử lý ở tầng cao hơn
        }
    }
}