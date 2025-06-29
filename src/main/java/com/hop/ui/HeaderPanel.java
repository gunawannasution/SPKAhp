
package com.hop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HeaderPanel extends JPanel{
    public HeaderPanel() {
        setBackground(new Color(33, 150, 243));
        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());
        
        //logo
        JLabel logo=new JLabel();
        //ImageIcon icon=new ImageIcon(getClass().getResource("images/logo.png"));
        //Image scaledImage = icon.getImage().getScaledInstance(40,40,Image.SCALE_SMOOTH);
        //logo.setIcon(new ImageIcon(scaledImage));
        //.setBorder(BorderFactory.createEmptyBorder(10,20,10,10));
        
        JLabel userLabel = new JLabel("Halo, Admin");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 20));
        add(userLabel, BorderLayout.EAST);
        

        JLabel title = new JLabel("SISTEM PENDUKUNG KEPUTUSAN AHP", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);

        add(title, BorderLayout.CENTER);
    }
}
