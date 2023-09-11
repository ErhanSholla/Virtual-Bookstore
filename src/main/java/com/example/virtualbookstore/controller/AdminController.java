package com.example.virtualbookstore.controller;

import com.example.virtualbookstore.entity.Book;
import com.example.virtualbookstore.entity.User;
import com.example.virtualbookstore.model.UserRegistrationDetails;
import com.example.virtualbookstore.registration.RegistrationRequest;
import com.example.virtualbookstore.service.servceimpl.AdminService;
import com.example.virtualbookstore.service.servceimpl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/users")
    public List<User> getUsers(){
        return  userService.findAllUsers();
    }
    @PutMapping("/users/{id}")
    public User updateUser(@PathVariable("id") Long id, @RequestBody RegistrationRequest registrationRequest){
        return adminService.updateUser(id,registrationRequest);
    }
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable("id")Long id){
        adminService.deleteById(id);
    }

    @PostMapping("/book/addbook")
    public Book addBook(@RequestBody Book book){
        return adminService.addBook(book);
    }
    @PostMapping("/book/delete/{id}")
    public void deleteBookById(@PathVariable("id") Long id){
        adminService.deleteBookById(id);
    }

}
