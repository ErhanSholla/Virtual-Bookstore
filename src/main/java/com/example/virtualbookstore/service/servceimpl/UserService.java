package com.example.virtualbookstore.service.servceimpl;

import com.example.virtualbookstore.entity.Role;
import com.example.virtualbookstore.exception.UserAlreadyExistException;
import com.example.virtualbookstore.registration.RegistrationRequest;
import com.example.virtualbookstore.registration.token.VerificationToken;
import com.example.virtualbookstore.repository.RoleRepository;
import com.example.virtualbookstore.repository.UserRepository;
import com.example.virtualbookstore.repository.VerificationTokenRepository;
import com.example.virtualbookstore.service.IUserService;
import com.example.virtualbookstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RoleRepository roleRepository;
    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(RegistrationRequest request) {
        Optional<User> theUser = this.findByEmail(request.getEmail());
        if(theUser.isPresent()){
            throw new UserAlreadyExistException("User with email " + request.getEmail() + " already  exist");
        }
        User newUser = new User();
        newUser.setFirstName(request.getFirstName());
        newUser.setLastName(request.getLastName());
        newUser.setEmail(request.getEmail());
        String rawPassword = request.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }else {
            newUser.setPassword(passwordEncoder.encode(rawPassword));
        }
        Role userRole = roleRepository.findByName(request.getRole().getName());
        newUser.setRole(List.of(userRole));
        return userRepository.save(newUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUserVerificationToken(User user, String verificationToken) {
        VerificationToken verificationToken1 = new VerificationToken(verificationToken,user);
        verificationTokenRepository.save(verificationToken1);
    }

    @Override
    public String validateToken(String token) {
        VerificationToken token1 = verificationTokenRepository.findByToken(token);
        if(token1 == null){
            return "Invalid verification token";
        }
        User user = token1.getUser();
        Calendar calendar = Calendar.getInstance();
        if((token1.getExpirationTime().getTime() - calendar.getTime().getTime()) <=0){
            verificationTokenRepository.delete(token1);
            return "Token already exists";
        }
        user.setEnabled(true);
        userRepository.save(user);
        return "valid";
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String passwordToken) {
        passwordResetTokenService.createPasswordResetToken(user,passwordToken);
    }

    @Override
    public String validatePasswordResetToken(String token) {
        return passwordResetTokenService.validateToken(token);
    }

    @Override
    public User findUserByPasswordToken(String token) {
        return passwordResetTokenService.findUserByPasswordToken(token).get();
    }

    @Override
    public void resetUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByFirstName(username);
    }
}
