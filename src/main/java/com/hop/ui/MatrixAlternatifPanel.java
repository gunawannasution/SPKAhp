package com.hop.ui;

import com.hop.dao.*;
import com.hop.model.*;
import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class MatrixAlternatifPanel extends JPanel {

    private final MatrixAlternatifDAO matrixDAO = new MatrixAlternatifDAOImpl();
    private final AlternatifDAO alternatifDAO = new AlternatifDAOImpl();
    private final KriteriaDAO kriteriaDAO = new KriteriaDAOImpl();

    private JTable table;
    private DefaultTableModel model;
    private JTable tableHasil;
    private DefaultTableModel modelHasil;

    private JComboBox<Alternatif> cbAlternatif;
    private JComboBox<Kriteria> cbKriteria;
    private JTextField tfNilai;
    private JButton btnSimpan, btnHapus, btnHitungSkor;

    public MatrixAlternatifPanel() {
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Main panel with shadow effect
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mainPanel.setBackground(Color.WHITE);

        // Input panel
        JPanel inputPanel = createInputPanel();
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // Table panel
        JPanel tablePanel = createTablePanel();
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        // Results panel
        JPanel resultsPanel = createResultsPanel();
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new MigLayout("wrap 3, insets 20, gap 20", "[][grow,fill][]", "[]10[]10[]"));
        panel.setBorder(BorderFactory.createTitledBorder("Input Nilai Alternatif"));
        panel.setBackground(Color.WHITE);

        // Alternatif combo
        cbAlternatif = new JComboBox<>();
        cbAlternatif.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Alternatif) {
                    setText(((Alternatif) value).getNama());
                }
                return this;
            }
        });

        // Kriteria combo
        cbKriteria = new JComboBox<>();
        cbKriteria.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Kriteria) {
                    setText(((Kriteria) value).getNamaKriteria());
                }
                return this;
            }
        });

        tfNilai = new JTextField();
        tfNilai.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        // Buttons
        btnSimpan = new JButton("Simpan");
        btnSimpan.addActionListener(e -> save());

        btnHapus = new JButton("Hapus");
        btnHapus.addActionListener(e -> delete());

        btnHitungSkor = new JButton("Hitung Skor");
        btnHitungSkor.addActionListener(e -> hitungSkorAlternatif());

        // Add components
        panel.add(new JLabel("Alternatif:"));
        panel.add(cbAlternatif, "growx");
        panel.add(new JLabel());
        
        panel.add(new JLabel("Kriteria:"));
        panel.add(cbKriteria, "growx");
        panel.add(new JLabel());
        
        panel.add(new JLabel("Nilai:"));
        panel.add(tfNilai, "growx");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnHitungSkor);
        
        panel.add(buttonPanel, "span 3, growx");

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Data Matrix Alternatif"));

        model = new DefaultTableModel(new Object[]{"ID", "Alternatif", "Kriteria", "Nilai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Hasil Perhitungan Skor Alternatif"));

        modelHasil = new DefaultTableModel(new Object[]{"Peringkat", "Alternatif", "Skor", "Rekomendasi"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableHasil = new JTable(modelHasil);
        tableHasil.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(tableHasil);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void loadData() {
        loadCombos();
        loadTable();
    }

    private void loadCombos() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<Alternatif> alternatifs = alternatifDAO.getAll();
                List<Kriteria> kriterias = kriteriaDAO.getAll();
                
                SwingUtilities.invokeLater(() -> {
                    cbAlternatif.removeAllItems();
                    alternatifs.forEach(cbAlternatif::addItem);
                    
                    cbKriteria.removeAllItems();
                    kriterias.forEach(cbKriteria::addItem);
                });
                return null;
            }
        };
        worker.execute();
    }

    private void loadTable() {
        SwingWorker<List<MatrixAlternatif>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<MatrixAlternatif> doInBackground() throws Exception {
                return matrixDAO.getAll();
            }
            
            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<MatrixAlternatif> list = get();
                    
                    for (MatrixAlternatif m : list) {
                        Alternatif a = alternatifDAO.getById(m.getIdAlternatif());
                        Kriteria k = kriteriaDAO.getById(m.getIdKriteria());
                        model.addRow(new Object[]{
                            m.getId(),
                            a != null ? a.getNama() : "N/A",
                            k != null ? k.getNamaKriteria() : "N/A",
                            String.format("%.2f", m.getNilai())
                        });
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel.this,
                            "Gagal memuat data: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void fillFormFromTable() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        String altName = model.getValueAt(r, 1).toString();
        String kriName = model.getValueAt(r, 2).toString();
        String nilai = model.getValueAt(r, 3).toString();

        selectCombo(cbAlternatif, altName, Alternatif::getNama);
        selectCombo(cbKriteria, kriName, Kriteria::getNamaKriteria);
        tfNilai.setText(nilai);
    }

    private <T> void selectCombo(JComboBox<T> combo, String value, java.util.function.Function<T, String> mapper) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (mapper.apply(combo.getItemAt(i)).equals(value)) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }

    private void save() {
        Alternatif alt = (Alternatif) cbAlternatif.getSelectedItem();
        Kriteria kri = (Kriteria) cbKriteria.getSelectedItem();
        String sn = tfNilai.getText().trim();

        if (alt == null || kri == null || sn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Isi semua field", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double val = Double.parseDouble(sn);
            if (val < 0) {
                JOptionPane.showMessageDialog(this, "Nilai harus positif", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MatrixAlternatif existing = matrixDAO.getByAlternatifAndKriteria(alt.getId(), kri.getIdKriteria());
            MatrixAlternatif m = new MatrixAlternatif(
                    existing != null ? existing.getId() : 0,
                    alt.getId(), 
                    kri.getIdKriteria(), 
                    val
            );

            boolean ok = existing != null ? matrixDAO.update(m) : matrixDAO.insert(m);
            JOptionPane.showMessageDialog(this,
                    ok ? "Data berhasil disimpan" : "Gagal menyimpan data",
                    "Info",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (ok) {
                loadTable();
                clearForm();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nilai harus berupa angka", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih data yang akan dihapus", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int idAlt = (int) model.getValueAt(r, 1); // Get alternatif ID from selected row
        int idKri = (int) model.getValueAt(r, 2); // Get kriteria ID from selected row

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus data ini?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = matrixDAO.delete(idAlt, idKri);
            JOptionPane.showMessageDialog(this,
                ok ? "Data berhasil dihapus" : "Gagal menghapus data",
                "Info",
                ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            
            if (ok) {
                loadTable();
                clearForm();
            }
        }
    }

    private void hitungSkorAlternatif() {
        SwingWorker<Map<String, Double>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<String, Double> doInBackground() throws Exception {
                List<Kriteria> kriteriaList = kriteriaDAO.getAll();
                List<Alternatif> alternatifList = alternatifDAO.getAll();
                List<MatrixAlternatif> allMatrixValues = matrixDAO.getAll();

                // Group by kriteria
                Map<Integer, List<MatrixAlternatif>> nilaiPerKriteria = allMatrixValues.stream()
                    .collect(Collectors.groupingBy(MatrixAlternatif::getIdKriteria));

                // Calculate sums per criteria
                Map<Integer, Double> jumlahPerKriteria = nilaiPerKriteria.entrySet().stream()
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(MatrixAlternatif::getNilai).sum()
                    ));

                // Calculate scores
                Map<String, Double> skorAlternatif = new HashMap<>();
                for (Alternatif a : alternatifList) {
                    double skor = 0.0;
                    for (Kriteria k : kriteriaList) {
                        double nilai = nilaiPerKriteria.getOrDefault(k.getIdKriteria(), Collections.emptyList()).stream()
                                .filter(m -> m.getIdAlternatif() == a.getId())
                                .mapToDouble(MatrixAlternatif::getNilai)
                                .findFirst().orElse(0.0);

                        double sumKri = jumlahPerKriteria.getOrDefault(k.getIdKriteria(), 1.0);
                        double norm = sumKri == 0 ? 0 : nilai / sumKri;
                        skor += norm * k.getBobot();
                    }
                    skorAlternatif.put(a.getNama(), skor);
                }

                return skorAlternatif;
            }
            
            @Override
            protected void done() {
                try {
                    Map<String, Double> skorAlternatif = get();
                    modelHasil.setRowCount(0);
                    
                    // Add ranking and recommendation
                    int rank = 1;
                    List<Map.Entry<String, Double>> sorted = skorAlternatif.entrySet().stream()
                            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                            .collect(Collectors.toList());
                    
                    for (Map.Entry<String, Double> entry : sorted) {
                        String recommendation = rank <= 3 ? "Direkomendasikan" : "-";
                        modelHasil.addRow(new Object[]{
                            rank,
                            entry.getKey(),
                            String.format("%.4f", entry.getValue()),
                            recommendation
                        });
                        rank++;
                    }
                    
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel.this,
                        "Perhitungan skor alternatif berhasil", 
                        "Sukses", 
                        JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel.this,
                        "Gagal menghitung skor alternatif:\n" + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void clearForm() {
        tfNilai.setText("");
        table.clearSelection();
    }
}