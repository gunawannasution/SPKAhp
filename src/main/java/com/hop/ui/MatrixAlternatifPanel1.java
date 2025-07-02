package com.hop.ui;

import com.hop.dao.*;
import com.hop.model.*;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class MatrixAlternatifPanel1 extends JPanel {
    // Warna modern
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private static final Color SECONDARY_COLOR = new Color(233, 30, 99);
    private static final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private static final Color TABLE_HEADER_COLOR = new Color(63, 81, 181);
    private static final Color TABLE_HEADER_TEXT_COLOR = Color.WHITE;
    
    private final HasilAlternatifDAO hasilDAO = new HasilAlternatifDAOImpl();
    private final MatrixAlternatifDAO matrixDAO = new MatrixAlternatifDAOImpl();
    private final AlternatifDAO alternatifDAO = new AlternatifDAOImpl();
    private final KriteriaDAO kriteriaDAO = new KriteriaDAOImpl();

    private JTable table, tableHasil;
    private DefaultTableModel model, modelHasil;
    private JComboBox<Alternatif> cbAlternatif;
    private List<JTextField> kriteriaFields = new ArrayList<>();
    private List<Kriteria> allKriteria = new ArrayList<>();
    private JButton btnSimpan, btnHapus, btnHitungSkor;

    public MatrixAlternatifPanel1() {
        initUI();
        initListeners();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        mainPanel.add(createInputPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createResultsPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    private void initListeners() {
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                refreshAlternatifCombo();
            }
            @Override
            public void ancestorRemoved(AncestorEvent event) {}
            @Override
            public void ancestorMoved(AncestorEvent event) {}
        });

        // Tambahkan ActionListener untuk tombol-tombol
        btnSimpan.addActionListener(e -> save());
        btnHapus.addActionListener(e -> delete());
        btnHitungSkor.addActionListener(e -> hitungSkorAlternatif());
    }
    private JPanel createInputPanel() {
        allKriteria = kriteriaDAO.getAll();
        int kolom = allKriteria.size();

        // Gunakan ukuran kolom yang sama untuk semua field
        StringBuilder colConstraints = new StringBuilder("[]10");
        for (int i = 0; i < kolom; i++) {
            colConstraints.append("[150::,fill]"); // Set fixed minimum width of 150px for each criteria column
        }

        JPanel panel = new JPanel(new MigLayout("wrap " + (kolom + 1), colConstraints.toString(), "[]10[]10[]10[]"));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Input Nilai Alternatif",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(80, 80, 80)));

        // ComboBox Alternatif
        cbAlternatif = new JComboBox<>();
        cbAlternatif.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int ix, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, ix, s, f);
                if (v instanceof Alternatif alternatif) setText(alternatif.getNama());
                return this;
            }
        });
        cbAlternatif.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbAlternatif.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblAlternatif = new JLabel("Alternatif:");
        lblAlternatif.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblAlternatif, "cell 0 0");
        panel.add(cbAlternatif, "cell 1 0, span " + kolom + ", growx");

        // Label nama kriteria
        for (int i = 0; i < kolom; i++) {
            JLabel lbl = new JLabel(allKriteria.get(i).getNamaKriteria());
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(lbl, "cell " + (i + 1) + " 1, center");
        }

        // Input field untuk nilai tiap kriteria dengan ukuran yang sama
        kriteriaFields.clear();
        Dimension fieldSize = new Dimension(150, 30); // Ukuran tetap untuk semua textfield
        for (int i = 0; i < kolom; i++) {
            JTextField tf = new JTextField();
            tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setPreferredSize(fieldSize);
            tf.setMinimumSize(fieldSize);
            panel.add(tf, "cell " + (i + 1) + " 2, growx");
            kriteriaFields.add(tf);
        }

        // Tombol-tombol
        btnSimpan = createStyledButton("Simpan", PRIMARY_COLOR);
        btnHapus = createStyledButton("Hapus", SECONDARY_COLOR);
        btnHitungSkor = createStyledButton("Hitung Skor", new Color(76, 175, 80));

        JPanel bp = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        bp.setBackground(Color.WHITE);
        bp.add(btnSimpan);
        bp.add(btnHapus);
        bp.add(btnHitungSkor);

        panel.add(bp, "cell 0 3, span " + (kolom + 1) + ", growx");

        return panel;
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
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "Data Matrix Alternatif",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(80, 80, 80)));

        Vector<String> cols = new Vector<>();
        cols.add("ID");
        cols.add("Alternatif");
        allKriteria.forEach(k -> cols.add(k.getNamaKriteria()));

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        customizeTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromTable());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)), 
            "HASIL PERHITUNGAN SKOR ALTERNATIF",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            new Color(80, 80, 80)));

        modelHasil = new DefaultTableModel(new Object[]{"Peringkat", "Alternatif", "Skor", "Rekomendasi"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
            @Override
            public Class<?> getColumnClass(int column) {
                switch(column) {
                    case 0: return Integer.class;  // Peringkat
                    case 1: return String.class;   // Alternatif
                    case 2: return Double.class;   // Skor
                    case 3: return String.class;   // Rekomendasi
                    default: return Object.class;
                }
            }
        };

        tableHasil = new JTable(modelHasil);

        // Custom renderer untuk kolom skor (format angka)
        tableHasil.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Format angka jika value adalah Number
                if (value instanceof Number) {
                    setText(String.format("%.4f", ((Number)value).doubleValue()));
                }

                c.setForeground(Color.BLACK);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });

        // Styling tabel hasil
        tableHasil.setRowHeight(30); // Diubah dari 5 ke 30
        tableHasil.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableHasil.setFillsViewportHeight(true);
        tableHasil.setAutoCreateRowSorter(true);
        tableHasil.setShowGrid(false);
        tableHasil.setIntercellSpacing(new Dimension(5, 5));
        tableHasil.setSelectionBackground(new Color(220, 220, 255));
        tableHasil.setSelectionForeground(Color.BLACK);

        // Styling header
        JTableHeader header = tableHasil.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TABLE_HEADER_TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Set renderer untuk kolom lainnya
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                c.setForeground(Color.BLACK);
                c.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }

                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        };

        // Terapkan ke kolom 0, 1, dan 3
        tableHasil.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableHasil.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tableHasil.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tableHasil);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    private void customizeTable(JTable table) {
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(5, 5)); // Diperkecil dari 100,100
        table.setSelectionBackground(new Color(220, 220, 255));
        table.setSelectionForeground(Color.BLACK);

        // Styling header tabel
        JTableHeader header = table.getTableHeader();
        header.setBackground(TABLE_HEADER_COLOR);
        header.setForeground(TABLE_HEADER_TEXT_COLOR);
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Renderer untuk isi tabel
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Set warna teks default
                c.setForeground(Color.BLACK); // Pastikan teks berwarna gelap
                c.setFont(new Font("Segoe UI", Font.PLAIN, 12));

                // Warna background
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 248, 248));
                }

                // Alignment tengah
                ((JLabel)c).setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        };

        // Terapkan ke semua kolom
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    private void loadData() {
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                refreshAlternatifCombo();
                loadTable();
                return null;
            }
        };
        w.execute();
    }
    public void refreshAlternatifCombo() {
        SwingWorker<List<Alternatif>, Void> w = new SwingWorker<>() {
            @Override
            protected List<Alternatif> doInBackground() {
                return alternatifDAO.getAll();
            }

            protected void done() {
                try {
                    cbAlternatif.removeAllItems();
                    for (Alternatif a : get()) cbAlternatif.addItem(a);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel1.this,
                            "Gagal memuat alternatif: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }
    private void loadTable() {
        SwingWorker<List<MatrixAlternatif>, Void> w = new SwingWorker<>() {
            @Override
            protected List<MatrixAlternatif> doInBackground() {
                return matrixDAO.getAll();
            }

            @Override
            protected void done() {
                try {
                    model.setRowCount(0);
                    List<MatrixAlternatif> mats = get();
                    List<Alternatif> als = alternatifDAO.getAll();

                    Map<Integer, Map<Integer, Double>> map = new HashMap<>();
                    for (MatrixAlternatif m : mats) {
                        map.computeIfAbsent(m.getIdAlternatif(), k -> new HashMap<>())
                                .put(m.getIdKriteria(), m.getNilai());
                    }

                    for (Alternatif a : als) {
                        Vector<Object> row = new Vector<>();
                        row.add(a.getId());
                        row.add(a.getNama());

                        Map<Integer, Double> sub = map.getOrDefault(a.getId(), new HashMap<>());
                        allKriteria.forEach(k -> row.add(sub.getOrDefault(k.getIdKriteria(), 0.0)));

                        model.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel1.this,
                            "Gagal memuat tabel: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }
    private void fillFormFromTable() {
        int sel = table.getSelectedRow();
        if (sel == -1) return;

        int id = (Integer) model.getValueAt(sel, 0);
        String name = (String) model.getValueAt(sel, 1);
        selectCombo(cbAlternatif, name, Alternatif::getNama);

        for (int i = 0; i < allKriteria.size(); i++) {
            Object v = model.getValueAt(sel, i + 2);
            kriteriaFields.get(i).setText(v == null ? "" : v.toString());
        }
    }
    private void save() {
    try {
        Alternatif alt = (Alternatif) cbAlternatif.getSelectedItem();
        if (alt == null) {
            JOptionPane.showMessageDialog(this, "Pilih alternatif terlebih dahulu", "Peringatan", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean dataTersimpan = false;
        for (int i = 0; i < allKriteria.size(); i++) {
            Kriteria k = allKriteria.get(i);
            String nilaiText = kriteriaFields.get(i).getText().trim();
            
            if (!nilaiText.isEmpty()) {
                try {
                    double nilai = Double.parseDouble(nilaiText);
                    if (nilai < 0) {
                        throw new NumberFormatException("Nilai tidak boleh negatif");
                    }

                    MatrixAlternatif existing = matrixDAO.getByAlternatifAndKriteria(alt.getId(), k.getIdKriteria());
                    MatrixAlternatif matrix = new MatrixAlternatif(
                        existing != null ? existing.getId() : 0,
                        alt.getId(),
                        k.getIdKriteria(),
                        nilai
                    );

                    if (existing != null) {
                        matrixDAO.update(matrix);
                    } else {
                        matrixDAO.insert(matrix);
                    }
                    dataTersimpan = true;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Nilai tidak valid untuk kriteria " + k.getNamaKriteria() + ": " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        if (dataTersimpan) {
            JOptionPane.showMessageDialog(this, "Data berhasil disimpan", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            loadTable();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dimasukkan", "Peringatan", JOptionPane.WARNING_MESSAGE);
        }
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Gagal menyimpan data: " + ex.getMessage(), 
            "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void delete() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow < 0) {
        JOptionPane.showMessageDialog(this, 
            "Pilih data yang akan dihapus terlebih dahulu", 
            "Peringatan", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int idAlternatif = (Integer) model.getValueAt(selectedRow, 0);
    String namaAlternatif = (String) model.getValueAt(selectedRow, 1);

    int confirm = JOptionPane.showConfirmDialog(this, 
        "Apakah Anda yakin ingin menghapus data untuk alternatif " + namaAlternatif + "?", 
        "Konfirmasi", JOptionPane.YES_NO_OPTION);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            boolean success = true;
            for (Kriteria k : allKriteria) {
                if (!matrixDAO.delete(idAlternatif, k.getIdKriteria())) {
                    success = false;
                    break;
                }
            }

            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Data berhasil dihapus", 
                    "Sukses", JOptionPane.INFORMATION_MESSAGE);
                loadTable();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Gagal menghapus beberapa data", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Gagal menghapus data: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
    private void hitungSkorAlternatif() {
        btnHitungSkor.setEnabled(false); // Disable tombol selama proses
        btnHitungSkor.setText("Menghitung...");

        SwingWorker<Map<Alternatif, Double>, Void> worker = new SwingWorker<>() {
            @Override
            protected Map<Alternatif, Double> doInBackground() throws Exception {
                List<Alternatif> alternatifs = alternatifDAO.getAll();
                List<MatrixAlternatif> matrices = matrixDAO.getAll();
                List<Kriteria> kriterias = kriteriaDAO.getAll();

                // 1. Hitung total nilai per kriteria
                Map<Integer, Double> totalPerKriteria = new HashMap<>();
                for (Kriteria k : kriterias) {
                    double total = matrices.stream()
                        .filter(m -> m.getIdKriteria() == k.getIdKriteria())
                        .mapToDouble(MatrixAlternatif::getNilai)
                        .sum();
                    totalPerKriteria.put(k.getIdKriteria(), total);
                }

                // 2. Hitung skor per alternatif
                Map<Alternatif, Double> skorAlternatif = new HashMap<>();
                for (Alternatif a : alternatifs) {
                    double totalSkor = 0.0;

                    for (Kriteria k : kriterias) {
                        // Cari nilai alternatif untuk kriteria ini
                        double nilai = matrices.stream()
                            .filter(m -> m.getIdAlternatif() == a.getId() && 
                                       m.getIdKriteria() == k.getIdKriteria())
                            .mapToDouble(MatrixAlternatif::getNilai)
                            .findFirst()
                            .orElse(0.0);

                        // Normalisasi
                        double total = totalPerKriteria.getOrDefault(k.getIdKriteria(), 1.0);
                        double normalized = (total == 0) ? 0 : (nilai / total);

                        // Hitung kontribusi bobot
                        totalSkor += normalized * k.getBobot();
                    }

                    skorAlternatif.put(a, totalSkor);
                }

                return skorAlternatif;
            }

            @Override
            protected void done() {
                try {
                    Map<Alternatif, Double> hasil = get();

                    // Update tabel hasil
                    modelHasil.setRowCount(0);

                    // Urutkan dari skor tertinggi ke terendah
                    List<Map.Entry<Alternatif, Double>> sorted = hasil.entrySet().stream()
                        .sorted(Map.Entry.<Alternatif, Double>comparingByValue().reversed())
                        .collect(Collectors.toList());

                    // Simpan ke database dan tampilkan
                    hasilDAO.deleteAll();
                    int peringkat = 1;

                    for (Map.Entry<Alternatif, Double> entry : sorted) {
                        String rekomendasi = (peringkat <= 3) ? "Direkomendasikan" : "-";

                        // Tambah ke tabel
                        modelHasil.addRow(new Object[]{
                            peringkat,
                            entry.getKey().getNama(),
                            String.format("%.4f", entry.getValue()),
                            rekomendasi
                        });

                        // Simpan ke database
                        hasilDAO.insert(new HasilAlternatif(
                            0,
                            entry.getKey().getId(),
                            entry.getValue(),
                            peringkat,
                            rekomendasi
                        ));

                        peringkat++;
                    }

                    // Refresh tampilan tabel
                    tableHasil.updateUI();

                    JOptionPane.showMessageDialog(MatrixAlternatifPanel1.this, 
                        "Perhitungan skor berhasil diselesaikan", 
                        "Sukses", JOptionPane.INFORMATION_MESSAGE);

                } catch (InterruptedException | ExecutionException e) {
                    JOptionPane.showMessageDialog(MatrixAlternatifPanel1.this,
                        "Error dalam perhitungan: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnHitungSkor.setEnabled(true);
                    btnHitungSkor.setText("Hitung Skor");
                }
            }
        };

        worker.execute();
    }
    private void clearForm() {
        cbAlternatif.setSelectedIndex(-1);
        kriteriaFields.forEach(tf -> tf.setText(""));
        table.clearSelection();
    }
    private <T> void selectCombo(JComboBox<T> cb, String value, java.util.function.Function<T, String> getter) {
        for (int i = 0; i < cb.getItemCount(); i++) {
            if (getter.apply(cb.getItemAt(i)).equals(value)) {
                cb.setSelectedIndex(i);
                return;
            }
        }
    }
}
