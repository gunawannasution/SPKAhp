package com.hop.dao;

import com.hop.model.Kriteria;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KriteriaDAOImpl implements KriteriaDAO {
   
    public KriteriaDAOImpl() {
        
    }

    @Override
    public boolean insert(Kriteria kriteria) {
        String sql = "INSERT INTO kriteria (kode, nama, keterangan, bobot) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, kriteria.getKodeKriteria());
            stmt.setString(2, kriteria.getNamaKriteria());
            stmt.setString(3, kriteria.getKetKriteria());
            stmt.setDouble(4, kriteria.getBobot());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    kriteria.setIdKriteria(generatedKeys.getInt(1));
                }
            }

            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menambah kriteria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Kriteria> getAll() {
        List<Kriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM kriteria ORDER BY id";

        try (Connection conn = DBConnection.getConnection();Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new Kriteria(
                        rs.getInt("id"),
                        rs.getString("kode"),
                        rs.getString("nama"),
                        rs.getString("keterangan"),
                        rs.getDouble("bobot")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil kriteria: " + e.getMessage());
        }

        return list;
    }

    @Override
    public Kriteria getById(int id) {
        String sql = "SELECT * FROM kriteria WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Kriteria(
                            rs.getInt("id"),
                            rs.getString("kode"),
                            rs.getString("nama"),
                            rs.getString("keterangan"),
                            rs.getDouble("bobot")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil kriteria berdasarkan ID: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Kriteria getByKode(String kode) {
        String sql = "SELECT * FROM kriteria WHERE kode = ?";

        try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Kriteria(
                            rs.getInt("id"),
                            rs.getString("kode"),
                            rs.getString("nama"),
                            rs.getString("keterangan"),
                            rs.getDouble("bobot")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil kriteria berdasarkan kode: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean update(Kriteria kriteria) {
        String sql = "UPDATE kriteria SET kode = ?, nama = ?, keterangan = ?, bobot = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kriteria.getKodeKriteria());
            stmt.setString(2, kriteria.getNamaKriteria());
            stmt.setString(3, kriteria.getKetKriteria());
            stmt.setDouble(4, kriteria.getBobot());
            stmt.setInt(5, kriteria.getIdKriteria());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengupdate kriteria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateBobot(String namaKriteria, double bobot) {
        String sql = "UPDATE kriteria SET bobot = ? WHERE nama = ?";

        try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, bobot);
            stmt.setString(2, namaKriteria);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal mengupdate bobot: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateAllBobot(List<Kriteria> kriteriaList) {
        String sql = "UPDATE kriteria SET bobot = ? WHERE id = ?";
        boolean success = true;

        try {
            DBConnection.getConnection().setAutoCommit(false);
            try (Connection conn = DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (Kriteria k : kriteriaList) {
                    stmt.setDouble(1, k.getBobot());
                    stmt.setInt(2, k.getIdKriteria());
                    stmt.addBatch();
                }

                int[] results = stmt.executeBatch();
                for (int result : results) {
                    if (result <= 0) {
                        success = false;
                        break;
                    }
                }

                if (success) {
                    DBConnection.getConnection().commit();
                } else {
                    DBConnection.getConnection().rollback();
                }
            }
        } catch (SQLException e) {
            success = false;
            try {
                DBConnection.getConnection().rollback();
            } catch (SQLException ex) {
                System.err.println("Gagal rollback saat update batch: " + ex.getMessage());
            }
            System.err.println("Gagal batch update bobot: " + e.getMessage());
        } finally {
            try {
                DBConnection.getConnection().setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Gagal mengatur auto-commit: " + e.getMessage());
            }
        }

        return success;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM kriteria WHERE id = ?";

        try (Connection conn=DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Gagal menghapus kriteria: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int count() {
        String sql = "SELECT COUNT(*) FROM kriteria";

        try (Connection conn=DBConnection.getConnection();Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Gagal menghitung jumlah kriteria: " + e.getMessage());
        }

        return 0;
    }

    @Override
    public boolean isKodeExist(String kode) {
        String sql = "SELECT COUNT(*) FROM kriteria WHERE kode = ?";

        try (Connection conn=DBConnection.getConnection();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengecek keberadaan kode: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
