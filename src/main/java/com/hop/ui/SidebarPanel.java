package com.hop.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SidebarPanel extends JPanel {
    public SidebarPanel(ContenPanel contenPanel) {
        setBackground(new Color(44, 62, 80));
        setPreferredSize(new Dimension(200, 0));
        setLayout(new GridLayout(0, 1, 0, 5));

        String[] menuItems = {"Dashboard", "Kriteria", "Alternatif","Matrix","MatrixAlternatif","Laporan"};
        String[] panelKeys = {"dashboard", "kriteria", "alternatif","matrix","matrixalternatif","laporan"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton btn = new JButton(menuItems[i]);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(52, 73, 94));
            btn.setForeground(Color.WHITE);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

            String panelName = panelKeys[i];
            btn.addActionListener(e -> contenPanel.showPanel(panelName));

            add(btn);
        }
    }
    
}
