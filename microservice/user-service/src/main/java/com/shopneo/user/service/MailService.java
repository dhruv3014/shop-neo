package com.shopneo.user.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendPasswordResetMailHtml(String to, String token) {

        String resetLink = "https://app.shopneo.com/reset-password?token=" + token;

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject("Reset your ShopNeo password");

            helper.setText("""
            <html>
              <body>
                <p>Hello,</p>
                <p>Click the link below to reset your password:</p>
                <p>
                  <a href="%s">Reset Password</a>
                </p>
                <p>This link expires in 30 minutes.</p>
              </body>
            </html>
        """.formatted(resetLink), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    public void sendPasswordResetMail(String to, String token) {

        String resetLink = "https://app.shopneo.com/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reset your ShopNeo password");
        message.setText("""
                Hello,

                We received a request to reset your password.

                Reset link (valid for 30 minutes):
                %s

                If you did not request this, ignore this email.

                – ShopNeo Team
                """.formatted(resetLink));

        mailSender.send(message);
    }

}

