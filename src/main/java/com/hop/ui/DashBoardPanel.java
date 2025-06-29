package com.hop.ui;

import net.miginfocom.swing.MigLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;

public class DashBoardPanel extends JPanel {
    public DashBoardPanel() {
        setLayout(new MigLayout("wrap 3, insets 20, gap 20", "[grow,fill][grow,fill][grow,fill]"));

        // Warna tema Flat / Material
        Color cardColor = new Color(250, 250, 250);
        Font titleFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font valueFont = new Font("Segoe UI", Font.BOLD, 24);

        // Panel kartu (ringkasan)
        add(createCard("Jumlah Kriteria", "5", cardColor, titleFont, valueFont));
        add(createCard("Jumlah Alternatif", "10", cardColor, titleFont, valueFont));
        add(createCard("Status", "Sudah Dihitung", cardColor, titleFont, valueFont));

        // Chart Panel (pie chart hasil AHP)
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createTitledBorder("Hasil Penilaian AHP"));
        chartContainer.add(createChartPanel(), BorderLayout.CENTER);
        add(chartContainer, "span 3, grow, h 300!");
    }
    private JPanel createCard(String title, String value, Color bg, Font titleFont, Font valueFont) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(100, 100, 100));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(valueFont);
        valueLabel.setForeground(new Color(33, 150, 243));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);

        return panel;
    }
    private ChartPanel createChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Karyawan A", 40);
        dataset.setValue("Karyawan B", 30);
        dataset.setValue("Karyawan C", 20);
        dataset.setValue("Karyawan D", 10);

        JFreeChart chart = ChartFactory.createPieChart(
            "", dataset, true, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);

        return new ChartPanel(chart);
    }
}
