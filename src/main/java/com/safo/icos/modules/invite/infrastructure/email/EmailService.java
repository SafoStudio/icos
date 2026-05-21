package com.safo.icos.modules.invite.infrastructure.email;

import com.safo.icos.modules.invite.domain.model.Invite;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${app.invitation.base-url}")
    private String baseUrl;

    @Value("${app.invitation.from-email}")
    private String fromEmail;

    @Async
    public void sendInvitationEmail(Invite invite, String companyName, String deptName, String managerName) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.error("Cannot send email: fromEmail is empty. Check your .env configuration.");
            return;
        }
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            String link = baseUrl + "?token=" + invite.getToken();

            String htmlContent = String.format(
                    "<html><body>" +
                    "<h1>Welcome to %s</h1>" +
                    "<p>You have been invited to join as a <strong>%s</strong>.</p>" +
                    "<ul>" +
                    "<li>Department: %s</li>" +
                    "<li>Manager: %s</li>" +
                    "</ul>" +
                    "<p>Please complete your registration here: <a href='%s'>Accept Invitation</a></p>" +
                    "<p>This link will expire on %s</p>" +
                    "</body></html>",
                    companyName, invite.getRole(), deptName, managerName, link, invite.getExpiresAt()
            );

            helper.setTo(invite.getEmail());
            helper.setSubject("Invitation to join " + companyName);
            helper.setText(htmlContent, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);
            log.info("Invitation email sent successfully to {}", invite.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send invitation email to {}", invite.getEmail(), e);
        }
    }
}
