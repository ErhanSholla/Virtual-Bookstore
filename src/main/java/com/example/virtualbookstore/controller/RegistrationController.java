package com.example.virtualbookstore.controller;

import com.example.virtualbookstore.event.RegistrationCompleteEvent;
import com.example.virtualbookstore.event.listener.RegistrationCompleteListener;
import com.example.virtualbookstore.registration.RegistrationRequest;
import com.example.virtualbookstore.registration.password.PasswordResetRequest;
import com.example.virtualbookstore.registration.token.VerificationToken;
import com.example.virtualbookstore.repository.VerificationTokenRepository;
import com.example.virtualbookstore.service.servceimpl.PasswordResetTokenService;
import com.example.virtualbookstore.service.servceimpl.UserService;
import com.example.virtualbookstore.entity.User;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RegistrationCompleteListener registrationCompleteListener;
    private final PasswordResetTokenService passwordResetTokenService;

    @PostMapping
    public String registerUser(@RequestBody RegistrationRequest request, final HttpServletRequest rq){
        User user = userService.addUser(request);
        applicationEventPublisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(rq)));
        return "Success! Please, check your email to complete registration";
    }

    @GetMapping("/verifyYourEmail")
    public String verifyEmail(@RequestParam("token") String token){
        VerificationToken verifyToken = verificationTokenRepository.findByToken(token);
        if(verifyToken.getUser().isEnabled()){
            return "This Account is already verified, please Login";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            verificationTokenRepository.delete(verifyToken);
            return "Email verified Succesfully. Now you can login to your account";
        }
        return "Invalid verification token";
    }

    @PostMapping("/password-reset-request")
    public String resetPasswordRequest(@RequestBody PasswordResetRequest passwordResetRequest,
                                       final HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        Optional<User> user = userService.findByEmail(passwordResetRequest.getEmail());
        String passwordResetUrl = "";
        if(user.isPresent()){
            String passwordResetToken = UUID.randomUUID().toString();
            userService.createPasswordResetTokenForUser(user.get(),passwordResetToken);
            passwordResetUrl = passwordResetEmailUrl(user.get(),applicationUrl(httpServletRequest),passwordResetToken);

        }
        return passwordResetUrl;
    }

    private String passwordResetEmailUrl(User user, String s, String passwordResetToken) throws MessagingException, UnsupportedEncodingException {

        String url = s + "/v1/register/resetPassword?token="+passwordResetToken;
        registrationCompleteListener.sentPasswordResetVerificationEmail(url,user);
        log.info("Click the link to reset your password : {}", url);
        return url;
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody PasswordResetRequest passwordResetRequest,
                                @RequestParam("token") String token){
        String tokenValidation = userService.validatePasswordResetToken(token);
        if (!tokenValidation.equalsIgnoreCase("valid")){
            return "Invalid Password Reset Token";
        }
        User user = userService.findUserByPasswordToken(token);
        if(user != null){
            userService.resetUserPassword(user,passwordResetRequest.getNewPassword());
            passwordResetTokenService.deleteToken(token);
            return "Password has been reset successfully";
        }
        return "Invalid Password";

    }

    private String applicationUrl(HttpServletRequest rq) {
        return "http://"+rq.getServerName()+":"+rq.getServerPort()+rq.getContextPath();
    }



}
