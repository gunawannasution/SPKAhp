package utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection conn;

    public static Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                String url = "jdbc:mysql://localhost:3306/spkhop";
                String user = "root";
                String password = "root"; // ganti sesuai konfigurasi kamu
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Gagal terhubung ke database", e);
        }
        return conn;
    }
}


