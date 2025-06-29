package com.hop.ui;

import java.awt.CardLayout;
import java.util.HashMap;
import javax.swing.JPanel;

public final class ContenPanel extends JPanel{
    private final CardLayout cardLayout;
    private final HashMap<String, JPanel> panels;

    public ContenPanel() {
        this.panels = new HashMap<>();
        cardLayout = new CardLayout();
        setLayout(cardLayout);

        // Tambahkan semua panel
        addPanel("dashboard", new DashBoardPanel());
        addPanel("kriteria", new KriteriaPanel()); 
        addPanel("alternatif", new AlternatifPanel()); 
        addPanel("matrix",new MatrixPanel());
        addPanel("matrixalternatif",new MatrixAlternatifPanel());
        addPanel("laporan",new JPanel());
        showPanel("dashboard");
    }

    public void addPanel(String name, JPanel panel) {
        panels.put(name, panel);
        add(panel, name);
    }

    public void showPanel(String name) {
        cardLayout.show(this, name);
    }
}
