package com.travel_agent.services.email;

import org.springframework.mail.SimpleMailMessage;

public class EmailContentBuilder {

    public SimpleMailMessage getSuccessEmailContent(String toEmail, String orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("projectmosd20251@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Xác nhận đơn đặt phòng/du thuyền mã #" + orderId);
        message.setText("Xin chào quý khách,\n\n"
                + "Cảm ơn bạn đã đặt hàng trong hệ thống của chúng tôi! Đơn hàng #" + orderId + " của bạn đã được đặt thành công.\n\n"
                + "Chúc bạn sẽ có những trải nghiệm vui vẻ và đáng nhớ!.\n\n"
                + "Trân trọng,\n"
                + "MoSD Team");
        return message;
    }

    public SimpleMailMessage getFailedEmailContent(String toEmail, String orderId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("projectmosd20251@gmail.com");
        message.setTo(toEmail);
        message.setSubject(" #" + orderId);
        message.setText("Xin chào quý khách,\n\n"
                + "Đơn hàng #" + orderId + " của bạn không thể được đặt do lỗi trong quá trình thanh toán. Vui lòng thử lại để thanh toán đơn hàng của bạn.\n\n"
                + "Vui lòng liên hệ với chúng tôi qua hotline để biết thêm thông tin.\n\n"
                + "Trân trọng,\n"
                + "MoSD Team");
        return message;
    }

}

