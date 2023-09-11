package com.example.virtualbookstore.controller;

import com.example.virtualbookstore.service.servceimpl.UserService;
import com.example.virtualbookstore.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> getUsers(){
        return  userService.findAllUsers();
    }


}
