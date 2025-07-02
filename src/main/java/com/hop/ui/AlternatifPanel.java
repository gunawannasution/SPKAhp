package com.hop.ui;

import com.hop.dao.AlternatifDAO;
import com.hop.dao.AlternatifDAOImpl;
import com.hop.model.Alternatif;
import com.hop.report.ReportUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Panel untuk mengelola data Alternatif (pegawai)
 */
public class AlternatifPanel extends JPanel {

    // === Komponen DAO ===
    private final AlternatifDAO dao = new AlternatifDAOImpl();

    // === Komponen UI ===
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtNik, txtNama, txtAlamat;
    private final JComboBox<String> cmbJabatan;
    private final JButton btnSimpan, btnUbah, btnHapus, btnCetak;

    // variable bantu saat table di 
    private int selectedId = -1;

    public AlternatifPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // membuat komponen table
        model = new DefaultTableModel(new String[]{"ID", "NIK", "NAMA", "JABATAN", "ALAMAT"}, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
            public Class<?> getColumnClass(int column) { return column == 0 ? Integer.class : String.class; }
        };

        table = new JTable(model);
        aturTabel();

        txtNik = buatTextField();
        txtNama = buatTextField();
        txtAlamat = buatTextField();
        cmbJabatan = buatComboBox();

        btnSimpan = buatButton("Simpan", new Color(76, 175, 80));
        btnUbah = buatButton("Ubah", new Color(33, 150, 243));
        btnHapus = buatButton("Hapus", new Color(244, 67, 54));
        btnCetak = buatButton("Cetak", new Color(0, 150, 136));

        // === Tambah ke UI ===
        add(buatFormPanel(), BorderLayout.NORTH);
        add(buatTablePanel(), BorderLayout.CENTER);

        // === Event Tombol ===
        aturEvent();

