package com.travel_agent.services.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSuccessOrderConfirmationEmail(String toEmail, String orderId,
                                                  String customerName, String phone,
                                                  LocalDate startDate, LocalDate endDate,
                                                  Integer adults, Integer children,
                                                  Integer totalAmount, String bookingType,
                                                  String itemName) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            EmailContentBuilder content = new EmailContentBuilder();
            content.buildSuccessEmailContent(mimeMessage, toEmail, orderId, customerName,
                    phone, startDate, endDate, adults, children,
                    totalAmount, bookingType, itemName);
            mailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully for order #" + orderId);
        } catch (Exception e) {
            System.err.println("❌ Error sending email for order #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendFailedOrderConfirmationEmail(String toEmail, String orderId,
                                                 String customerName, String bookingType) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            EmailContentBuilder content = new EmailContentBuilder();
            content.buildFailedEmailContent(mimeMessage, toEmail, orderId, customerName, bookingType);
            mailSender.send(mimeMessage);
        } catch (Exception e) {
            System.err.println("❌ Error sending failure email #" + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}