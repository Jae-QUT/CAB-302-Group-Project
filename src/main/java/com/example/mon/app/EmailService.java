package com.example.mon.app;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private final String from = "MathBattleMonsters@gmail.com";
    private final String password = "kyvd kjpn ltko awdf"; // App password

    public void sendPasswordReset(String to, String token) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        // Create mail session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        // --- Build the message ---
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("PokéMath Password Reset");

        // Desktop-friendly message content (no localhost link)
        String resetBody =
                "Hi there,\n\n" +
                        "Here is your PokéMath password reset token:\n\n" +
                        token + "\n\n" +
                        "To reset your password:\n" +
                        "1. Open PokéMath.\n" +
                        "2. Click 'Forgot password?'.\n" +
                        "3. Choose 'Yes' and paste this token.\n\n" +
                        "This token will expire in 15 minutes.\n\n" +
                        "— PokéMath Support";

        message.setText(resetBody);

        // Send the email
        Transport.send(message);
    }
}
