package com.hop.model;

import java.util.List;

/**
 * Model untuk entitas Kriteria.
 */
public class Kriteria implements PdfExportable {
    private int idKriteria;
    private String kodeKriteria;
    private String namaKriteria;
    private String ketKriteria;
    private double bobot;

    // Konstruktor default
    public Kriteria() {}

    // Konstruktor lengkap
    public Kriteria(int idKriteria, String kodeKriteria, String namaKriteria, String ketKriteria, double bobot) {
        this.idKriteria = idKriteria;
        this.kodeKriteria = kodeKriteria;
        this.namaKriteria = namaKriteria;
        this.ketKriteria = ketKriteria;
        this.bobot = bobot;
    }

    // Getter dan Setter
    public int getIdKriteria() {
        return idKriteria;
    }

    public void setIdKriteria(int idKriteria) {
        this.idKriteria = idKriteria;
    }

    public String getKodeKriteria() {
        return kodeKriteria;
    }

    public void setKodeKriteria(String kodeKriteria) {
        this.kodeKriteria = kodeKriteria;
    }

    public String getNamaKriteria() {
        return namaKriteria;
    }

    public void setNamaKriteria(String namaKriteria) {
        this.namaKriteria = namaKriteria;
    }

    public String getKetKriteria() {
        return ketKriteria;
    }

    public void setKetKriteria(String ketKriteria) {
        this.ketKriteria = ketKriteria;
    }

    public double getBobot() {
        return bobot;
    }

    public void setBobot(double bobot) {
        this.bobot = bobot;
    }

    @Override
    public String toString() {
        return namaKriteria + " (" + kodeKriteria + ")";
    }
    
    @Override
    public List<String> toPdfRow() {
        return List.of(String.valueOf(idKriteria),kodeKriteria,namaKriteria,ketKriteria,String.valueOf(bobot));
    }
}
