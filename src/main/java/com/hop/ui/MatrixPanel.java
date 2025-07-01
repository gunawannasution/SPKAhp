package com.hop.ui;

import com.hop.controller.MatrixController;
import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.dao.MatrixDAOImpl;
import com.hop.model.Matrix;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixPanel extends JPanel {
    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SECONDARY_COLOR = new Color(233, 30, 99);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color CARD_SHADOW = new Color(0, 0, 0, 20);
    private static final Color TITLE_COLOR = new Color(33, 33, 33);

    private final int n = 4;
    private final String[] kriteria = {"Kompetensi", "Disiplin", "Tanggung Jawab", "Kerja Sama"};
    private final double[] randomIndex = {0, 0, 0.58, 0.9, 1.12}; // RI values for n=1-5

    private final JTextField[][] inputFields = new JTextField[n][n];
    private final JTextField[][] normFields = new JTextField[n][n];
    private final JTextField[] priorityFields = new JTextField[n];
    private boolean isUpdating = false;

    private final KriteriaDAO kriteriaDAO;
    private final MatrixController matrixController;

    public MatrixPanel() {
        this.kriteriaDAO = new KriteriaDAOImpl();
        this.matrixController = new MatrixController(new MatrixDAOImpl());

        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        mainPanel.add(createMatrixPanel("MATRIX PERBANDINGAN BERPASANGAN", inputFields, true));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createMatrixPanel("MATRIX NORMALISASI", normFields, false));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createPriorityPanel());

        JButton btnHitung = createStyledButton("HITUNG AHP", PRIMARY_COLOR);
        btnHitung.addActionListener(e -> hitungAHP());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(BACKGROUND_COLOR);
        btnPanel.add(btnHitung);

        add(mainPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        isiDiagonalAwal();
        pasangListenerReciprocal();
    }

    private JPanel createMatrixPanel(String title, JTextField[][] fields, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create a custom border with shadow effect
        Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border innerBorder = BorderFactory.createLineBorder(new Color(220, 220, 220), 1);
        Border compoundBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
        
        // Create titled border with custom styling
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            compoundBorder,
            title,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            TITLE_COLOR
        );
        
        titledBorder.setTitlePosition(TitledBorder.TOP);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            titledBorder
        ));

        JPanel grid = new JPanel(new GridLayout(n + 1, n + 1, 5, 5));
        grid.setBackground(BACKGROUND_COLOR);

        // Header row
        grid.add(createHeaderLabel(""));
        for (String k : kriteria) {
            grid.add(createHeaderLabel(k));
        }

        // Data rows
        for (int i = 0; i < n; i++) {
            grid.add(createHeaderLabel(kriteria[i]));

            for (int j = 0; j < n; j++) {
                JTextField tf = new JTextField();
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setEditable(editable && i != j);
                tf.setBackground(editable ? Color.WHITE : new Color(240, 240, 240));
                tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                    BorderFactory.createEmptyBorder(3, 3, 3, 3)
                ));
                
                if (i == j) {
                    tf.setBackground(new Color(230, 230, 230));
                }
                
                fields[i][j] = tf;
                grid.add(tf);
            }
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(TITLE_COLOR);
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private JPanel createPriorityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        
        // Create a custom border with shadow effect
        Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border innerBorder = BorderFactory.createLineBorder(new Color(220, 220, 220), 1);
        Border compoundBorder = BorderFactory.createCompoundBorder(outerBorder, innerBorder);
        
        // Create titled border with custom styling
        TitledBorder titledBorder = BorderFactory.createTitledBorder(
            compoundBorder,
            "HASIL PERHITUNGAN AHP",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            TITLE_COLOR
        );
        
        titledBorder.setTitlePosition(TitledBorder.TOP);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(5, 5, 5, 5),
            titledBorder
        ));

        JPanel grid = new JPanel(new GridLayout(n + 1, 2, 10, 10));
        grid.setBackground(BACKGROUND_COLOR);

        // Header
        grid.add(createHeaderLabel("Kriteria"));
        grid.add(createHeaderLabel("Bobot Prioritas"));

        // Data rows
        for (int i = 0; i < n; i++) {
            JLabel label = new JLabel(kriteria[i] + ": ");
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5));
            
            JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setBackground(new Color(245, 245, 245));
            tf.setFont(new Font("Segoe UI", Font.BOLD, 12));
            tf.setForeground(PRIMARY_COLOR);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
            ));
            
            priorityFields[i] = tf;
            grid.add(label);
            grid.add(tf);
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void isiDiagonalAwal() {
        for (int i = 0; i < n; i++) {
            inputFields[i][i].setText("1.0");
            inputFields[i][i].setEditable(false);
            inputFields[i][i].setBackground(new Color(220, 220, 220));
        }
    }

    private void pasangListenerReciprocal() {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    final int row = i;
                    final int col = j;
                    inputFields[row][col].getDocument().addDocumentListener(new DocumentListener() {
                        public void insertUpdate(DocumentEvent e) { updateReciprocal(row, col); }
                        public void removeUpdate(DocumentEvent e) { updateReciprocal(row, col); }
                        public void changedUpdate(DocumentEvent e) { updateReciprocal(row, col); }
                    });
                }
            }
        }
    }

    private void updateReciprocal(int i, int j) {
        if (isUpdating) return;

        String val = inputFields[i][j].getText().trim();
        try {
            isUpdating = true;
            
            if (val.isEmpty()) {
                inputFields[j][i].setText("");
                return;
            }

            double num = parseLocalNumber(val); // Gunakan parser lokal
            
            if (num <= 0) {
                inputFields[j][i].setText("");
                return;
            }

            // Validasi skala AHP (1-9 atau 1/9-1/1)
            if ((num < 1 || num > 9) && (num <= 0.1111 || num > 1)) {
                JOptionPane.showMessageDialog(this,
                    "Nilai harus antara 1-9 atau 1/9-1",
                    "Nilai Tidak Valid", JOptionPane.WARNING_MESSAGE);
                inputFields[i][j].setText("");
                inputFields[j][i].setText("");
                return;
            }

            double reciprocal = 1.0 / num;
            inputFields[j][i].setText(formatLocalNumber(reciprocal)); // Format output
        } catch (NumberFormatException e) {
            inputFields[j][i].setText("");
        } finally {
            isUpdating = false;
        }
    }

    private void hitungAHP() {
        try {
            double[][] matrix = validateAndBuildMatrix();
            double[] priority = calculatePriorities(matrix);
            checkConsistency(matrix, priority);
            saveResults(priority);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error: " + e.getMessage() + "\nPastikan semua kolom terisi dengan angka 1-9.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saat proses: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double[][] validateAndBuildMatrix() throws NumberFormatException {
        double[][] matrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                String val = inputFields[i][j].getText().trim();
                if (val.isEmpty()) {
                    throw new NumberFormatException("Kolom " + kriteria[i] + " vs " + kriteria[j] + " kosong");
                }

                matrix[i][j] = parseLocalNumber(val); // Gunakan parser lokal

                // Validasi skala AHP
                if ((i != j) && 
                    ((matrix[i][j] < 1 || matrix[i][j] > 9) && 
                     (matrix[i][j] <= 0.1111 || matrix[i][j] > 1))) {
                    throw new NumberFormatException(
                        "Nilai " + kriteria[i] + " vs " + kriteria[j] + 
                        " harus antara 1-9 atau 1/9-1 (Ditemukan: " + val + ")"
                    );
                }
            }
        }
        return matrix;
    }

    private double[] calculatePriorities(double[][] matrix) {
        double[] colSums = new double[n];
        double[] priority = new double[n];
        List<Matrix> matrixList = new ArrayList<>();

        // Hitung jumlah kolom
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                colSums[j] += matrix[i][j];
            }
        }

        // Normalisasi matriks
        for (int i = 0; i < n; i++) {
            double sumRow = 0;
            for (int j = 0; j < n; j++) {
                double norm = matrix[i][j] / colSums[j];
                normFields[i][j].setText(formatLocalNumber(norm)); // Format output
                sumRow += norm;
                matrixList.add(new Matrix(kriteria[i], kriteria[j], norm));
            }
            priority[i] = sumRow / n;
            priorityFields[i].setText(formatLocalNumber(priority[i])); // Format output
        }

        matrixController.simpanMatrixNormalisasi(matrixList);
        return priority;
    }

    private void checkConsistency(double[][] matrix, double[] priority) {
        // Calculate lambda max
        double lambdaMax = 0;
        for (int i = 0; i < n; i++) {
            double sum = 0;
            for (int j = 0; j < n; j++) {
                sum += matrix[i][j] * priority[j];
            }
            lambdaMax += sum / priority[i];
        }
        lambdaMax /= n;

        // Calculate consistency
        double consistencyIndex = (lambdaMax - n) / (n - 1);
        double consistencyRatio = consistencyIndex / randomIndex[n];

        if (consistencyRatio > 0.1) {
            JOptionPane.showMessageDialog(this,
                "Peringatan: Rasio Konsistensi (CR) = " + String.format("%.4f", consistencyRatio) +
                "\nNilai CR > 0.1 menunjukkan ketidakkonsistenan dalam penilaian." +
                "\nPertimbangkan untuk meninjau kembali perbandingan yang Anda buat.",
                "Peringatan Konsistensi", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveResults(double[] priority) {
        boolean allUpdated = true;
        for (int i = 0; i < n; i++) {
            if (!kriteriaDAO.updateBobot(kriteria[i], priority[i])) {
                allUpdated = false;
                System.err.println("Gagal update bobot untuk kriteria: " + kriteria[i]);
            }
        }

        if (allUpdated) {
            JOptionPane.showMessageDialog(this, """
                                                Perhitungan AHP berhasil disimpan!
                                                Bobot prioritas telah diperbarui.""",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                "Perhitungan berhasil tetapi ada masalah saat menyimpan beberapa bobot.",
                "Partial Success", JOptionPane.WARNING_MESSAGE);
        }
    }

    private String formatLocalNumber(double value) {
        // Format angka dengan koma sebagai desimal dan 4 digit presisi
        return String.format("%.4f", value).replace(".", ",");
    }

    private double parseLocalNumber(String input) throws NumberFormatException {
        // Ganti koma dengan titik untuk parsing, lalu kembalikan sebagai double
        return Double.parseDouble(input.replace(",", "."));
    }
}