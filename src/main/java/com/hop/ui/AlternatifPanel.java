package com.hop.ui;

import com.hop.dao.AlternatifDAO;
import com.hop.dao.AlternatifDAOImpl;
import com.hop.model.Alternatif;

import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class AlternatifPanel extends JPanel {
    private DefaultTableModel model;
    private JTable table;
    private JTextField txtNik, txtNama, txtAlamat;
    private JComboBox<String> cmbJabatan;
    private JButton btnSimpan, btnUbah, btnHapus;
    private AlternatifDAO dao;
    private int selectedId = -1;

    public AlternatifPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        dao = new AlternatifDAOImpl();

        // Inisialisasi tabel
        model = new DefaultTableModel(new String[]{"ID", "NIK", "NAMA", "JABATAN", "ALAMAT"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
                txtNik.setText(model.getValueAt(row, 1).toString());
                txtNama.setText(model.getValueAt(row, 2).toString());
                cmbJabatan.setSelectedItem(model.getValueAt(row, 3).toString());
                txtAlamat.setText(model.getValueAt(row, 4).toString());
            }
        });

        // Inisialisasi form
        txtNik = new JTextField(20);
        txtNama = new JTextField(20);
        txtAlamat = new JTextField(20);
        cmbJabatan = new JComboBox<>(new String[]{
            "Manager Teknik","Manager Keuangan","Tenaga Ahli","Staf","Karyawan"
        });
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");

        // Form panel dengan GridBagLayout, rata kiri
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("NIK"), gbc);
        gbc.gridx = 1; formPanel.add(txtNik, gbc);

        gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Nama"), gbc);
        gbc.gridx = 1; formPanel.add(txtNama, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Jabatan"), gbc);
        gbc.gridx = 1; formPanel.add(cmbJabatan, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Alamat"), gbc);
        gbc.gridx = 1; formPanel.add(txtAlamat, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        JPanel tombolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        tombolPanel.setBackground(Color.WHITE);
        tombolPanel.add(btnSimpan);
        tombolPanel.add(btnUbah);
        tombolPanel.add(btnHapus);
        formPanel.add(tombolPanel, gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(formPanel, BorderLayout.WEST);
        add(wrapper, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load data awal
        loadTable();

        // Tombol Simpan
        btnSimpan.addActionListener((ActionEvent e) -> {
            String nik = txtNik.getText().trim(), nama = txtNama.getText().trim(),
                   jabatan = (String) cmbJabatan.getSelectedItem(),
                   alamat = txtAlamat.getText().trim();

            if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (dao instanceof AlternatifDAOImpl && ((AlternatifDAOImpl) dao).isNikExists(nik)) {
                JOptionPane.showMessageDialog(this, "NIK sudah digunakan!", "Gagal Simpan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Alternatif alt = new Alternatif(0, nik, nama, jabatan, alamat);
            if (dao.insert(alt)) {
                loadTable(); resetForm();
            }
        });

        // Tombol Ubah
        btnUbah.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String nik = txtNik.getText().trim(), nama = txtNama.getText().trim(),
                   jabatan = (String) cmbJabatan.getSelectedItem(),
                   alamat = txtAlamat.getText().trim();
            if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Semua field harus diisi!", "Validasi", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = selectedId;
            if (dao instanceof AlternatifDAOImpl &&
                ((AlternatifDAOImpl) dao).isNikExists(nik, id)) {
                JOptionPane.showMessageDialog(this, "NIK sudah digunakan!", "Gagal Ubah", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Alternatif alt = new Alternatif(id, nik, nama, jabatan, alamat);
            if (dao.update(alt)) {
                loadTable(); resetForm();
            }
        });

        // Tombol Hapus
        btnHapus.addActionListener(e -> {
            if (table.getSelectedRow() == -1) {
                JOptionPane.showMessageDialog(this, "Pilih data dulu!", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (dao.delete(selectedId)) {
                loadTable(); resetForm();
            }
        });
    }

    private void resetForm() {
        txtNik.setText(""); txtNama.setText("");
        txtAlamat.setText(""); cmbJabatan.setSelectedIndex(0);
        table.clearSelection(); selectedId = -1;
    }

    private void loadTable() {
        model.setRowCount(0);
        for (Alternatif alt : dao.getAll()) {
            model.addRow(new Object[]{ alt.getId(), alt.getNik(), alt.getNama(), alt.getJabatan(), alt.getAlamat() });
        }
    }
}
