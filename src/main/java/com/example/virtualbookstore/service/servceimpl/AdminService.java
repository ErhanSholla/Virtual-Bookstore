package com.example.virtualbookstore.service.servceimpl;

import com.example.virtualbookstore.entity.Book;
import com.example.virtualbookstore.entity.Role;
import com.example.virtualbookstore.entity.User;
import com.example.virtualbookstore.registration.RegistrationRequest;
import com.example.virtualbookstore.repository.BookRepository;
import com.example.virtualbookstore.repository.RoleRepository;
import com.example.virtualbookstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    public User updateUser(Long id, RegistrationRequest registrationRequest){
        Optional<User> user1 = userRepository.findById(id);
        user1.get().setFirstName(registrationRequest.getFirstName());
        user1.get().setLastName(registrationRequest.getLastName());
        user1.get().setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        Role role = roleRepository.findByName(registrationRequest.getRole().getName());
        user1.get().setRole(List.of(role));
        return userRepository.save(user1.get());
    }
    public void deleteById(Long id){
        userRepository.deleteById(id);
    }

    public Book addBook(Book book){
        return bookRepository.save(book);
    }

    public void deleteBookById(Long id){
        bookRepository.deleteById(id);
    }
}
