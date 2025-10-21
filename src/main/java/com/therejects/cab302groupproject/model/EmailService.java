package com.therejects.cab302groupproject.model;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {
    private final String from = "mathbattlemonsters@gmail.com";
    private final String password = "dvzw fbxc ggtp izpa";

    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        if (password == null || password.isBlank()) {
            throw new IllegalStateException("Missing Gmail app password. Please set POKEMATH_APP_PASSWORD env variable.");
        }

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("PokéMath Password Reset");

        message.setText("""
            Hi there,

            Here is your PokéMath password reset token:
            %s

            To reset your password:
            1. Open PokéMath.
            2. Click 'Forgot password?'.
            3. Choose 'Yes' and paste this token.

            This token expires in 15 minutes.

            — PokéMath Support
        """.formatted(token));

        Transport.send(message);
        System.out.println("Password reset email has been successfully sent to " + to);
    }
}
