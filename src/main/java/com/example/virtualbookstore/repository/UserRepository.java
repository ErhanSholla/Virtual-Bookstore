package com.example.virtualbookstore.repository;

import com.example.virtualbookstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository  extends JpaRepository<User,Long> {
     Optional<User> findByEmail(String email);
     Optional<User> findByFirstName(String username);
}
