package com.hop.ui;

import com.hop.controller.MatrixController;
import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.dao.MatrixDAOImpl;
import com.hop.model.Matrix;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixPanel extends JPanel {

    private final int n = 4;
    private final String[] kriteria = {"Kompetensi", "Disiplin", "Tanggung Jawab", "Kerja Sama"};

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
        setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createMatrixPanel("Matrix Input AHP", inputFields, true));
        mainPanel.add(createMatrixPanel("Matrix Normalisasi", normFields, false));
        mainPanel.add(createPriorityPanel());

        JButton btnHitung = new JButton("Hitung");
        btnHitung.addActionListener(e -> hitungAHP());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnHitung);

        add(mainPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);

        isiDiagonalAwal();
        pasangListenerReciprocal();
    }

    private JPanel createMatrixPanel(String title, JTextField[][] fields, boolean editable) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(Color.WHITE);

        JPanel grid = new JPanel(new GridLayout(n + 1, n + 1, 5, 5));
        grid.setBackground(Color.WHITE);

        grid.add(new JLabel(""));
        for (int i = 0; i < n; i++) {
            JLabel label = new JLabel(kriteria[i], JLabel.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            grid.add(label);
        }

        for (int i = 0; i < n; i++) {
            JLabel rowLabel = new JLabel(kriteria[i], JLabel.CENTER);
            rowLabel.setFont(rowLabel.getFont().deriveFont(Font.BOLD));
            grid.add(rowLabel);

            for (int j = 0; j < n; j++) {
                JTextField tf = new JTextField();
                tf.setHorizontalAlignment(JTextField.CENTER);
                tf.setEditable(editable && i != j);
                tf.setBackground(editable ? Color.WHITE : new Color(240, 240, 240));
                fields[i][j] = tf;
                grid.add(tf);
            }
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPriorityPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Bobot Prioritas"));

        JPanel grid = new JPanel(new GridLayout(n, 2, 5, 5));
        grid.setBackground(Color.WHITE);

        for (int i = 0; i < n; i++) {
            JLabel label = new JLabel(kriteria[i] + ": ");
            JTextField tf = new JTextField();
            tf.setEditable(false);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setBackground(new Color(245, 245, 245));
            priorityFields[i] = tf;
            grid.add(label);
            grid.add(tf);
        }

        panel.add(grid, BorderLayout.CENTER);
        return panel;
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
                        public void insertUpdate(DocumentEvent e) {
                            updateReciprocal(row, col);
                        }

                        public void removeUpdate(DocumentEvent e) {
                            updateReciprocal(row, col);
                        }

                        public void changedUpdate(DocumentEvent e) {
                            updateReciprocal(row, col);
                        }
                    });
                }
            }
        }
    }

    private void updateReciprocal(int i, int j) {
        if (isUpdating) return;

        String val = inputFields[i][j].getText().trim();

        // Jika kosong, reset reciprocal dan return
        if (val.isEmpty()) {
            try {
                isUpdating = true;
                inputFields[j][i].setText("");
            } finally {
                isUpdating = false;
            }
            return;
        }

        try {
            double num = Double.parseDouble(val.replace(",", "."));
            if (num <= 0) {
                // Jika input <= 0, kosongkan reciprocal juga
                try {
                    isUpdating = true;
                    inputFields[j][i].setText("");
                } finally {
                    isUpdating = false;
                }
                return;
            }

            try {
                isUpdating = true;
                double reciprocal = 1.0 / num;
                inputFields[j][i].setText(String.format("%.4f", reciprocal));
            } finally {
                isUpdating = false;
            }
        } catch (NumberFormatException ignored) {
            try {
                isUpdating = true;
                inputFields[j][i].setText("");
            } finally {
                isUpdating = false;
            }
        }
    }

    private void hitungAHP() {
        double[][] matrix = new double[n][n];
        double[] colSums = new double[n];

        try {
            // Validasi dan isi matrix + hitung col sums
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    String val = inputFields[i][j].getText().trim();
                    if (val.isEmpty()) {
                        throw new NumberFormatException("Ada field kosong");
                    }
                    matrix[i][j] = Double.parseDouble(val.replace(",", "."));
                    if (matrix[i][j] <= 0) {
                        throw new NumberFormatException("Angka tidak valid");
                    }
                    colSums[j] += matrix[i][j];
                }
            }

            double[] priority = new double[n];
            List<Matrix> matrixList = new ArrayList<>();

            // Normalisasi dan hitung prioritas
            for (int i = 0; i < n; i++) {
                double sumRow = 0;
                for (int j = 0; j < n; j++) {
                    double norm = matrix[i][j] / colSums[j];
                    normFields[i][j].setText(String.format("%.4f", norm));
                    sumRow += norm;

                    matrixList.add(new Matrix(kriteria[i], kriteria[j], norm));
                }
                priority[i] = sumRow / n;
                priorityFields[i].setText(String.format("%.4f", priority[i]));
            }

            // Update bobot ke DB
            for (int i = 0; i < n; i++) {
                boolean updated = kriteriaDAO.updateBobot(kriteria[i], priority[i]);
                if (!updated) {
                    System.err.println("Gagal update bobot untuk kriteria: " + kriteria[i]);
                }
            }

            // Simpan matrix normalisasi ke DB via controller
            boolean saved = matrixController.simpanMatrixNormalisasi(matrixList);

            if (saved) {
                JOptionPane.showMessageDialog(this,
                        "Perhitungan AHP dan matrix normalisasi berhasil disimpan!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Gagal menyimpan matrix normalisasi ke database.",
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage() + "\nPastikan semua kolom terisi dengan angka positif dan valid.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this,
                    "Error saat proses: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
