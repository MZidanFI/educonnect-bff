package com.edu.website_bff.dto;

public class DTOUserProfile {
    private String id;
    private String nama;
    private String email;
    private String role;

    // Constructors
    public DTOUserProfile() {
    }

    public DTOUserProfile(String id, String nama, String email, String role) {
        this.id = id;
        this.nama = nama;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}