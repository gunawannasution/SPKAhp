package com.hop.dao;

import com.hop.model.MatrixAlternatif;
import utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixAlternatifDAOImpl implements MatrixAlternatifDAO {
    private final Connection conn;

    public MatrixAlternatifDAOImpl() {
        this.conn = DBConnection.getConnection();
    }

    @Override
    public boolean insert(MatrixAlternatif m) {
        String sql = "INSERT INTO matrix_alternatif (id_alternatif, id_kriteria, nilai) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.getIdAlternatif());
            ps.setInt(2, m.getIdKriteria());
            ps.setDouble(3, m.getNilai());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            // TODO: gunakan logger jika memungkinkan
            System.err.println("Insert error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean update(MatrixAlternatif m) {
        String sql = "UPDATE matrix_alternatif SET nilai = ? WHERE id_alternatif = ? AND id_kriteria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, m.getNilai());
            ps.setInt(2, m.getIdAlternatif());
            ps.setInt(3, m.getIdKriteria());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Update error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(int idAlternatif, int idKriteria) {
        String sql = "DELETE FROM matrix_alternatif WHERE id_alternatif = ? AND id_kriteria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlternatif);
            ps.setInt(2, idKriteria);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Delete error: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<MatrixAlternatif> getAll() {
        List<MatrixAlternatif> list = new ArrayList<>();
        String sql = "SELECT * FROM matrix_alternatif";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapResultSetToMatrix(rs));
            }
        } catch (SQLException e) {
            System.err.println("GetAll error: " + e.getMessage());
        }
        return list;
    }

    @Override
    public MatrixAlternatif getByAlternatifAndKriteria(int idAlt, int idKri) {
        String sql = "SELECT * FROM matrix_alternatif WHERE id_alternatif = ? AND id_kriteria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAlt);
            ps.setInt(2, idKri);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMatrix(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("GetByAlternatifAndKriteria error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<MatrixAlternatif> getByKriteria(int idKriteria) {
        List<MatrixAlternatif> list = new ArrayList<>();
        String sql = "SELECT * FROM matrix_alternatif WHERE id_kriteria = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idKriteria);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToMatrix(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("GetByKriteria error: " + e.getMessage());
        }
        return list;
    }

    // Helper method untuk mapping ResultSet ke MatrixAlternatif
    private MatrixAlternatif mapResultSetToMatrix(ResultSet rs) throws SQLException {
        return new MatrixAlternatif(
            rs.getInt("id"),
            rs.getInt("id_alternatif"),
            rs.getInt("id_kriteria"),
            rs.getDouble("nilai")
        );
    }
}
