package de.fll.screen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String verificationToken) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Email Verification");
            
            String verificationLink = "http://localhost:8081/auth/verify-email?token=" + verificationToken;
            String emailContent = String.format("""
                <html>
                    <body>
                        <h2>Welcome to FLL Screen!</h2>
                        <p>Please click the link below to verify your email address:</p>
                        <a href="%s">Verify Email</a>
                        <p>If you did not create an account, please ignore this email.</p>
                    </body>
                </html>
                """, verificationLink);

            helper.setText(emailContent, true); // true indicates HTML content

            mailSender.send(message);
            logger.info("Verification email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send verification email to: {}", to, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
} 