        // === Load Data Awal ===
        muatData();
    }

    // -----------------------------------
    //  Bagian tampilan 
    // -----------------------------------

    private JTextField buatTextField() {
        JTextField tf = new JTextField(20);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JComboBox<String> buatComboBox() {
        JComboBox<String> cb = new JComboBox<>(new String[]{"Manager Teknik", "Manager Keuangan", "Tenaga Ahli", "Staf", "Karyawan"});
        cb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cb.setBackground(Color.WHITE);
        return cb;
    }

    private JButton buatButton(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        return btn;
    }

    private JPanel buatFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Form Alternatif"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Buat label dan field form dengan label rata kiri & lebar seragam
        tambahFormRow(panel, gbc, 0, "NIK:", txtNik);
        tambahFormRow(panel, gbc, 1, "Nama:", txtNama);
        tambahFormRow(panel, gbc, 2, "Jabatan:", cmbJabatan);
        tambahFormRow(panel, gbc, 3, "Alamat:", txtAlamat);

        // Panel tombol
        JPanel panelBtn = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelBtn.setBackground(Color.WHITE);
        panelBtn.add(btnSimpan);
        panelBtn.add(btnUbah);
        panelBtn.add(btnHapus);
        panelBtn.add(btnCetak);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(panelBtn, gbc);

        return panel;  // Pastikan RETURN panel di sini, bukan memanggil buatFormPanel lagi
    }

    // Tambah baris form dengan label & komponen input
    private void tambahFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        // Label
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(4, 4, 4, 4); // lebih rapat
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(60, 20)); // kecilkan jika perlu
        panel.add(lbl, gbc);

        // Komponen input
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(comp, gbc);
    }


    private JScrollPane buatTablePanel() {
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Daftar Alternatif"));
        return scroll;
    }

    private void aturTabel() {
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(63, 81, 181));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.convertRowIndexToModel(table.getSelectedRow());
                selectedId = (int) model.getValueAt(row, 0);
                txtNik.setText(model.getValueAt(row, 1).toString());
                txtNama.setText(model.getValueAt(row, 2).toString());
                cmbJabatan.setSelectedItem(model.getValueAt(row, 3).toString());
                txtAlamat.setText(model.getValueAt(row, 4).toString());
            }
        });
    }

    // -----------------------------------
    // 🔹 Event Handler
    // -----------------------------------

    private void aturEvent() {
        btnSimpan.addActionListener(this::simpanData);
        btnUbah.addActionListener(this::ubahData);
        btnHapus.addActionListener(this::hapusData);
        btnCetak.addActionListener(e -> cetakLaporan());
    }

    private void simpanData(ActionEvent e) {
        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = (String) cmbJabatan.getSelectedItem();
        String alamat = txtAlamat.getText().trim();

        if (!validasi(nik, nama, alamat)) return;

        if (((AlternatifDAOImpl) dao).isNikExists(nik)) {
            tampilError("NIK sudah digunakan!", "Gagal Simpan");
            return;
        }

        Alternatif alt = new Alternatif(0, nik, nama, jabatan, alamat);
        if (dao.insert(alt)) {
            muatData();
            resetForm();
            tampilInfo("Data berhasil disimpan.");
        }
    }

    private void ubahData(ActionEvent e) {
        if (selectedId == -1) {
            tampilInfo("Pilih data terlebih dahulu.");
            return;
        }

        String nik = txtNik.getText().trim();
        String nama = txtNama.getText().trim();
        String jabatan = (String) cmbJabatan.getSelectedItem();
        String alamat = txtAlamat.getText().trim();

        if (!validasi(nik, nama, alamat)) return;

        if (((AlternatifDAOImpl) dao).isNikExists(nik, selectedId)) {
            tampilError("NIK sudah digunakan!", "Gagal Ubah");
            return;
        }

        Alternatif alt = new Alternatif(selectedId, nik, nama, jabatan, alamat);
        if (dao.update(alt)) {
            muatData();
            resetForm();
            tampilInfo("Data berhasil diubah.");
        }
    }

    private void hapusData(ActionEvent e) {
        if (selectedId == -1) {
            tampilInfo("Pilih data yang ingin dihapus.");
            return;
        }

        int konfirmasi = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (konfirmasi == JOptionPane.YES_OPTION) {
            if (dao.delete(selectedId)) {
                muatData();
                resetForm();
                tampilInfo("Data berhasil dihapus.");
            }
        }
    }

    private boolean validasi(String nik, String nama, String alamat) {
        if (nik.isEmpty() || nama.isEmpty() || alamat.isEmpty()) {
            tampilPeringatan("Semua field harus diisi!", "Validasi");
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

    private void muatData() {
        model.setRowCount(0);
        try {
            List<Alternatif> list = dao.getAll();
            for (Alternatif alt : list) {
                model.addRow(new Object[]{
                        alt.getId(), alt.getNik(), alt.getNama(), alt.getJabatan(), alt.getAlamat()
                });
            }
        } catch (Exception e) {
            tampilError("Gagal memuat data: " + e.getMessage(), "Error");
        }
    }

    // -----------------------------------
    // 🔹 Cetak Laporan
    // -----------------------------------

    public void cetakLaporan() {
        try {
            List<Alternatif> list = dao.getAll();
            ReportUtil.generatePdfReport(
                    list,
                    new String[]{"ID", "NIK", "Nama", "Jabatan", "Alamat"},
                    "Laporan Alternatif",
                    "alternatif_report.pdf",
                    "Bandung",
                    "Gunawan"
            );
            tampilInfo("Laporan berhasil dibuat.");
        } catch (Exception e) {
            tampilError("Gagal mencetak laporan: " + e.getMessage(), "Gagal Cetak");
        }
    }

    // -----------------------------------
    // 🔹 Utility Message
    // -----------------------------------

    private void tampilInfo(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Informasi", JOptionPane.INFORMATION_MESSAGE);
    }

    private void tampilPeringatan(String pesan, String judul) {
        JOptionPane.showMessageDialog(this, pesan, judul, JOptionPane.WARNING_MESSAGE);
    }

    private void tampilError(String pesan, String judul) {
        JOptionPane.showMessageDialog(this, pesan, judul, JOptionPane.ERROR_MESSAGE);
    }
}
