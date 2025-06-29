package com.hop.dao;

import com.hop.model.Kriteria;
import java.util.List;

public interface KriteriaDAO {

    /**
     * Menyisipkan data kriteria baru.
     * @param kriteria objek kriteria
     * @return true jika berhasil, false jika gagal
     */
    boolean insert(Kriteria kriteria);

    /**
     * Mengambil semua data kriteria.
     * @return list semua kriteria
     */
    List<Kriteria> getAll();

    /**
     * Mengambil kriteria berdasarkan ID.
     * @param id ID kriteria
     * @return objek kriteria jika ditemukan, null jika tidak
     */
    Kriteria getById(int id);

    /**
     * Mengambil kriteria berdasarkan kode unik.
     * @param kode kode kriteria
     * @return objek kriteria jika ditemukan, null jika tidak
     */
    Kriteria getByKode(String kode);

    /**
     * Memperbarui data kriteria berdasarkan ID.
     * @param kriteria objek kriteria
     * @return true jika berhasil, false jika gagal
     */
    boolean update(Kriteria kriteria);

    /**
     * Memperbarui bobot berdasarkan nama kriteria.
     * @param namaKriteria nama kriteria
     * @param bobot bobot baru
     * @return true jika berhasil, false jika gagal
     */
    boolean updateBobot(String namaKriteria, double bobot);

    /**
     * Memperbarui semua bobot kriteria (batch).
     * @param kriteriaList daftar kriteria
     * @return true jika semua berhasil, false jika ada yang gagal
     */
    boolean updateAllBobot(List<Kriteria> kriteriaList);

    /**
     * Menghapus data kriteria berdasarkan ID.
     * @param id ID kriteria
     * @return true jika berhasil, false jika gagal
     */
    boolean delete(int id);

    /**
     * Menghitung jumlah total kriteria.
     * @return jumlah kriteria
     */
    int count();

    /**
     * Mengecek apakah kode kriteria sudah ada.
     * @param kode kode kriteria
     * @return true jika ada, false jika belum
     */
    boolean isKodeExist(String kode);
    
}
