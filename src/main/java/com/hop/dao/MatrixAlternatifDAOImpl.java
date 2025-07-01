package com.hop.dao;

import com.hop.model.MatrixAlternatif;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixAlternatifDAOImpl implements MatrixAlternatifDAO {
    private static final Logger logger = Logger.getLogger(MatrixAlternatifDAOImpl.class.getName());

    // 1. Tambah Data Matrix Alternatif
    @Override
    public boolean insert(MatrixAlternatif m) {
        String query = "INSERT INTO matrix_alternatif (id_alternatif, id_kriteria, nilai) VALUES (?, ?, ?)";
        
        try (Connection koneksi = DBConnection.getConnection();
             PreparedStatement perintah = koneksi.prepareStatement(query)) {
            
            // Set nilai parameter
            perintah.setInt(1, m.getIdAlternatif());
            perintah.setInt(2, m.getIdKriteria());
            perintah.setDouble(3, m.getNilai());
            
            // Jalankan query
            int hasil = perintah.executeUpdate();
            return hasil > 0; // True jika berhasil insert
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Gagal menambahkan data", e);
            return false;
        }
    }

    // 2. Update Data Matrix Alternatif
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

    // 3. Hapus Data Matrix Alternatif
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

    // 4. Ambil Semua Data Matrix Alternatif (PERBAIKAN ERROR UTAMA)
    @Override
    public List<MatrixAlternatif> getAll() {
        List<MatrixAlternatif> semuaData = new ArrayList<>();
        String query = "SELECT * FROM matrix_alternatif";
        
        try (Connection koneksi = DBConnection.getConnection();
             Statement perintah = koneksi.createStatement();
             ResultSet hasil = perintah.executeQuery(query)) {
            
            // Proses setiap baris hasil query
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

    // 5. Cari Data Berdasarkan Alternatif dan Kriteria
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

    // 6. Ambil Data Berdasarkan Kriteria Tertentu
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