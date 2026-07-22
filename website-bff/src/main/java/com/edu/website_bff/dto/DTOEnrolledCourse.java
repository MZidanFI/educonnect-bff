package com.edu.website_bff.dto;

public class DTOEnrolledCourse {
    private String judul;
    private String kategori;
    private int progress;

    // Constructors
    public DTOEnrolledCourse() {
    }

    public DTOEnrolledCourse(String judul, String kategori, int progress) {
        this.judul = judul;
        this.kategori = kategori;
        this.progress = progress;
    }

    // Getters and Setters
    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}