package com.example.virtualbookstore.registration;

import com.example.virtualbookstore.entity.Role;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RegistrationRequest {
    String firstName;
    String lastName;
    String email;
    String password;
    Role role;
}
