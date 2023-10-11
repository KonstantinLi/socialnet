package ru.skillbox.socialnet.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.skillbox.socialnet.exception.EmailContentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Component
public class EmailContentManager {

    @Value("${email.recovery-email-blueprint}")
    private String emailBluePrint;

    @Value("${email.recovery-base-url}")
    private String recoveryBaseUrl;

    public String getRecoveryEmailContent(EmailType type, String token) {

        if (type.equals(EmailType.EMAIL_RECOVERY)) {
            return getEmailRecoveryEmailContent(token);
        } else if (type.equals(EmailType.PASSWORD_RECOVERY)) {
            return getPasswordRecoveryEmailContent(token);
        } else {
            throw new IllegalArgumentException("Invalid email type");
        }
    }

    private String getBlueprintAsString() {

        InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(emailBluePrint);

        if (resourceAsStream == null) {
            throw new IllegalArgumentException(String.format(
                    "Email blueprint with address %s not found", emailBluePrint));
        }


        StringBuilder stringBuilder = new StringBuilder();
        try (InputStreamReader streamReader =
                     new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8);

             BufferedReader reader = new BufferedReader(streamReader)) {

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

        } catch (IOException e) {
            throw new EmailContentException("Error while reading email blueprint");
        }

        if (stringBuilder.isEmpty()) {
            throw new EmailContentException("Email blueprint is empty");
        }

        return stringBuilder.toString();
    }

    private String getEmailRecoveryEmailContent(String token) {
        String blueprint = getBlueprintAsString();
        String confirmationButtonCode = getEmailResetConfirmationButtonCode(token);

        blueprint = blueprint.replace("[TITLE]", "Email Recovery");
        blueprint = blueprint.replace("[START_TEXT]", "Click on the link below to change email: ");
        blueprint = blueprint.replace("[CONFIRMATION_BUTTON]", confirmationButtonCode);
        blueprint = blueprint.replace("[END_TEXT]",
                "If you did not request a email change, ignore this email.");

        return blueprint;
    }

    private String getPasswordRecoveryEmailContent(String token) {
        String blueprint = getBlueprintAsString();
        String confirmationButtonCode = getPasswordResetConfirmationButtonCode(token);

        blueprint = blueprint.replace("[TITLE]", "Password Recovery");
        blueprint = blueprint.replace("[START_TEXT]", "Click on the link below to change password: ");
        blueprint = blueprint.replace("[CONFIRMATION_BUTTON]", confirmationButtonCode);
        blueprint = blueprint.replace("[END_TEXT]",
                "If you did not request a password change, ignore this email.");

        return blueprint;
    }

    private String getEmailResetConfirmationButtonCode(String token) {
        return String.format("<a href=\"%sshift-email?token=%s\" class=\"button\">Reset email</a></p>",
                recoveryBaseUrl, token);
    }

    private String getPasswordResetConfirmationButtonCode(String token) {
        return String.format("<a href=\"%schange-password?token=%s\" class=\"button\">Change password!</a>",
                recoveryBaseUrl, token);
    }

    public enum EmailType {
        EMAIL_RECOVERY,
        PASSWORD_RECOVERY
    }
}
