package com.edu.user_service.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
public class ModelUser {
    private String id;
    private String nama;
    private String email;
    private String role;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return this.nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    // "STUDENT" atau "ADMIN"
}
