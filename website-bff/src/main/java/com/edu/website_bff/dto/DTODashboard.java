package com.edu.website_bff.dto;

import java.util.List;

public class DTODashboard {
    private String namaUser;
    private String email;
    private int totalKursusDiikuti;
    private List<DTOEnrolledCourse> kursusAktif;

    // 1. No-Args Constructor (Dibutuhkan oleh library seperti Jackson/JSON)
    public DTODashboard() {
    }

    // 2. All-Args Constructor (Untuk memudahkan Anda mengisi data objek)
    public DTODashboard(String namaUser, String email, int totalKursusDiikuti, List<DTOEnrolledCourse> kursusAktif) {
        this.namaUser = namaUser;
        this.email = email;
        this.totalKursusDiikuti = totalKursusDiikuti;
        this.kursusAktif = kursusAktif;
    }

    public String getNamaUser() {
        return this.namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getTotalKursusDiikuti() {
        return this.totalKursusDiikuti;
    }

    public void setTotalKursusDiikuti(int totalKursusDiikuti) {
        this.totalKursusDiikuti = totalKursusDiikuti;
    }

    public List<DTOEnrolledCourse> getKursusAktif() {
        return this.kursusAktif;
    }

    public void setKursusAktif(List<DTOEnrolledCourse> kursusAktif) {
        this.kursusAktif = kursusAktif;
    }

}