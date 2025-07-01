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
    // Modern color palette
    private final Color PRIMARY_COLOR = new Color(63, 81, 181);
    private final Color SECONDARY_COLOR = new Color(233, 30, 99);
    private final Color ACCENT_COLOR = new Color(255, 193, 7);
    private final Color BACKGROUND_COLOR = new Color(250, 250, 250);
    private final Color CARD_SHADOW = new Color(0, 0, 0, 20);

    public DashBoardPanel() {
        setLayout(new MigLayout("wrap 3, insets 20, gap 20", "[grow,fill][grow,fill][grow,fill]"));
        setBackground(BACKGROUND_COLOR);

        // Modern card design
        add(createModernCard("Jumlah Kriteria", "5", PRIMARY_COLOR, ""));
        add(createModernCard("Jumlah Alternatif", "10", SECONDARY_COLOR, ""));
        add(createModernCard("Status AHP", "Completed", ACCENT_COLOR, ""));

        // Modern chart panel
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
            new EmptyBorder(15, 0, 0, 0),
            new ModernBorder("Hasil Analisis AHP", PRIMARY_COLOR)
        ));
        chartContainer.add(createModernChartPanel(), BorderLayout.CENTER);
        add(chartContainer, "span 3, grow, h 350!");
    }

    private JPanel createModernCard(String title, String value, Color color, String iconPath) {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                
                // Shadow effect
                g2d.setColor(CARD_SHADOW);
                g2d.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, 20, 20);
                
                // Card background with rounded corners
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, 20, 20);
                
                // Left border accent
                g2d.setColor(color);
                g2d.fillRoundRect(0, 0, 7, getHeight()-5, 20, 20);
                
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 25, 20, 20));

        // Icon with modern styling
        JLabel iconLabel = new JLabel();
        iconLabel.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        iconLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(iconLabel, BorderLayout.WEST);

        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout(5, 5));
        panel.add(contentPanel, BorderLayout.CENTER);

        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI SemiBold", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        contentPanel.add(titleLabel, BorderLayout.NORTH);

        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        contentPanel.add(valueLabel, BorderLayout.CENTER);

        // Optional: Add a subtle animation on hover
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBorder(new EmptyBorder(18, 23, 18, 18));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBorder(new EmptyBorder(20, 25, 20, 20));
            }
        });

        return panel;
    }

    private ChartPanel createModernChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Karyawan A", 40);
        dataset.setValue("Karyawan B", 30);
        dataset.setValue("Karyawan C", 20);
        dataset.setValue("Karyawan D", 10);

        JFreeChart chart = ChartFactory.createPieChart(
            null, // No title
            dataset,
            true, // Include legend
            true, // Tooltips
            false // No URLs
        );

        // Modern chart styling
        chart.setBackgroundPaint(null);
        chart.setBorderVisible(false);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
        plot.setSectionPaint("Karyawan A", new Color(63, 81, 181));
        plot.setSectionPaint("Karyawan B", new Color(233, 30, 99));
        plot.setSectionPaint("Karyawan C", new Color(255, 193, 7));
        plot.setSectionPaint("Karyawan D", new Color(76, 175, 80));
        
        // Add gradient effects to pie sections
        plot.setSectionOutlinesVisible(false);
        plot.setShadowXOffset(0);
        plot.setShadowYOffset(0);
        plot.setLabelGenerator(null); // Remove labels from pie slices

        ChartPanel chartPanel = new ChartPanel(chart) {
            @Override
            public void paintComponent(Graphics g) {  // Changed to public
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Paint rounded corners
                Shape clip = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setClip(clip);
                super.paintComponent(g);
            }
        };
        chartPanel.setOpaque(false);
        chartPanel.setBackground(new Color(255, 255, 255, 200));
        chartPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        return chartPanel;
    }

    // Custom border class for modern styling
    private static class ModernBorder extends EmptyBorder {
        private final String title;
        private final Color color;

        public ModernBorder(String title, Color color) {
            super(15, 0, 15, 0);
            this.title = title;
            this.color = color;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw title
            g2d.setFont(new Font("Segoe UI SemiBold", Font.PLAIN, 14));
            FontMetrics fm = g2d.getFontMetrics();
            int titleWidth = fm.stringWidth(title);
            
            g2d.setColor(color);
            g2d.drawString(title, 15, 12);
            
            // Draw line
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawLine(15 + titleWidth + 10, 6, width - 15, 6);
            
            g2d.dispose();
        }
    }
}