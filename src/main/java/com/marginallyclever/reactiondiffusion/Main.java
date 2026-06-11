package com.marginallyclever.reactiondiffusion;

import com.formdev.flatlaf.FlatDarkLaf;
import com.marginallyclever.reactiondiffusion.gui.MainWindow;

import javax.swing.*;

/**
 * Entry point.  Must run on the Swing EDT.
 */
public class Main {

    public static void main(String[] args) {
        // Install FlatLaf dark theme before any Swing component is created.
        FlatDarkLaf.setup();

        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
        });
    }
}
