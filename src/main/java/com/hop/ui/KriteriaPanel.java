package com.hop.ui;

import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.model.Kriteria;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class KriteriaPanel extends JPanel {
    // Warna modern
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SECONDARY_COLOR = new Color(233, 30, 99);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color TABLE_HEADER_COLOR = new Color(63, 81, 181);
    private static final Color TABLE_HEADER_TEXT_COLOR = Color.WHITE;
    
    private DefaultTableModel model;
    private JTable table;
    private JTextField tKode, tNama, tKet, tBobot;
    private JButton btnTambah, btnUpdate, btnHapus;
    private KriteriaDAO dao;
    private int selectedId = -1;

    public KriteriaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        dao = new KriteriaDAOImpl();
        
        // Panel utama dengan border dan shadow effect
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Buat tabel dengan styling modern
        createTable();
        
        // Buat form input dengan styling modern
        mainPanel.add(createFormPanel(), BorderLayout.NORTH);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        loadData();
    }

    private void createTable() {
        model = new DefaultTableModel(new String[]{"ID", "Kode", "Nama Kriteria", "Keterangan", "Bobot"}, 0) {
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
        
        // Styling tabel
        table.setRowHeight(32);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 220, 255));
        table.setSelectionForeground(Color.BLACK);
        
        // Styling header tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TABLE_HEADER_TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        
        // Renderer untuk tengahkan isi tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Warna baris bergantian
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }
                
                return c;
            }
        });

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
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Form Kriteria",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(80, 80, 80)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buat text field dengan styling modern
        tKode = createTextField("Contoh: C1");
        tNama = createTextField("Contoh: Kompetensi");
        tKet = createTextField("Contoh: Dinilai dari hasil pelatihan");
        tBobot = createTextField("Contoh: 0.25");

        // Row 0: Kode Kriteria
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(createFormLabel("Kode Kriteria:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tKode, gbc);

        // Row 1: Nama Kriteria
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(createFormLabel("Nama Kriteria:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tNama, gbc);

        // Row 2: Keterangan
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(createFormLabel("Keterangan:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tKet, gbc);

        // Row 3: Bobot
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        formPanel.add(createFormLabel("Bobot:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(tBobot, gbc);

        // Panel tombol
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        
        btnTambah = createStyledButton("Tambah", PRIMARY_COLOR);
        btnUpdate = createStyledButton("Update", new Color(255, 152, 0));
        btnHapus = createStyledButton("Hapus", SECONDARY_COLOR);

        btnTambah.addActionListener(e -> tambahKriteria());
        btnUpdate.addActionListener(e -> updateKriteria());
        btnHapus.addActionListener(e -> hapusKriteria());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnTambah);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnHapus);
        
        formPanel.add(btnPanel, gbc);

        return formPanel;
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        tf.putClientProperty("JTextField.placeholderText", placeholder);
        return tf;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(bgColor.darker(), 1, true),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
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
            showMessage("Kode, Nama, dan Bobot wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double bobot = Double.parseDouble(bobotText);
            dao.insert(new Kriteria(0, kode, nama, ket, bobot));
            loadData();
            showMessage("Data berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            showMessage("Bobot harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateKriteria() {
        if (selectedId == -1) {
            showMessage("Pilih data yang akan diupdate", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String kode = tKode.getText().trim();
        String nama = tNama.getText().trim();
        String ket = tKet.getText().trim();
        String bobotText = tBobot.getText().trim();

        if (kode.isEmpty() || nama.isEmpty() || bobotText.isEmpty()) {
            showMessage("Kode, Nama, dan Bobot wajib diisi!", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            double bobot = Double.parseDouble(bobotText);
            dao.update(new Kriteria(selectedId, kode, nama, ket, bobot));
            loadData();
            showMessage("Data berhasil diupdate!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            showMessage("Bobot harus berupa angka!", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void hapusKriteria() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Hapus data ini?", 
                "Konfirmasi", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                int id = (int) model.getValueAt(row, 0);
                dao.delete(id);
                loadData();
                showMessage("Data berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            showMessage("Pilih baris yang akan dihapus", "Info", JOptionPane.INFORMATION_MESSAGE);
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

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}