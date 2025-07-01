package com.hop.model;

public class HasilAlternatif {
    
    private int id;
    private int idAlternatif;
    private double skor;
    private int peringkat;
    private String rekomendasi;

    public HasilAlternatif(int id, int idAlternatif, double skor, int peringkat, String rekomendasi) {
        this.id = id;
        this.idAlternatif = idAlternatif;
        this.skor = skor;
        this.peringkat = peringkat;
        this.rekomendasi = rekomendasi;
    }

    public int getId() { return id; }
    public int getIdAlternatif() { return idAlternatif; }
    public double getSkor() { return skor; }
    public int getPeringkat() { return peringkat; }
    public String getRekomendasi() { return rekomendasi; }

}
