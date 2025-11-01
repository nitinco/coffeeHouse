package com.coffeehouse.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String role;
    private String name; // optional: useful for frontend routing
    private Long id; // add this to pass customerId to frontend

    // Updated constructor with id
    public LoginResponse(boolean success, String message, String role, String name, Long id) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.name = name;
        this.id = id;
    }

    // Optional convenience constructor without name/id
    public LoginResponse(boolean success, String message, String role) {
        this(success, message, role, null, null);
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {   // getter for id
        return id;
    }

    public void setId(Long id) { // setter for id
        this.id = id;
    }
}
