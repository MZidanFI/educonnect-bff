package com.edu.auth_service.dto;

public class ResponseLoginDTO {
    private String token;
    private String userId;
    private String role;

    public ResponseLoginDTO(String token, String userId, String role) {
        this.token = token;
        this.userId = userId;
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getRole() {
        return role;
    }
}