
package com.coffeehouse.service;

import com.coffeehouse.dto.LoginResponse;
import com.coffeehouse.dto.RegisterResponse;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Set<String> ALLOWED_ROLES = Set.of("admin", "customer", "chef", "waiter");


    // ✅ Registration Logic
    public RegisterResponse register(Customer customer, String confirmPassword) {

        if (repository.findByUserName(customer.getUserName()).isPresent()) {
            return new RegisterResponse(false, "❌ Username already taken. Try another.");
        }

        if (repository.findByEmail(customer.getEmail()).isPresent()) {
            return new RegisterResponse(false, "❌ Email already registered!");
        }

        if (!customer.getPassword().equals(confirmPassword)) {
            return new RegisterResponse(false, "❌ Passwords do not match!");
        }

        if (customer.getRole() == null || customer.getRole().trim().isEmpty()) {
            return new RegisterResponse(false, "❌ Role is required!");
        }

        String role = customer.getRole().trim().toLowerCase();

        if (!ALLOWED_ROLES.contains(role)) {
            return new RegisterResponse(false, "❌ Invalid role! Allowed roles: admin, customer, chef, waiter");
        }

        if ("admin".equalsIgnoreCase(role)) {
            Optional<Customer> existingAdmin = repository.findByRoleIgnoreCase("admin");
            if (existingAdmin.isPresent()) {
                return new RegisterResponse(false, "❌ Admin already exists! Only one admin is allowed.");
            }
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        repository.save(customer);

        return new RegisterResponse(true, "✅ Registration successful!", customer.getRole(), customer.getUserName());
    }


    // ✅ Simplified Login Logic (No role required)
    public LoginResponse login(String identifier, String password) {
        // identifier can be either email or username
        Optional<Customer> existing = repository.findByEmail(identifier);

        if (existing.isEmpty()) {
            existing = repository.findByUserName(identifier);
            if (existing.isEmpty()) {
                return new LoginResponse(false, "❌ User not found!", null);
            }
        }

        Customer customer = existing.get();

        if (!passwordEncoder.matches(password, customer.getPassword())) {
            return new LoginResponse(false, "❌ Invalid password!", null);
        }

        return new LoginResponse(true, "✅ Login successful!", customer.getRole(), customer.getUserName(), customer.getId());
    }


    // ✅ Profile Update Logic
    public RegisterResponse updateProfile(String email, String newUserName, String newPhoneNumber) {
        Optional<Customer> existing = repository.findByEmail(email);

        if (existing.isEmpty()) {
            return new RegisterResponse(false, "❌ User not found!");
        }

        Customer customer = existing.get();
        boolean updated = false;

        if (newUserName != null && !newUserName.trim().isEmpty()) {
            customer.setUserName(newUserName.trim());
            updated = true;
        }

        if (newPhoneNumber != null && !newPhoneNumber.trim().isEmpty()) {
            customer.setPhoneNumber(newPhoneNumber.trim());
            updated = true;
        }

        if (!updated) {
            return new RegisterResponse(false, "⚠️ No valid fields to update!");
        }

        repository.save(customer);

        return new RegisterResponse(true, "✅ Profile updated successfully!", customer.getRole(), customer.getUserName());
    }
}
