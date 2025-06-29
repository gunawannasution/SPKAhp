package com.hop.model;

public class MatrixAlternatif {
    private int id;
    private int idAlternatif;   // atau objek Alternatif alternatif;
    private int idKriteria;     // atau objek Kriteria kriteria;
    private double nilai;       // nilai normalisasi alternatif terhadap kriteria

    public MatrixAlternatif() {}

    public MatrixAlternatif(int id, int idAlternatif, int idKriteria, double nilai) {
        this.id = id;
        this.idAlternatif = idAlternatif;
        this.idKriteria = idKriteria;
        this.nilai = nilai;
    }

    public int getId() {
        return id;
    }

    public int getIdAlternatif() {
        return idAlternatif;
    }

    public int getIdKriteria() {
        return idKriteria;
    }

    public double getNilai() {
        return nilai;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setIdAlternatif(int idAlternatif) {
        this.idAlternatif = idAlternatif;
    }

    public void setIdKriteria(int idKriteria) {
        this.idKriteria = idKriteria;
    }

    public void setNilai(double nilai) {
        this.nilai = nilai;
    }
}
