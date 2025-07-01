package com.hop.ui;

import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class DashBoardPanel extends JPanel {

    // Warna utama yang digunakan di seluruh dashboard
    private final Color PRIMARY = new Color(63, 81, 181);
    private final Color SECONDARY = new Color(233, 30, 99);
    private final Color ACCENT = new Color(255, 193, 7);
    private final Color BACKGROUND = new Color(250, 250, 250);
    private final Color SHADOW = new Color(0, 0, 0, 20);

    public DashBoardPanel() {
        setLayout(new MigLayout("wrap 3, insets 20, gap 20", "[grow,fill][grow,fill][grow,fill]"));
        setBackground(BACKGROUND);

        // Menambahkan 3 kotak info
        add(createInfoCard("Jumlah Kriteria", "5", PRIMARY));
        add(createInfoCard("Jumlah Alternatif", "10", SECONDARY));
        add(createInfoCard("Status AHP", "Completed", ACCENT));

        // Tambahkan grafik pie di bagian bawah
        JPanel chartPanel = createChartSection();
        add(chartPanel, "span 3, grow, h 350!");
    }

    /**
     * Membuat panel info (kotak atas) yang menampilkan angka & label
     */
    private JPanel createInfoCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                // Bayangan
                g2.setColor(SHADOW);
                g2.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 20, 20);

                // Latar putih
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);

                // Garis vertikal warna di kiri
                g2.setColor(color);
                g2.fillRoundRect(0, 0, 7, getHeight() - 5, 20, 20);

                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 25, 20, 20));

        // Panel isi tengah
        JPanel content = new JPanel(new BorderLayout(5, 5));
        content.setOpaque(false);

        // Judul
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));

        // Nilai utama
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        content.add(titleLabel, BorderLayout.NORTH);
        content.add(valueLabel, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    /**
     * Membuat panel grafik pie sederhana dengan warna modern
     */
    private JPanel createChartSection() {
        // Dataset pie chart
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Karyawan A", 40);
        dataset.setValue("Karyawan B", 30);
        dataset.setValue("Karyawan C", 20);
        dataset.setValue("Karyawan D", 10);

        // Buat chart-nya
        JFreeChart chart = ChartFactory.createPieChart(
            null, // Tidak ada judul
            dataset,
            true,
            true,
            false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setLabelGenerator(null); // Tidak tampil label di slice

        // Warna untuk setiap bagian
        plot.setSectionPaint("Karyawan A", PRIMARY);
        plot.setSectionPaint("Karyawan B", SECONDARY);
        plot.setSectionPaint("Karyawan C", ACCENT);
        plot.setSectionPaint("Karyawan D", new Color(76, 175, 80));

        // Panel pembungkus chart dengan sudut tumpul
        ChartPanel chartPanel = new ChartPanel(chart) {
            @Override
            public void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Sudut tumpul
                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setClip(clip);
                super.paintComponent(g);
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bungkus dalam panel + judul border
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setBorder(new ChartTitleBorder("Hasil Analisis AHP", PRIMARY));
        container.add(chartPanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * Border khusus dengan judul modern
     */
    private static class ChartTitleBorder extends EmptyBorder {
        private final String title;
        private final Color color;

        public ChartTitleBorder(String title, Color color) {
            super(15, 0, 15, 0);
            this.title = title;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int titleWidth = fm.stringWidth(title);

            g2.setColor(color);
            g2.drawString(title, 15, 12);
            g2.drawLine(15 + titleWidth + 10, 6, width - 15, 6);

            g2.dispose();
        }
    }
}
