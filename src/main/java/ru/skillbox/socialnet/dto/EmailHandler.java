package ru.skillbox.socialnet.dto;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.exception.EmailAddressException;
import ru.skillbox.socialnet.exception.EmailContentException;

import java.util.Properties;

@Component
public class EmailHandler {

    @Value("${email.project-email}")
    private String projectEmail;

    @Value("${email.project-email-password}")
    private String projectPassword;

    @Value("${email.smtp-auth}")
    private boolean smtpAuth;

    @Value("${email.smtp-starttls-enable}")
    private boolean smtpStartTlsEnable;

    @Value("${email.smtp-host}")
    private String smtpHost;

    @Value("${email.smtp-port}")
    private String smtpPort;

    @Value("${email.smtp-ssl-trust}")
    private boolean smtpSslTrust;

    public void sendEmail(String emailTo, String subject, String text) {

        Session session = createSession();

        Message message = new MimeMessage(session);
        InternetAddress from = null;
        try {
            from = new InternetAddress(projectEmail);
            message.setFrom(from);
        } catch (MessagingException e) {
            throw new EmailAddressException("Неверный адрес отправителя: " + from);
        }

        try {
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(emailTo));
            message.setSubject(subject);
        } catch (MessagingException e) {
            throw new EmailAddressException("Неверный адрес получателя: " + emailTo);
        }

        try {
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(text, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            message.setContent(multipart);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new EmailContentException("Ошибка при попытке отправить письмо: " + text);
        }
    }

    private Session createSession() {

        Properties properties = getInitializedProperties();
        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(projectEmail, projectPassword);
            }
        });
    }

    private Properties getInitializedProperties() {

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", smtpAuth);
        properties.put("mail.smtp.starttls.enable", smtpStartTlsEnable);
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        properties.put("mail.smtp.ssl.trust", smtpSslTrust);

        return properties;
    }
}
