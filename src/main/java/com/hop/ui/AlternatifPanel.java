package com.hop.ui;

import com.hop.dao.AlternatifDAO;
import com.hop.dao.AlternatifDAOImpl;
import com.hop.model.Alternatif;
import com.hop.report.ReportUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class AlternatifPanel extends JPanel {
    private AlternatifDAO alternatifDAO = new AlternatifDAOImpl();
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextField txtNik, txtNama, txtAlamat;
    private final JComboBox<String> cmbJabatan;
    private final JButton btnSimpan, btnUbah, btnHapus, btnCetak;
    private final AlternatifDAO dao;
    private int selectedId = -1;

    public AlternatifPanel() {
        // Inisialisasi DAO
        dao = new AlternatifDAOImpl();

        // Setup panel utama
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Inisialisasi komponen UI
        model = new DefaultTableModel(new String[]{"ID", "NIK", "NAMA", "JABATAN", "ALAMAT"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
            
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };
        
        table = new JTable(model);
        customizeTable();
        
        txtNik = createTextField();
        txtNama = createTextField();
        txtAlamat = createTextField();
        cmbJabatan = createComboBox();
        
        btnSimpan = createButton("Simpan", new Color(76, 175, 80));
        btnUbah = createButton("Ubah", new Color(33, 150, 243));
        btnHapus = createButton("Hapus", new Color(244, 67, 54));
        btnCetak = createButton("Print", new Color(0, 150, 136));

        // Setup layout
        setupFormPanel();
        setupTablePanel();

        // Load data awal
        loadTableData();

        // Event handlers
        setupEventHandlers();
    }

    private void customizeTable() {
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        
        // Styling header tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(63, 81, 181));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Selection listener
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                selectedId = (Integer) model.getValueAt(row, 0);
                txtNik.setText(model.getValueAt(row, 1).toString());
                txtNama.setText(model.getValueAt(row, 2).toString());
                cmbJabatan.setSelectedItem(model.getValueAt(row, 3).toString());
                txtAlamat.setText(model.getValueAt(row, 4).toString());
            }
        });
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JComboBox<String> createComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(new String[]{
            "Manager Teknik", "Manager Keuangan", "Tenaga Ahli", "Staf", "Karyawan"
        });
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        return button;
    }

    private void setupFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Alternatif"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        addFormRow(formPanel, gbc, 0, "NIK:", txtNik);
        addFormRow(formPanel, gbc, 1, "Nama:", txtNama);
        addFormRow(formPanel, gbc, 2, "Jabatan:", cmbJabatan);
        addFormRow(formPanel, gbc, 3, "Alamat:", txtAlamat);

        // Panel tombol
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(btnSimpan);
        buttonPanel.add(btnUbah);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnCetak);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.NORTH);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent component) {
        gbc.gridx = 0;
        gbc.gridy = row;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        
        gbc.gridx = 1;
        panel.add(component, gbc);
    }

    private void setupTablePanel() {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Daftar Alternatif"));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        btnSimpan.addActionListener(this::handleSimpan);
        btnUbah.addActionListener(this::handleUbah);
        btnHapus.addActionListener(this::handleHapus);
        btnCetak.addActionListener(e->cetakLaporan());
    }

    private void handleSimpan(ActionEvent e) {
        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = (String) cmbJabatan.getSelectedItem();
        String alamat = txtAlamat.getText().trim();

        if (!validateInput(nik, nama, alamat)) {
            return;
        }

        if (((AlternatifDAOImpl) dao).isNikExists(nik)) {
            showErrorMessage("NIK sudah digunakan!", "Gagal Simpan");
            return;
        }

        Alternatif alt = new Alternatif(0, nik, nama, jabatan, alamat);
        if (dao.insert(alt)) {
            loadTableData();
            resetForm();
            showSuccessMessage("Data berhasil disimpan!");
        }
    }

    private void handleUbah(ActionEvent e) {
        if (table.getSelectedRow() == -1) {
            showInfoMessage("Pilih data terlebih dahulu!");
            return;
        }

        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = (String) cmbJabatan.getSelectedItem();
        String alamat = txtAlamat.getText().trim();

        if (!validateInput(nik, nama, alamat)) {
            return;
        }

        if (((AlternatifDAOImpl) dao).isNikExists(nik, selectedId)) {
            showErrorMessage("NIK sudah digunakan!", "Gagal Ubah");
            return;
        }

        Alternatif alt = new Alternatif(selectedId, nik, nama, jabatan, alamat);
        if (dao.update(alt)) {
            loadTableData();
            resetForm();
            showSuccessMessage("Data berhasil diubah!");
        }
    }

    private void handleHapus(ActionEvent e) {
        if (table.getSelectedRow() == -1) {
            showInfoMessage("Pilih data terlebih dahulu!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this, 
            "Apakah Anda yakin ingin menghapus data ini?", 
            "Konfirmasi", 
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION && dao.delete(selectedId)) {
            loadTableData();
            resetForm();
            showSuccessMessage("Data berhasil dihapus!");
        }
    }

    private boolean validateInput(String nik, String nama, String alamat) {
        if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            showWarningMessage("Semua field harus diisi!", "Validasi");
            return false;
        }
        return true;
    }

    private void resetForm() {
        txtNik.setText("");
        txtNama.setText("");
        txtAlamat.setText("");
        cmbJabatan.setSelectedIndex(0);
        table.clearSelection();
        selectedId = -1;
    }

    private void loadTableData() {
        model.setRowCount(0);
        try {
            List<Alternatif> alternatifList = dao.getAll();
            for (Alternatif alt : alternatifList) {
                model.addRow(new Object[]{
                    alt.getId(),
                    alt.getNik(),
                    alt.getNama(),
                    alt.getJabatan(),
                    alt.getAlamat()
                });
            }
        } catch (Exception e) {
            showErrorMessage("Gagal memuat data: " + e.getMessage(), "Error");
        }
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Sukses", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfoMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.WARNING_MESSAGE);
    }

    private void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
    
    public void cetakLaporan() {
        try {
            List<Alternatif> list = alternatifDAO.getAll();
            ReportUtil.generatePdfReport(
                list,
                new String[]{"ID", "NIK", "Nama", "Jabatan", "Alamat"},
                "Laporan Data Alternatif",
                "alternatif_report.pdf",
                "Bandung",
                "Gunawan"
            );
            JOptionPane.showMessageDialog(this, "Laporan berhasil dibuat.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal cetak laporan: " + e.getMessage());
        }
    }
}