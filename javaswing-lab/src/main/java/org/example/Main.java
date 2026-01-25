package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SwingBenchmarkFrame frame = new SwingBenchmarkFrame();
            frame.setVisible(true);
        });
    }
}