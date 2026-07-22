package com.edu.course_service.dto;

public class EnrolledCourseDTO {
    private String judul;
    private String kategori;
    private int progress;

    public EnrolledCourseDTO(String judul, String kategori, int progress) {
        this.judul = judul;
        this.kategori = kategori;
        this.progress = progress;
    }

    public String getJudul() {
        return judul;
    }

    public String getKategori() {
        return kategori;
    }

    public int getProgress() {
        return progress;
    }
}
