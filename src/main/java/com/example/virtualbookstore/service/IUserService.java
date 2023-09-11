package com.example.virtualbookstore.service;

import com.example.virtualbookstore.registration.RegistrationRequest;
import com.example.virtualbookstore.entity.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    List<User> findAllUsers();
    User addUser(RegistrationRequest request);
    Optional<User> findByEmail(String email);


    void saveUserVerificationToken(User user, String verificationToken);

    String validateToken(String token);

    void createPasswordResetTokenForUser(User user, String passwordToken);

    String validatePasswordResetToken(String token);
    User findUserByPasswordToken(String token);

    void resetUserPassword(User user, String newPassword);
    Optional<User> findByUsername(String username);
}
