package com.hop.ui;

import com.hop.dao.*;
import com.hop.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MatrixAlternatifPanel extends JPanel {

    private final MatrixAlternatifDAO matrixDAO = new MatrixAlternatifDAOImpl();
    private final AlternatifDAO alternatifDAO = new AlternatifDAOImpl();
    private final KriteriaDAO kriteriaDAO = new KriteriaDAOImpl();

    private JTable table;
    private DefaultTableModel model;

    private JComboBox<Alternatif> cbAlternatif;
    private JComboBox<Kriteria> cbKriteria;
    private JTextField tfNilai;
    private JButton btnSimpan, btnHapus;

    private JButton btnHitungSkor;
    private JTable tableHasil;
    private DefaultTableModel modelHasil;

    public MatrixAlternatifPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        initInputPanel();
        initMainTable();
        initHasilPanel();

        loadCombos();
        loadTable();
    }

    private void initInputPanel() {
        setLayout(new BorderLayout(10, 10));

        // Panel form input vertikal dengan GridBagLayout
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Masukkan Nilai Alternatif"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;  // Rata kiri

        // Baris 0 - Label Alternatif
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(new JLabel("Alternatif:"), gbc);
        // Combo Alternatif
        gbc.gridx = 1;
        form.add(cbAlternatif = new JComboBox<>(), gbc);

        // Baris 1 - Label Kriteria
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(new JLabel("Kriteria:"), gbc);
        // Combo Kriteria
        gbc.gridx = 1;
        form.add(cbKriteria = new JComboBox<>(), gbc);

        // Baris 2 - Label Nilai
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(new JLabel("Nilai:"), gbc);
        // TextField Nilai
        gbc.gridx = 1;
        form.add(tfNilai = new JTextField(8), gbc);

        // Baris 3 - Panel tombol, satu baris, rata kiri
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;  // menempati 2 kolom
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        btnSimpan = new JButton("Simpan");
        btnHapus = new JButton("Hapus");
        btnHitungSkor = new JButton("Hitung Skor Alternatif");
        panelButtons.add(btnSimpan);
        panelButtons.add(btnHapus);
        panelButtons.add(btnHitungSkor);
        form.add(panelButtons, gbc);

        add(form, BorderLayout.NORTH);

        // Inisialisasi event tombol
        btnSimpan.addActionListener(e -> save());
        btnHapus.addActionListener(e -> delete());
        btnHitungSkor.addActionListener(e -> hitungSkorAlternatif());

        // Setup table seperti sebelumnya...
        model = new DefaultTableModel(new Object[]{"ID", "Alternatif", "Kriteria", "Nilai"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Setup panel hasil perhitungan di bawah
        initHasilPanel();
    }



    private void initMainTable() {
        model = new DefaultTableModel(new Object[]{"ID", "Alternatif", "Kriteria", "Nilai"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Data Matrix Alternatif"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initHasilPanel() {
        JPanel hasilPanel = new JPanel(new BorderLayout(5,5));
        hasilPanel.setBorder(BorderFactory.createTitledBorder("Hasil Perhitungan Skor Alternatif"));

        btnHitungSkor = new JButton("Hitung Skor Alternatif");
        btnHitungSkor.addActionListener(e -> hitungSkorAlternatif());

        modelHasil = new DefaultTableModel(new Object[]{"Alternatif", "Skor"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableHasil = new JTable(modelHasil);
        tableHasil.setRowHeight(24);

        hasilPanel.add(btnHitungSkor, BorderLayout.NORTH);
        hasilPanel.add(new JScrollPane(tableHasil), BorderLayout.CENTER);

        add(hasilPanel, BorderLayout.SOUTH);
    }

    private void loadCombos() {
        cbAlternatif.removeAllItems();
        alternatifDAO.getAll().forEach(cbAlternatif::addItem);

        cbKriteria.removeAllItems();
        kriteriaDAO.getAll().forEach(cbKriteria::addItem);
    }

    private void loadTable() {
        model.setRowCount(0);
        List<MatrixAlternatif> list = matrixDAO.getAll();
        for (MatrixAlternatif m : list) {
            Alternatif a = alternatifDAO.getById(m.getIdAlternatif());
            Kriteria k = kriteriaDAO.getById(m.getIdKriteria());
            model.addRow(new Object[]{
                    m.getId(),
                    a != null ? a.getNama() : "–",
                    k != null ? k.getNamaKriteria() : "–",
                    m.getNilai()
            });
        }
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
            JOptionPane.showMessageDialog(this, "Isi semua field", "Warning", JOptionPane.WARNING_MESSAGE);
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
                    alt.getId(), kri.getIdKriteria(), val
            );

            boolean ok = existing != null ? matrixDAO.update(m) : matrixDAO.insert(m);
            JOptionPane.showMessageDialog(this,
                    ok ? "Tersimpan" : "Gagal simpan",
                    "Info",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nilai harus angka", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void delete() {
        int r = table.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih baris dulu", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Alternatif alt = (Alternatif) cbAlternatif.getSelectedItem();
        Kriteria kri = (Kriteria) cbKriteria.getSelectedItem();

        int yes = JOptionPane.showConfirmDialog(this, "Hapus nilai ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (yes == JOptionPane.YES_OPTION) {
            boolean ok = matrixDAO.delete(alt.getId(), kri.getIdKriteria());
            JOptionPane.showMessageDialog(this,
                    ok ? "Terhapus" : "Gagal hapus",
                    "Info",
                    ok ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            loadTable();
        }
    }

    private void hitungSkorAlternatif() {
        try {
            List<Kriteria> kriteriaList = kriteriaDAO.getAll();
            Map<Integer, Double> bobotKriteria = new HashMap<>();
            for (Kriteria k : kriteriaList) {
                bobotKriteria.put(k.getIdKriteria(), Math.max(0, k.getBobot()));
            }

            List<Alternatif> alternatifList = alternatifDAO.getAll();

            Map<Integer, List<MatrixAlternatif>> nilaiPerKriteria = new HashMap<>();
            for (Kriteria k : kriteriaList) {
                List<MatrixAlternatif> list = matrixDAO.getByKriteria(k.getIdKriteria());
                nilaiPerKriteria.put(k.getIdKriteria(), list != null ? list : Collections.emptyList());
            }

            Map<Integer, Double> jumlahPerKriteria = new HashMap<>();
            for (Kriteria k : kriteriaList) {
                double sum = nilaiPerKriteria.get(k.getIdKriteria()).stream()
                        .mapToDouble(MatrixAlternatif::getNilai)
                        .sum();
                jumlahPerKriteria.put(k.getIdKriteria(), sum);
            }

            Map<String, Double> skorAlternatif = new HashMap<>();
            for (Alternatif a : alternatifList) {
                double skor = 0.0;
                for (Kriteria k : kriteriaList) {
                    double nilai = nilaiPerKriteria.get(k.getIdKriteria()).stream()
                            .filter(m -> m.getIdAlternatif() == a.getId())
                            .map(MatrixAlternatif::getNilai)
                            .findFirst().orElse(0.0);

                    double sumKri = jumlahPerKriteria.get(k.getIdKriteria());
                    double norm = sumKri == 0 ? 0 : nilai / sumKri;
                    skor += norm * bobotKriteria.getOrDefault(k.getIdKriteria(), 0.0);
                }
                skorAlternatif.put(a.getNama(), skor);
            }

            modelHasil.setRowCount(0);
            skorAlternatif.entrySet().stream()
                    .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                    .forEach(entry -> modelHasil.addRow(new Object[]{entry.getKey(), String.format("%.4f", entry.getValue())}));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Gagal menghitung skor alternatif:\n" + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
