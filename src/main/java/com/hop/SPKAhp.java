package com.hop;

import com.hop.ui.ContenPanel;
import com.hop.ui.FooterPanel;
import com.hop.ui.HeaderPanel;
import com.hop.ui.SidebarPanel;
import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SPKAhp extends JFrame {

    public SPKAhp() {
        setTitle("SISTEM PENUNJANG KEPUTUSAN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        ContenPanel contentPanel = new ContenPanel();

        add(new HeaderPanel(), BorderLayout.NORTH);
        add(new SidebarPanel(contentPanel), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        FlatlafSetup.setup();
        SwingUtilities.invokeLater(SPKAhp::new);
    }
}
