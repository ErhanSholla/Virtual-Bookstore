package com.example.virtualbookstore.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.virtualbookstore.config.JWTConfig;
import com.example.virtualbookstore.entity.Role;
import com.example.virtualbookstore.entity.User;
import com.example.virtualbookstore.model.AuthResponse;
import com.example.virtualbookstore.model.UsernamePasswordDto;
import com.example.virtualbookstore.service.servceimpl.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/v1/auth")
public class LoginController {
    private final Algorithm algorithm;
    private final JWTConfig jwtConfig;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    public LoginController(JWTConfig jwtConfig, JWTConfig jwtConfig1, UserService userService, PasswordEncoder passwordEncoder){
        algorithm=Algorithm.HMAC256(jwtConfig.getSignature());
        this.jwtConfig = jwtConfig1;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginJwtToken(@RequestBody UsernamePasswordDto usernamePasswordDto){
        Optional<User>user =userService.findByUsername(usernamePasswordDto.getUsername());
        if(user.isEmpty()){
            throw new UsernameNotFoundException("User not found "+ user.get().getFirstName());
        }
        if(!passwordEncoder.matches(usernamePasswordDto.getPassword(),user.get().getPassword())){
            throw new  RuntimeException("Password dont Match");
        }
        List<String> roles = user.get().getRole().stream().map(Role::getName).toList();

        Instant issuedAt = Instant.now();
        Instant expiresAt =Instant.ofEpochSecond(issuedAt.getEpochSecond() + 6000);
        Instant refreshTokenExpiresAt = Instant.ofEpochSecond(issuedAt.getEpochSecond() + 1800);
        String token = JWT.create().withIssuer(jwtConfig.getIssuer())
                .withIssuedAt(Instant.now())
                .withExpiresAt(expiresAt)
                .withClaim("username",usernamePasswordDto.getUsername())
                .withClaim("roles",roles)
                .sign(algorithm);
        String refreshToken = JWT.create().withIssuer(jwtConfig.getIssuer())
                .withIssuedAt(Instant.now())
                .withExpiresAt(refreshTokenExpiresAt)
                .withClaim("username",usernamePasswordDto.getUsername())
                .withClaim("roles",roles)
                .sign(algorithm)
                ;
        return ResponseEntity.ok(new AuthResponse(token, refreshToken));
    }



}
