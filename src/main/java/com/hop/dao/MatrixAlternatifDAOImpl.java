package com.hop.dao;

import com.hop.model.MatrixAlternatif;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementasi DAO untuk entitas MatrixAlternatif.
 * Mengelola operasi CRUD pada tabel matrix_alternatif.
 */
public class MatrixAlternatifDAOImpl implements MatrixAlternatifDAO {
    private static final Logger logger = Logger.getLogger(MatrixAlternatifDAOImpl.class.getName());

    /**
     * Tambah data MatrixAlternatif baru ke database.
     * @param m objek MatrixAlternatif
     * @return true jika berhasil, false jika gagal
     */
    @Override
    public boolean insert(MatrixAlternatif m) {
        String query = "INSERT INTO matrix_alternatif (id_alternatif, id_kriteria, nilai) VALUES (?, ?, ?)";
        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {

            perintah.setInt(1, m.getIdAlternatif());
            perintah.setInt(2, m.getIdKriteria());
            perintah.setDouble(3, m.getNilai());

            int hasil = perintah.executeUpdate();
            return hasil > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal menambahkan data", e);
            return false;
        }
    }

    /**
     * Update nilai pada MatrixAlternatif berdasarkan id_alternatif dan id_kriteria.
     * @param m objek MatrixAlternatif dengan nilai terbaru
     * @return true jika update berhasil
     */
    @Override
    public boolean update(MatrixAlternatif m) {
        String query = "UPDATE matrix_alternatif SET nilai = ? WHERE id_alternatif = ? AND id_kriteria = ?";
        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {

            perintah.setDouble(1, m.getNilai());
            perintah.setInt(2, m.getIdAlternatif());
            perintah.setInt(3, m.getIdKriteria());

            return perintah.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal mengupdate data", e);
            return false;
        }
    }

    /**
     * Hapus data MatrixAlternatif berdasarkan id_alternatif dan id_kriteria.
     * @param idAlternatif id alternatif
     * @param idKriteria id kriteria
     * @return true jika berhasil dihapus
     */
    @Override
    public boolean delete(int idAlternatif, int idKriteria) {
        String query = "DELETE FROM matrix_alternatif WHERE id_alternatif = ? AND id_kriteria = ?";
        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {

            perintah.setInt(1, idAlternatif);
            perintah.setInt(2, idKriteria);

            return perintah.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal menghapus data", e);
            return false;
        }
    }

    /**
     * Ambil semua data MatrixAlternatif.
     * @return list semua data MatrixAlternatif
     */
    @Override
    public List<MatrixAlternatif> getAll() {
        List<MatrixAlternatif> semuaData = new ArrayList<>();
        String query = "SELECT * FROM matrix_alternatif";

        try (Connection koneksi = DBConnection.getConnection();
             Statement perintah = koneksi.createStatement();
             ResultSet hasil = perintah.executeQuery(query)) {

            while (hasil.next()) {
                MatrixAlternatif data = new MatrixAlternatif(
                    hasil.getInt("id"),
                    hasil.getInt("id_alternatif"),
                    hasil.getInt("id_kriteria"),
                    hasil.getDouble("nilai")
                );
                semuaData.add(data);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal mengambil semua data", e);
        }
        return semuaData;
    }

    /**
     * Cari data MatrixAlternatif berdasarkan id_alternatif dan id_kriteria.
     * @param idAlternatif id alternatif
     * @param idKriteria id kriteria
     * @return objek MatrixAlternatif jika ditemukan, null jika tidak
     */
    @Override
    public MatrixAlternatif getByAlternatifAndKriteria(int idAlternatif, int idKriteria) {
        String query = "SELECT * FROM matrix_alternatif WHERE id_alternatif = ? AND id_kriteria = ?";
        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {

            perintah.setInt(1, idAlternatif);
            perintah.setInt(2, idKriteria);

            try (ResultSet hasil = perintah.executeQuery()) {
                if (hasil.next()) {
                    return new MatrixAlternatif(
                        hasil.getInt("id"),
                        hasil.getInt("id_alternatif"),
                        hasil.getInt("id_kriteria"),
                        hasil.getDouble("nilai")
                    );
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal mencari data", e);
        }
        return null;
    }

    /**
     * Ambil daftar MatrixAlternatif berdasarkan id_kriteria.
     * @param idKriteria id kriteria
     * @return list MatrixAlternatif untuk kriteria tersebut
     */
    @Override
    public List<MatrixAlternatif> getByKriteria(int idKriteria) {
        List<MatrixAlternatif> dataKriteria = new ArrayList<>();
        String query = "SELECT * FROM matrix_alternatif WHERE id_kriteria = ?";

        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {

            perintah.setInt(1, idKriteria);

            try (ResultSet hasil = perintah.executeQuery()) {
                while (hasil.next()) {
                    dataKriteria.add(new MatrixAlternatif(
                        hasil.getInt("id"),
                        hasil.getInt("id_alternatif"),
                        hasil.getInt("id_kriteria"),
                        hasil.getDouble("nilai")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal mengambil data kriteria", e);
        }
        return dataKriteria;
    }
}
