package com.hop.ui;

import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.model.Kriteria;
import com.hop.report.ReportUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

/**
 * Panel untuk mengelola data kriteria (tambah, edit, hapus, cetak)
 */
public class KriteriaPanel extends JPanel {

    // === Warna yang digunakan di panel ===
    private final Color WARNA_UTAMA = new Color(63, 81, 181);
    private final Color WARNA_HAPUS = new Color(233, 30, 99);
    private final Color WARNA_CETAK = new Color(0, 150, 136);
    private final Color LATAR_BELAKANG = new Color(250, 250, 250);
    
    // === Komponen utama ===
    private JTable table;
    private DefaultTableModel model;
    private JTextField tKode, tNama, tKet, tBobot;
    private JButton btnTambah, btnUpdate, btnHapus, btnCetak;

    // DAO (data akses)
    private KriteriaDAO dao = new KriteriaDAOImpl();

    // ID baris yang sedang dipilih
    private int selectedId = -1;

    public KriteriaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(LATAR_BELAKANG);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel utama
        JPanel panelUtama = new JPanel(new BorderLayout(10, 10));
        panelUtama.setBackground(LATAR_BELAKANG);
        panelUtama.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
            new EmptyBorder(10, 10, 10, 10)
        ));

        // Buat dan tambahkan komponen
        buatTabel();
        panelUtama.add(buatForm(), BorderLayout.NORTH);
        panelUtama.add(new JScrollPane(table), BorderLayout.CENTER);

        add(panelUtama, BorderLayout.CENTER);
        muatData();
    }

    /**
     * Buat tabel dan atur tampilannya
     */
    private void buatTabel() {
        model = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Keterangan", "Bobot"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setSelectionBackground(new Color(220, 230, 255));

        // Header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(WARNA_UTAMA);
        header.setForeground(Color.WHITE);

        // Tengah semua isi kolom
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Baris selang-seling warna
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        });

        // Event saat memilih baris
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                selectedId = (int) model.getValueAt(row, 0);
                tKode.setText(model.getValueAt(row, 1).toString());
                tNama.setText(model.getValueAt(row, 2).toString());
                tKet.setText(model.getValueAt(row, 3).toString());
                tBobot.setText(model.getValueAt(row, 4).toString());
            }
        });
    }

    /**
     * Buat form input data kriteria + tombol
     */
    private JPanel buatForm() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Form Kriteria"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Text field
        tKode = buatTextField("Contoh: C1");
        tNama = buatTextField("Contoh: Kompetensi");
        tKet = buatTextField("Contoh: Berdasarkan pelatihan");
        tBobot = buatTextField("Contoh: 0.25");

        // Label & input
        tambahFormItem(panel, gbc, 0, "Kode:", tKode);
        tambahFormItem(panel, gbc, 1, "Nama:", tNama);
        tambahFormItem(panel, gbc, 2, "Keterangan:", tKet);
        tambahFormItem(panel, gbc, 3, "Bobot:", tBobot);

        // Tombol-tombol aksi
        btnTambah = buatButton("Tambah", WARNA_UTAMA, e -> tambah());
        btnUpdate = buatButton("Update", new Color(255, 152, 0), e -> update());
        btnHapus = buatButton("Hapus", WARNA_HAPUS, e -> hapus());
        btnCetak = buatButton("Cetak", WARNA_CETAK, e -> cetak());

        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBtn.setBackground(Color.WHITE);
        panelBtn.add(btnTambah);
        panelBtn.add(btnUpdate);
        panelBtn.add(btnHapus);
        panelBtn.add(btnCetak);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(panelBtn, gbc);

        return panel;
    }

    private void tambahFormItem(JPanel panel, GridBagConstraints gbc, int row, String label, JTextField field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private JTextField buatTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.putClientProperty("JTextField.placeholderText", placeholder); // Support dari LookAndFeel tertentu
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Color.LIGHT_GRAY),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return tf;
    }

    private JButton buatButton(String teks, Color warna, java.awt.event.ActionListener action) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.addActionListener(action);
        return btn;
    }

    /**
     * Load ulang data dari database
     */
    private void muatData() {
        model.setRowCount(0);
        for (Kriteria k : dao.getAll()) {
            model.addRow(new Object[]{k.getIdKriteria(), k.getKodeKriteria(), k.getNamaKriteria(), k.getKetKriteria(), k.getBobot()});
        }
        kosongkanForm();
    }

    private void tambah() {
        if (!validasiInput()) return;

        try {
            dao.insert(new Kriteria(0, tKode.getText(), tNama.getText(), tKet.getText(), Double.parseDouble(tBobot.getText())));
            muatData();
            info("Data berhasil ditambahkan!");
        } catch (NumberFormatException e) {
            error("Bobot harus berupa angka desimal!");
        }
    }

    private void update() {
        if (selectedId == -1) {
            info("Pilih data yang ingin diubah terlebih dahulu.");
            return;
        }

        if (!validasiInput()) return;

        try {
            dao.update(new Kriteria(selectedId, tKode.getText(), tNama.getText(), tKet.getText(), Double.parseDouble(tBobot.getText())));
            muatData();
            info("Data berhasil diupdate!");
        } catch (NumberFormatException e) {
            error("Bobot harus berupa angka desimal!");
        }
    }

    private void hapus() {
        if (selectedId == -1) {
            info("Pilih data yang ingin dihapus.");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            dao.delete(selectedId);
            muatData();
            info("Data berhasil dihapus.");
        }
    }

    private void cetak() {
        try {
            List<Kriteria> list = dao.getAll();
            ReportUtil.generatePdfReport(
                list,
                new String[]{"ID", "Kode", "Nama Kriteria", "Keterangan", "Bobot"},
                "Laporan Kriteria",
                "kriteria_report.pdf",
                "Jakarta",
                "IR. JANNUS SIMANJUNTAK"
            );
            info("Laporan berhasil dibuat.");
        } catch (Exception e) {
            error("Gagal membuat laporan: " + e.getMessage());
        }
    }

    private boolean validasiInput() {
        if (tKode.getText().trim().isEmpty() || tNama.getText().trim().isEmpty() || tBobot.getText().trim().isEmpty()) {
            error("Kode, Nama, dan Bobot wajib diisi.");
            return false;
        }
        return true;
    }

    private void kosongkanForm() {
        selectedId = -1;
        tKode.setText(""); tNama.setText(""); tKet.setText(""); tBobot.setText("");
        table.clearSelection();
    }

    private void info(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void error(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Kesalahan", JOptionPane.ERROR_MESSAGE);
    }
}
