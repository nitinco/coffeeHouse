package com.coffeehouse.dto;

public class RegisterResponse {
    private boolean success;
    private String message;
    private String role;
    private String name;

    public RegisterResponse() {} // default no-args constructor

    // ✅ Fix: initialize fields properly in this constructor
    public RegisterResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RegisterResponse(boolean success, String message, String role, String name) {
        this.success = success;
        this.message = message;
        this.role = role;
        this.name = name;
    }

    // Getters and Setters
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
}
