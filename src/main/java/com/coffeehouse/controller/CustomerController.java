package com.coffeehouse.controller;

import com.coffeehouse.dto.LoginResponse;
import com.coffeehouse.dto.RegisterResponse;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService service;

    @PostMapping("/register")
    public RegisterResponse register(@RequestBody Map<String, String> request) {
        Customer customer = Customer.builder()
                .userName(request.get("userName"))
                .email(request.get("email"))
                .phoneNumber(request.get("phoneNumber"))
                .password(request.get("password"))
                .role(request.get("role"))
                .build();
        return service.register(customer, request.get("confirmPassword"));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody Map<String, String> request) {
        return service.login(
                request.get("identifier"), // changed key name
                request.get("password")
//                request.get("role")
        );
    }

    @PutMapping("/update/{email}")
    public RegisterResponse updateProfile(
            @PathVariable String email,
            @RequestBody Map<String, String> request) {
        String newUserName = request.get("userName");
        String newPhoneNumber = request.get("phoneNumber");
        return service.updateProfile(email, newUserName, newPhoneNumber);
    }

}
