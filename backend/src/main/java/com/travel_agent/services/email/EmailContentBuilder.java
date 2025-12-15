package com.travel_agent.services.email;

import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EmailContentBuilder {

    private static final String FROM_EMAIL = "projectmosd20251@gmail.com";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public void buildSuccessEmailContent(MimeMessage mimeMessage, String toEmail, String orderId,
                                         String customerName, String phone, LocalDate startDate,
                                         LocalDate endDate, Integer adults, Integer children,
                                         Integer totalAmount, String bookingType, String itemName) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(FROM_EMAIL);
        helper.setTo(toEmail);
        helper.setSubject("✅ Xác nhận đơn đặt " + ("hotel".equals(bookingType) ? "phòng khách sạn" : "du thuyền") + " #" + orderId);

        String htmlContent = buildSuccessHtmlContent(orderId, customerName, phone, toEmail,
                startDate, endDate, adults, children,
                totalAmount, bookingType, itemName);
        helper.setText(htmlContent, true);
    }

    public void buildFailedEmailContent(MimeMessage mimeMessage, String toEmail, String orderId,
                                        String customerName, String bookingType) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setFrom(FROM_EMAIL);
        helper.setTo(toEmail);
        helper.setSubject("❌ Thông báo thanh toán thất bại - Đơn hàng #" + orderId);

        String htmlContent = buildFailedHtmlContent(orderId, customerName, bookingType);
        helper.setText(htmlContent, true);
    }

    private String buildSuccessHtmlContent(String orderId, String customerName, String phone,
                                           String email, LocalDate startDate, LocalDate endDate,
                                           Integer adults, Integer children, Integer totalAmount,
                                           String bookingType, String itemName) {
        String bookingTypeName = "hotel".equals(bookingType) ? "Khách sạn" : "Du thuyền";
        String formattedAmount = CURRENCY_FORMAT.format(totalAmount != null ? totalAmount : 0);
        String formattedStartDate = startDate != null ? startDate.format(DATE_FORMATTER) : "N/A";
        String formattedEndDate = endDate != null ? endDate.format(DATE_FORMATTER) : "N/A";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"vi\">");
        html.append("<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f4f4f4;\">");
        html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f4f4;padding:20px;\">");
        html.append("<tr><td align=\"center\">");
        html.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:10px;overflow:hidden;box-shadow:0 4px 6px rgba(0,0,0,0.1);\">");

        // Header
        html.append("<tr><td style=\"background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);padding:40px 30px;text-align:center;\">");
        html.append("<h1 style=\"color:#ffffff;margin:0;font-size:28px;\">✅ Đặt chỗ thành công!</h1>");
        html.append("<p style=\"color:#ffffff;margin:10px 0 0 0;font-size:16px;opacity:0.9;\">Cảm ơn bạn đã tin tưởng MoSD Travel</p>");
        html.append("</td></tr>");

        // Order Info
        html.append("<tr><td style=\"padding:30px;\">");
        html.append("<div style=\"background-color:#f8f9fa;border-left:4px solid #28a745;padding:15px;margin-bottom:25px;border-radius:5px;\">");
        html.append("<p style=\"margin:0;color:#666;font-size:14px;\">Mã đơn hàng</p>");
        html.append("<p style=\"margin:5px 0 0 0;color:#333;font-size:24px;font-weight:bold;\">#").append(orderId).append("</p>");
        html.append("</div>");

        html.append("<h2 style=\"color:#333;font-size:20px;margin:0 0 20px 0;border-bottom:2px solid #667eea;padding-bottom:10px;\">");
        html.append("📋 Thông tin khách hàng</h2>");
        html.append("<table width=\"100%\" style=\"margin-bottom:25px;\">");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;width:40%;\"><strong>Họ và tên:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(customerName).append("</td></tr>");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Email:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(email).append("</td></tr>");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Số điện thoại:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(phone).append("</td></tr>");
        html.append("</table>");

        html.append("<h2 style=\"color:#333;font-size:20px;margin:0 0 20px 0;border-bottom:2px solid #667eea;padding-bottom:10px;\">");
        html.append("🏨 Chi tiết đặt chỗ</h2>");
        html.append("<table width=\"100%\" style=\"margin-bottom:25px;\">");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;width:40%;\"><strong>Loại:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(bookingTypeName).append("</td></tr>");

        if (itemName != null) {
            html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Tên:</strong></td>");
            html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(itemName).append("</td></tr>");
        }

        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Ngày nhận:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(formattedStartDate).append("</td></tr>");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Số người lớn:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(adults != null ? adults : 0).append("</td></tr>");
        html.append("<tr><td style=\"padding:8px 0;color:#666;font-size:14px;\"><strong>Số trẻ em:</strong></td>");
        html.append("<td style=\"padding:8px 0;color:#333;font-size:14px;\">").append(children != null ? children : 0).append("</td></tr>");
        html.append("</table>");

        html.append("<div style=\"background-color:#f8f9fa;padding:20px;border-radius:5px;text-align:center;margin:25px 0;\">");
        html.append("<p style=\"margin:0;color:#666;font-size:14px;\">Tổng thanh toán</p>");
        html.append("<p style=\"margin:10px 0 0 0;color:#28a745;font-size:32px;font-weight:bold;\">").append(formattedAmount).append("</p>");
        html.append("<p style=\"margin:10px 0 0 0;color:#28a745;font-size:14px;font-weight:bold;\">✓ Đã thanh toán</p>");
        html.append("</div>");

        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #ffc107;padding:15px;margin:20px 0;border-radius:5px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-size:14px;line-height:1.6;\">");
        html.append("<strong>📌 Lưu ý quan trọng:</strong><br>");
        html.append("• Vui lòng mang theo CMND/CCCD khi nhận phòng/lên tàu<br>");
        html.append("• Thời gian nhận phòng: 14:00 | Trả phòng: 12:00<br>");
        html.append("• Vui lòng đến đúng giờ để tránh ảnh hưởng đến lịch trình");
        html.append("</p></div>");

        html.append("<p style=\"color:#666;font-size:14px;line-height:1.8;margin:25px 0;\">");
        html.append("Chúng tôi rất vui được phục vụ bạn! Nếu có bất kỳ thắc mắc nào, ");
        html.append("vui lòng liên hệ email <strong style=\"color:#667eea;\">mosd00424@gmail.com</strong> ");
        html.append("hoặc trả lời email này.");
        html.append("</p>");

        html.append("<p style=\"color:#333;font-size:14px;margin:0;\">Chúc bạn có một chuyến đi tuyệt vời! 🎉</p>");
        html.append("<p style=\"color:#667eea;font-weight:bold;margin:10px 0 0 0;font-size:14px;\">");
        html.append("Trân trọng,<br>MoSD Travel Team</p>");
        html.append("</td></tr>");

        // Footer
        html.append("<tr><td style=\"background-color:#f8f9fa;padding:20px 30px;text-align:center;border-top:1px solid #e0e0e0;\">");
        html.append("<p style=\"margin:0;color:#999;font-size:12px;line-height:1.6;\">");
        html.append("© 2025 MoSD Travel. All rights reserved.<br>");
        html.append("Email này được gửi tự động, vui lòng không trả lời trực tiếp.");
        html.append("</p></td></tr>");

        html.append("</table></td></tr></table></body></html>");

        return html.toString();
    }

    private String buildFailedHtmlContent(String orderId, String customerName, String bookingType) {
        String bookingTypeName = "hotel".equals(bookingType) ? "khách sạn" : "du thuyền";

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html lang=\"vi\">");
        html.append("<head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head>");
        html.append("<body style=\"margin:0;padding:0;font-family:Arial,sans-serif;background-color:#f4f4f4;\">");
        html.append("<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#f4f4f4;padding:20px;\">");
        html.append("<tr><td align=\"center\">");
        html.append("<table width=\"600\" cellpadding=\"0\" cellspacing=\"0\" style=\"background-color:#ffffff;border-radius:10px;overflow:hidden;box-shadow:0 4px 6px rgba(0,0,0,0.1);\">");

        // Header
        html.append("<tr><td style=\"background:linear-gradient(135deg,#f093fb 0%,#f5576c 100%);padding:40px 30px;text-align:center;\">");
        html.append("<h1 style=\"color:#ffffff;margin:0;font-size:28px;\">❌ Thanh toán thất bại</h1>");
        html.append("<p style=\"color:#ffffff;margin:10px 0 0 0;font-size:16px;opacity:0.9;\">");
        html.append("Đơn đặt ").append(bookingTypeName).append(" của bạn chưa hoàn tất</p>");
        html.append("</td></tr>");

        // Content
        html.append("<tr><td style=\"padding:40px 30px;\">");
        html.append("<p style=\"color:#333;font-size:16px;margin:0 0 20px 0;\">Xin chào <strong>").append(customerName).append("</strong>,</p>");

        html.append("<div style=\"background-color:#fff3cd;border-left:4px solid #ffc107;padding:20px;margin:20px 0;border-radius:5px;\">");
        html.append("<p style=\"margin:0;color:#856404;font-size:14px;line-height:1.6;\">");
        html.append("<strong>⚠️ Thông báo:</strong><br>");
        html.append("Đơn hàng <strong>#").append(orderId).append("</strong> của bạn không thể hoàn tất do ");
        html.append("<strong>lỗi trong quá trình thanh toán</strong>.");
        html.append("</p></div>");

        html.append("<h3 style=\"color:#333;font-size:18px;margin:25px 0 15px 0;\">🔄 Bạn có thể:</h3>");
        html.append("<ul style=\"color:#666;font-size:14px;line-height:1.8;margin:0;padding-left:20px;\">");
        html.append("<li>Thử lại thanh toán cho đơn hàng này</li>");
        html.append("<li>Kiểm tra số dư tài khoản ngân hàng</li>");
        html.append("<li>Sử dụng phương thức thanh toán khác</li>");
        html.append("<li>Liên hệ ngân hàng nếu tiền đã bị trừ</li>");
        html.append("</ul>");

        html.append("<div style=\"text-align:center;margin:30px 0;\">");
        html.append("<a href=\"http://localhost:5173\" style=\"display:inline-block;background:linear-gradient(135deg,#667eea 0%,#764ba2 100%);");
        html.append("color:#ffffff;padding:15px 40px;text-decoration:none;border-radius:5px;font-weight:bold;font-size:16px;\">");
        html.append("Thử lại thanh toán");
        html.append("</a></div>");

        html.append("<div style=\"background-color:#f8f9fa;padding:20px;border-radius:5px;margin:25px 0;\">");
        html.append("<p style=\"margin:0;color:#666;font-size:14px;line-height:1.6;\">");
        html.append("<strong>💬 Cần hỗ trợ?</strong><br>");
        html.append("Hotline: <strong style=\"color:#667eea;\">1900-xxxx</strong> (24/7)<br>");
        html.append("Email: <strong style=\"color:#667eea;\">support@mosd.com</strong>");
        html.append("</p></div>");

        html.append("<p style=\"color:#666;font-size:14px;margin:20px 0 0 0;line-height:1.6;\">");
        html.append("Chúng tôi luôn sẵn sàng hỗ trợ bạn!");
        html.append("</p>");

        html.append("<p style=\"color:#667eea;font-weight:bold;margin:15px 0 0 0;font-size:14px;\">");
        html.append("Trân trọng,<br>MoSD Travel Team</p>");
        html.append("</td></tr>");

        // Footer
        html.append("<tr><td style=\"background-color:#f8f9fa;padding:20px 30px;text-align:center;border-top:1px solid #e0e0e0;\">");
        html.append("<p style=\"margin:0;color:#999;font-size:12px;line-height:1.6;\">");
        html.append("© 2025 MoSD Travel. All rights reserved.<br>");
        html.append("Email này được gửi tự động, vui lòng không trả lời trực tiếp.");
        html.append("</p></td></tr>");

        html.append("</table></td></tr></table></body></html>");

        return html.toString();
    }
}

