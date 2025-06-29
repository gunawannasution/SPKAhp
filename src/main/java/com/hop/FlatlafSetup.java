package com.hop;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.UIManager;

public class FlatlafSetup {
    public static void setup() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
