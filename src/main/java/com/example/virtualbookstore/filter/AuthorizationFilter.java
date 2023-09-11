package com.example.virtualbookstore.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.virtualbookstore.config.JWTConfig;
import com.example.virtualbookstore.model.LoggedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;

public class AuthorizationFilter extends OncePerRequestFilter {
    private final JWTVerifier jwtVerifier;

    public AuthorizationFilter(JWTConfig jwtConfig) {
        Algorithm algorithm = Algorithm.HMAC256(jwtConfig.getSignature());
        jwtVerifier = JWT.require(algorithm).withIssuer(jwtConfig.getIssuer())
                .build();
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if(authorization == null || authorization.isEmpty()){
            filterChain.doFilter(request,response);
            return;
        }
        String token= authorization.split(" ")[1];
        try{
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            if(decodedJWT.getExpiresAt().before(new Date())){
                filterChain.doFilter(request,response);
                return;
            }
            SecurityContextHolder.getContext().setAuthentication(new LoggedUser(decodedJWT.getClaim("username")
                    .asString(),decodedJWT.getClaim("roles").asList(String.class)));
        }catch (JWTVerificationException ex){

        }
        filterChain.doFilter(request,response);
    }
}
