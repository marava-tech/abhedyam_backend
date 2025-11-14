package com.abhedyam.service;

import com.abhedyam.service.interfaces.IEmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService implements IEmailService {
    
    private final GmailSender gmailSender;
    
    @Value("${app.email.enabled:true}")
    private boolean emailEnabled;
    
    @Override
    public void sendEmail(String to, String subject, String body) {
        sendEmailWithRetry(to, subject, body, 1);
    }
    
    @Override
    public boolean sendEmailWithRetry(String to, String subject, String body, int maxRetries) {
        if (!emailEnabled) {
            log.warn("Email service is disabled. Skipping email send to: {}", to);
            return false;
        }
        
        if (to == null || to.isEmpty() || !isValidEmail(to)) {
            log.warn("Invalid email address: {}", to);
            return false;
        }
        
        int attempt = 0;
        Exception lastException = null;
        
        while (attempt < maxRetries) {
            attempt++;
            try {
                gmailSender.sendGmail(to, subject, body);
                log.info("Email sent successfully to {} (attempt {}/{})", to, attempt, maxRetries);
                return true;
            } catch (MessagingException e) {
                lastException = e;
                log.warn("Error sending email to {} (attempt {}/{}): {}", to, attempt, maxRetries, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted during retry delay", ie);
                        return false;
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.warn("Unexpected error sending email to {} (attempt {}/{}): {}", to, attempt, maxRetries, e.getMessage());
                
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(1000 * attempt);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Interrupted during retry delay", ie);
                        return false;
                    }
                }
            }
        }
        
        log.error("Failed to send email after {} attempts to: {}", maxRetries, to, lastException);
        return false;
    }
    
    @Override
    public void sendOtpEmail(String to, String otp) {
        String subject = "Your OTP for Abhedyam";
        String body = String.format(
            "<html><body>" +
            "<h2>Your OTP for Abhedyam</h2>" +
            "<p><strong>Your OTP is: %s</strong></p>" +
            "<p>This OTP is valid for 10 minutes.</p>" +
            "<p>If you didn't request this OTP, please ignore this email.</p>" +
            "<br>" +
            "<p>Best regards,<br>Abhedyam Team</p>" +
            "</body></html>",
            otp
        );
        sendEmail(to, subject, body);
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}

