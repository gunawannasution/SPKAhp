package com.hop.dao;

import com.hop.model.Matrix;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import utils.DBConnection;
import static utils.DBConnection.getConnection;

public class MatrixDAOImpl implements MatrixDAO{
    private final Connection conn;

     
    public MatrixDAOImpl(){
        this.conn = DBConnection.getConnection();
    }
   
    @Override
    public boolean simpanMatrix(List<Matrix> matrixData){
        String sqlInsert = "INSERT INTO matrix_normalisasi (kriteria_row, kriteria_col, nilai) VALUES (?, ?, ?)";
        String sqlDelete = "DELETE FROM matrix_normalisasi"; // Hapus data lama sebelum insert baru

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement deleteStmt = conn.prepareStatement(sqlDelete)) {
                deleteStmt.executeUpdate();
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                for (Matrix mn : matrixData) {
                    insertStmt.setString(1, mn.getKriteriaRow());
                    insertStmt.setString(2, mn.getKriteriaCol());
                    insertStmt.setDouble(3, mn.getNilai());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
