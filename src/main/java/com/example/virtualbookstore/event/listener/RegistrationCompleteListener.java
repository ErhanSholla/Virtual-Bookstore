package com.example.virtualbookstore.event.listener;

import com.example.virtualbookstore.event.RegistrationCompleteEvent;
import com.example.virtualbookstore.service.servceimpl.UserService;
import com.example.virtualbookstore.entity.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationCompleteListener implements ApplicationListener<RegistrationCompleteEvent> {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private  User user;
    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String verificationToken = UUID.randomUUID().toString();
        userService.saveUserVerificationToken(user,verificationToken);
        String url = event.getApplicationUrl() + "/v1/register/verifyYourEmail?token="+verificationToken;
//        log.info("Click the link to verify your registration : {}", url);
        try {
            sendVerificationEmail(url);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "User Registration Portal Service";
        String mailContent ="<p>Hi, "+ user.getFirstName()+ "</p>"+
                "<p>Thank you for your registering with us,"+""+
                "Please, Follow link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you for Your Service </p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("eskenderbe@gmail.com",senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent,true);
        mailSender.send(message);
    }



    public void sentPasswordResetVerificationEmail(String url,User user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "User Registration Portal Service";
        String mailContent ="<p>Hi, "+ user.getFirstName()+ "</p>"+
                "<p>Thank you for your registering with us,"+""+
                "Please, Follow link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you for Your Service </p>";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("eskenderbe@gmail.com",senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent,true);
        mailSender.send(message);
    }
}
