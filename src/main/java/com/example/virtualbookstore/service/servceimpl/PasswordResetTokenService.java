package com.example.virtualbookstore.service.servceimpl;


import com.example.virtualbookstore.registration.password.PasswordResetToken;
import com.example.virtualbookstore.repository.PasswordResetTokenRepository;
import com.example.virtualbookstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PasswordResetTokenService  {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    public void createPasswordResetToken(User user,String token){
        PasswordResetToken passwordResetToken = new PasswordResetToken(token,user);
        passwordResetTokenRepository.save(passwordResetToken);
    }
    public String validateToken(String token) {
        PasswordResetToken token1 = passwordResetTokenRepository.findByToken(token);
        if(token1 == null){
            return "Invalid password reset  token";}
        User user = token1.getUser();
        Calendar calendar = Calendar.getInstance();
        if((token1.getExpirationTime().getTime() - calendar.getTime().getTime()) <=0){
            passwordResetTokenRepository.delete(token1);
            return "Link already expired, resend link";
        }
        return "valid";
    }
    public Optional<User> findUserByPasswordToken(String passwordToken){
        return Optional.ofNullable(passwordResetTokenRepository.findByToken(passwordToken).getUser());
    }

    public void deleteToken(String token){
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        passwordResetTokenRepository.delete(passwordResetToken);
    }


}
