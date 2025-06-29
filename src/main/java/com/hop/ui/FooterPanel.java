package com.hop.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FooterPanel extends JPanel {
    public FooterPanel() {
        setBackground(new Color(236, 240, 241));
        setPreferredSize(new Dimension(0, 30));
        setLayout(new FlowLayout(FlowLayout.RIGHT));

        JLabel info = new JLabel("© 2025 Sistem AHP - All rights reserved");
        info.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        add(info);
    }
}
