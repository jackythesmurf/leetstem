package au.edu.sydney.elec5619.leetstem.service.email.impl;

import au.edu.sydney.elec5619.leetstem.service.email.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.net.URI;

@Service
public class AsyncJavaMailEmailService implements EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;
    private final URI signUpVerificationPageUri;
    private final URI passwordResetPageUri;
    private final URI emailUpdatePageUri;
    private final String signUpVerificationTemplate;
    private final String passwordResetTemplate;
    private final String emailUpdateTemplate;
    private final String signUpVerificationSubject;
    private final String passwordResetSubject;
    private final String emailUpdateSubject;
    private final String emailSenderAddress;
    private final String emailSenderName;
    private final Logger logger = LoggerFactory.getLogger(AsyncJavaMailEmailService.class);

    public AsyncJavaMailEmailService(JavaMailSender javaMailSender,
                                     SpringTemplateEngine templateEngine,
                                     @Value("${leetstem.frontend.uri.signUpVerify}") URI signUpVerifyUri,
                                     @Value("${leetstem.frontend.uri.passwordReset}") URI passwordResetPageUri,
                                     @Value ("${leetstem.frontend.uri.emailUpdate}")URI emailUpdatePageUri,
                                     @Value("${leetstem.mail.template.signUpVerify}") String signUpVerifyTemplate,
                                     @Value("${leetstem.mail.template.passwordReset}") String passwordResetTemplate,
                                     @Value("${leetstem.mail.template.emailUpdate}") String emailUpdateTemplate,
                                     @Value("${spring.mail.username}") String emailSenderAddress,
                                     @Value("${leetstem.mail.senderName}") String emailSenderName,
                                     @Value("${leetstem.mail.subject.signUpVerify}") String signUpVerifySubject,
                                     @Value("${leetstem.mail.subject.passwordReset}") String passwordResetSubject,
                                     @Value("${leetstem.mail.subject.emailUpdate}") String emailUpdateSubject) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
        this.signUpVerificationPageUri = signUpVerifyUri;
        this.passwordResetPageUri = passwordResetPageUri;
        this.emailUpdatePageUri = emailUpdatePageUri;
        this.signUpVerificationTemplate = signUpVerifyTemplate;
        this.passwordResetTemplate = passwordResetTemplate;
        this.emailUpdateTemplate = emailUpdateTemplate;
        this.emailUpdateSubject = emailUpdateSubject;
        this.emailSenderAddress = emailSenderAddress;
        this.emailSenderName = emailSenderName;
        this.signUpVerificationSubject = signUpVerifySubject;
        this.passwordResetSubject = passwordResetSubject;
    }

    @Override
    @Async
    public void sendSignUpVerificationEmail(String token, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Build content
        String verificationLink = UriComponentsBuilder
                .fromUri(signUpVerificationPageUri)
                .queryParam("token", token)
                .build()
                .toUriString();
        Context context = new Context();
        context.setVariable("verificationLink", verificationLink);
        String text = templateEngine.process(signUpVerificationTemplate, context);

        try {
            helper.setFrom(emailSenderAddress, emailSenderName);
            helper.setTo(email);
            helper.setSubject(signUpVerificationSubject);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Email wasn't sent: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String token, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Build content
        String passwordResetLink = UriComponentsBuilder
                .fromUri(passwordResetPageUri)
                .queryParam("token", token)
                .build()
                .toUriString();
        Context context = new Context();
        context.setVariable("passwordResetLink", passwordResetLink);
        String text = templateEngine.process(passwordResetTemplate, context);

        try {
            helper.setFrom(emailSenderAddress, emailSenderName);
            helper.setTo(email);
            helper.setSubject(passwordResetSubject);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Email wasn't sent: {}", e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailResetEmail(String token, String email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        // Build content
        String emailUpdateLink = UriComponentsBuilder
                .fromUri(emailUpdatePageUri)
                .queryParam("token", token)
                .build()
                .toUriString();
        Context context = new Context();
        context.setVariable("emailUpdateLink", emailUpdateLink);
        String text = templateEngine.process(emailUpdateTemplate, context);

        try {
            helper.setFrom(emailSenderAddress, emailSenderName);
            helper.setTo(email);
            helper.setSubject(emailUpdateSubject);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.error("Email wasn't sent: {}", e.getMessage());
        }
    }
}
