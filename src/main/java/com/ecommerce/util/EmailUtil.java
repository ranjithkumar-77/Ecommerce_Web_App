package com.ecommerce.util;

public class EmailUtil {

    public static void sendPasswordResetEmail(String toEmail, String resetToken) {
        String baseUrl = System.getProperty("app.baseUrl", "http://localhost:8080/Ecommerce_Web_App");
        String resetLink = baseUrl + "/reset-password?token=" + resetToken;
        System.out.println("=== DEVELOPMENT PASSWORD RESET EMAIL (configure SMTP before production) ===");
        System.out.println("To: " + toEmail);
        System.out.println("Subject: Reset Your Password");
        System.out.println("Body: Click the link below to reset your password:");
        System.out.println(resetLink);
        System.out.println("=========================================================================");
    }
}
