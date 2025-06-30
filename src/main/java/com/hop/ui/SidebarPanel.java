package com.hop.ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SidebarPanel extends JPanel {
    private JButton activeButton;
    private final Color BACKGROUND_COLOR = new Color(34, 40, 49);
    private final Color MENU_COLOR = new Color(48, 56, 65);
    private final Color HOVER_COLOR = new Color(0, 173, 181);
    private final Color ACTIVE_COLOR = new Color(0, 150, 160);
    private final Color TEXT_COLOR = new Color(238, 238, 238);

    public SidebarPanel(ContenPanel contentPanel) {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(250, 0));
        setBorder(new EmptyBorder(20, 0, 20, 0));

        // Header with logo
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Menu items
        JPanel menuPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        menuPanel.setBackground(BACKGROUND_COLOR);
        menuPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[] menuItems = {"Dashboard", "Kriteria", "Alternatif", "Matrix", "Matrix Alternatif", "Laporan"};
        String[] icons = {"📊", "📋", "👥", "🔢", "🧮", "📑"};
        String[] panelKeys = {"dashboard", "kriteria", "alternatif", "matrix", "matrixalternatif", "laporan"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], icons[i], panelKeys[i], contentPanel);
            menuPanel.add(menuButton);
            
            // Set first button as active by default
            if (i == 0) {
                setActiveButton(menuButton);
            }
        }

        add(menuPanel, BorderLayout.CENTER);

        // Footer with user info
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(0, 20, 20, 20));

        JLabel logo = new JLabel("SPK AHP", SwingConstants.CENTER);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(TEXT_COLOR);
        
        JLabel version = new JLabel("v2.0", SwingConstants.CENTER);
        version.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        version.setForeground(new Color(150, 150, 150));

        panel.add(logo, BorderLayout.CENTER);
        panel.add(version, BorderLayout.SOUTH);
        
        return panel;
    }

    private JButton createMenuButton(String text, String icon, String panelKey, ContenPanel contentPanel) {
        JButton button = new JButton("<html><table><tr><td width='30'>" + icon + "</td><td>" + text + "</td></tr></table></html>");
        button.setFocusPainted(false);
        button.setBackground(MENU_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 25, 12, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeButton) {
                    button.setBackground(MENU_COLOR);
                }
            }
        });

        button.addActionListener(e -> {
            contentPanel.showPanel(panelKey);
            setActiveButton(button);
        });

        return button;
    }

    private void setActiveButton(JButton button) {
        if (activeButton != null) {
            activeButton.setBackground(MENU_COLOR);
        }
        activeButton = button;
        activeButton.setBackground(ACTIVE_COLOR);
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(MENU_COLOR);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel userLabel = new JLabel("<html><small>Logged in as</small><br><b>Administrator</b></html>");
        userLabel.setForeground(TEXT_COLOR);
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setForeground(TEXT_COLOR);
        logoutBtn.setBackground(new Color(231, 76, 60));
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        logoutBtn.setBorder(new EmptyBorder(5, 10, 5, 10));
        logoutBtn.setFocusPainted(false);

        panel.add(userLabel, BorderLayout.CENTER);
        panel.add(logoutBtn, BorderLayout.EAST);
        
        return panel;
    }
}