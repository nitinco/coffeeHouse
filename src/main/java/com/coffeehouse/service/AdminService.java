package com.coffeehouse.service;

import com.coffeehouse.dto.RegisterResponse;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AdminService {

    @Autowired
    private CustomerRepository repository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final Set<String> ALLOWED_ROLES = Set.of("admin", "customer", "chef", "waiter");

    // ✅ Add new customer (Admin only)
    public RegisterResponse addCustomer(Customer customer, String confirmPassword) {

        if (repository.findByUserName(customer.getUserName()).isPresent()) {
            return new RegisterResponse(false, "❌ Username already exists!");
        }
        if (repository.findByEmail(customer.getEmail()).isPresent()) {
            return new RegisterResponse(false, "❌ Email already registered!");
        }
        if (!customer.getPassword().equals(confirmPassword)) {
            return new RegisterResponse(false, "❌ Passwords do not match!");
        }

        String role = customer.getRole() != null ? customer.getRole().trim().toLowerCase() : "customer";
        if (!ALLOWED_ROLES.contains(role)) {
            return new RegisterResponse(false, "❌ Invalid role! Allowed roles: admin, customer, chef, waiter");
        }

        if ("admin".equalsIgnoreCase(role)) {
            Optional<Customer> existingAdmin = repository.findByRoleIgnoreCase("admin");
            if (existingAdmin.isPresent()) {
                return new RegisterResponse(false, "❌ An admin already exists!");
            }
        }

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer.setRole(role);
        repository.save(customer);

        return new RegisterResponse(true, "✅ Customer added successfully!", customer.getRole(), customer.getUserName());
    }

    // ✅ Delete a customer by ID or Email
    public RegisterResponse deleteCustomerByEmail(String email) {
        Optional<Customer> existing = repository.findByEmail(email);

        if (existing.isEmpty()) {
            return new RegisterResponse(false, "❌ Customer not found!");
        }

        Customer customer = existing.get();

        if ("admin".equalsIgnoreCase(customer.getRole())) {
            return new RegisterResponse(false, "❌ Cannot delete admin account!");
        }

        repository.delete(customer);
        return new RegisterResponse(true, "✅ Customer deleted successfully!");
    }

    // ✅ Get all customers
    public List<Customer> getAllCustomers() {
        return repository.findAll();
    }

    // ✅ Get customer by email
    public Optional<Customer> getCustomerByEmail(String email) {
        return repository.findByEmail(email);
    }
}
