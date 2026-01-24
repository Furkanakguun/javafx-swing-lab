package org.example;

import org.example.viewer.MapViewerPanel;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TestStep2_1 {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Step 2.1 - MapViewerPanel Basic Structure");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            MapViewerPanel mapViewer = new MapViewerPanel();
            frame.add(mapViewer);
            
            frame.pack();
            
            frame.setLocationRelativeTo(null);
            
            frame.setVisible(true);
            
            System.out.println("MapViewerPanel created successfully!");
            System.out.println("Layer count: " + mapViewer.getLayerCount());
        });
    }
}

