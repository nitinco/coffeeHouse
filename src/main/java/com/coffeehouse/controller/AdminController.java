package com.coffeehouse.controller;

import com.coffeehouse.dto.RegisterResponse;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // ✅ Add new customer (Admin only)
    @PostMapping("/add-customer")
    public RegisterResponse addCustomer(@RequestBody Map<String, String> request) {
        Customer customer = Customer.builder()
                .userName(request.get("userName"))
                .email(request.get("email"))
                .phoneNumber(request.get("phoneNumber"))
                .password(request.get("password"))
                .role(request.get("role"))
                .build();

        return adminService.addCustomer(customer, request.get("confirmPassword"));
    }

    // ✅ Delete customer by email
    @DeleteMapping("/delete/{email}")
    public RegisterResponse deleteCustomer(@PathVariable String email) {
        return adminService.deleteCustomerByEmail(email);
    }

    // ✅ View all customers
    @GetMapping("/all")
    public List<Customer> getAllCustomers() {
        return adminService.getAllCustomers();
    }

    // ✅ Get single customer by email
    @GetMapping("/get/{email}")
    public Customer getCustomerByEmail(@PathVariable String email) {
        return adminService.getCustomerByEmail(email)
                .orElse(null);
    }
}
