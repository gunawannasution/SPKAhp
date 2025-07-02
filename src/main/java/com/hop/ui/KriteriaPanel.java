package com.hop.ui;

import com.hop.dao.KriteriaDAO;
import com.hop.dao.KriteriaDAOImpl;
import com.hop.model.Kriteria;
import com.hop.report.ReportUtil;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class KriteriaPanel extends JPanel {

    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private final Color SECONDARY_COLOR = new Color(255, 152, 0);
    private final Color DANGER_COLOR = new Color(233, 30, 99);
    private final Color SUCCESS_COLOR = new Color(0, 150, 136);
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private final Color CARD_COLOR = Color.WHITE;
    
    // Components
    private JTable table;
    private DefaultTableModel model;
    private JTextField tKode, tNama, tKet, tBobot;
    private JButton btnTambah, btnUpdate, btnHapus, btnCetak;
    private KriteriaDAO dao = new KriteriaDAOImpl();
    private int selectedId = -1;

    public KriteriaPanel() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Main card panel
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_COLOR);
        cardPanel.setBorder(new CompoundBorder(
            new ShadowBorder(10, new Color(0, 0, 0, 20)),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Create components
        createTable();
        cardPanel.add(createFormPanel(), BorderLayout.NORTH);
        cardPanel.add(createTablePanel(), BorderLayout.CENTER);

        add(cardPanel, BorderLayout.CENTER);
        loadData();
    }

    private void createTable() {
        model = new DefaultTableModel(new String[]{"ID", "Kode", "Nama", "Keterangan", "Bobot"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 0 ? Integer.class : String.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 230, 255));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        // Custom header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Center alignment for all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Zebra striping
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    c.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(245, 245, 245));
                }
                return c;
            }
        });

        // Row selection listener
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

    private JPanel createTablePanel() {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(0, 0, 15, 0));

        // Title
        JLabel title = new JLabel("Form Kriteria");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(PRIMARY_COLOR);
        title.setBorder(new EmptyBorder(0, 0, 15, 0));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Form fields
        JPanel formFields = new JPanel();
        formFields.setLayout(new GridLayout(2, 2, 15, 15));
        formFields.setBackground(CARD_COLOR);
        formFields.setBorder(new EmptyBorder(0, 0, 15, 0));
        formFields.setAlignmentX(Component.LEFT_ALIGNMENT);

        tKode = createModernTextField("Contoh: C1");
        tNama = createModernTextField("Contoh: Kompetensi");
        tKet = createModernTextField("Contoh: Berdasarkan pelatihan");
        tBobot = createModernTextField("Contoh: 0.25");

        formFields.add(createFieldPanel("Kode:", tKode));
        formFields.add(createFieldPanel("Nama:", tNama));
        formFields.add(createFieldPanel("Keterangan:", tKet));
        formFields.add(createFieldPanel("Bobot:", tBobot));

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        btnTambah = createModernButton("Tambah", PRIMARY_COLOR, e -> tambah());
        btnUpdate = createModernButton("Update", SECONDARY_COLOR, e -> update());
        btnHapus = createModernButton("Hapus", DANGER_COLOR, e -> hapus());
        btnCetak = createModernButton("Cetak", SUCCESS_COLOR, e -> cetak());

        buttonPanel.add(btnTambah);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnHapus);
        buttonPanel.add(btnCetak);

        panel.add(title);
        panel.add(formFields);
        panel.add(buttonPanel);

        return panel;
    }

    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(CARD_COLOR);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lbl.setForeground(new Color(80, 80, 80));
        
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    private JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
            new EmptyBorder(8, 5, 8, 5)
        ));
        field.putClientProperty("JTextField.placeholderText", placeholder);
        return field;
    }

    private JButton createModernButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(action);
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    // Shadow border for modern look
    private static class ShadowBorder extends AbstractBorder {
        private final int shadowSize;
        private final Color shadowColor;
        
        public ShadowBorder(int shadowSize, Color shadowColor) {
            this.shadowSize = shadowSize;
            this.shadowColor = shadowColor;
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            for (int i = 0; i < shadowSize; i++) {
                g2.setColor(new Color(
                    shadowColor.getRed(),
                    shadowColor.getGreen(),
                    shadowColor.getBlue(),
                    shadowColor.getAlpha() * (shadowSize - i) / shadowSize
                ));
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 8, 8);
            }
            
            g2.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
    }

    // Rest of your methods remain the same (muatData, tambah, update, hapus, cetak, etc.)
    // Just copy them exactly as they are from your original code
    private void loadData() {
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
            loadData();
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
            loadData();
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
            loadData();
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