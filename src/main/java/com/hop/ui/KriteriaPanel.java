package com.hop.ui;

import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.model.Kriteria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KriteriaPanel extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JTextField tKode;
    private JTextField tNama;
    private JTextField tKet;
    private JTextField tBobot;
    private JButton btnTambah;
    private JButton btnUpdate;
    private JButton btnHapus;
    private KriteriaDAO dao;

    private int selectedId = -1;

    public KriteriaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        dao = new KriteriaDAOImpl();
        //buat Table
        model = new DefaultTableModel(new String[]{"ID", "Kode", "Nama Kriteria", "Keterangan", "Bobot"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
                tKode.setText(model.getValueAt(row, 1).toString());
                tNama.setText(model.getValueAt(row, 2).toString());
                Object keterangan = model.getValueAt(row, 3);
                tKet.setText(keterangan != null ? keterangan.toString() : "");
                Object bobot = model.getValueAt(row, 4);
                tBobot.setText(bobot != null ? bobot.toString() : "");
            }
        });

        // ==== FORM ====
        tKode = new JTextField();
        tNama = new JTextField();
        tKet = new JTextField();
        tBobot = new JTextField();

        tKode.putClientProperty("JTextField.placeholderText", "Contoh: C1");
        tNama.putClientProperty("JTextField.placeholderText", "Contoh: Kompetensi");
        tKet.putClientProperty("JTextField.placeholderText", "Contoh: Dinilai dari hasil pelatihan");
        tBobot.putClientProperty("JTextField.placeholderText", "Contoh: 0.25");

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Form Kriteria"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Kode Kriteria
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Kode Kriteria:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tKode, gbc);

        // Row 1: Nama Kriteria
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Nama Kriteria:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tNama, gbc);

        // Row 2: Keterangan
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Keterangan:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tKet, gbc);

        // Row 3: Bobot
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Bobot:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tBobot, gbc);

        // ==== BUTTON ====
        btnTambah = new JButton("Tambah");
        btnUpdate = new JButton("Update");
        btnHapus = new JButton("Hapus");

        btnTambah.addActionListener(e -> tambahKriteria());
        btnUpdate.addActionListener(e -> updateKriteria());
        btnHapus.addActionListener(e -> hapusKriteria());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);

        // ==== PANEL ATAS ====
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        loadData();
    }
    private void loadData() {
        model.setRowCount(0);
        List<Kriteria> list = dao.getAll();
        for (Kriteria k : list) {
            model.addRow(new Object[]{
                k.getIdKriteria(),
                k.getKodeKriteria(),
                k.getNamaKriteria(),
                k.getKetKriteria(),
                k.getBobot()
            });
        }
        clearForm();
    }
    private void tambahKriteria() {
        String kode = tKode.getText().trim();
        String nama = tNama.getText().trim();
        String ket = tKet.getText().trim();
        String bobotText = tBobot.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || bobotText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Kode, Nama, dan Bobot wajib diisi!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double bobot = Double.parseDouble(bobotText);
            dao.insert(new Kriteria(0, kode, nama, ket, bobot));
            loadData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Bobot harus berupa angka!", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateKriteria() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, 
                "Pilih data yang akan diupdate", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String kode = tKode.getText().trim();
        String nama = tNama.getText().trim();
        String ket = tKet.getText().trim();
        String bobotText = tBobot.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || bobotText.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Kode, Nama, dan Bobot wajib diisi!", 
                "Peringatan", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double bobot = Double.parseDouble(bobotText);
            dao.update(new Kriteria(selectedId, kode, nama, ket, bobot));
            loadData();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                "Bobot harus berupa angka!", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusKriteria() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Hapus data ini?", 
                "Konfirmasi", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                dao.delete(id);
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Pilih baris yang akan dihapus", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        selectedId = -1;
        tKode.setText("");
        tNama.setText("");
        tKet.setText("");
        tBobot.setText("");
        table.clearSelection();
    }
}