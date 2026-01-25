package org.example;

import org.example.common.DrawMode;
import org.example.monitor.SwingPerformanceMonitor;
import org.example.monitor.SwingRefreshRateMonitor;
import org.example.viewer.MapViewerPanel;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class SwingBenchmarkFrame extends JFrame {
    
    public SwingBenchmarkFrame() {
        super("Swing Benchmark");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MapViewerPanel mapViewer = new MapViewerPanel();
        SwingPerformanceMonitor monitor = new SwingPerformanceMonitor();
        mapViewer.setPerformanceMonitor(monitor);
        
        JLabel statusLabel = new JLabel("Refresh: 0.0 fps | CPU: N/A | Memory: 0/0 MB");
        SwingRefreshRateMonitor refreshMonitor = new SwingRefreshRateMonitor(monitor, statusLabel);
        refreshMonitor.start();
        
        setLayout(new BorderLayout());
        add(mapViewer, BorderLayout.CENTER);
        add(createControlPanel(mapViewer, statusLabel), BorderLayout.SOUTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private JPanel createControlPanel(MapViewerPanel mapViewer, JLabel statusLabel) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton panButton = new JButton("Pan");
        JButton lineButton = new JButton("Create Line");
        JButton polygonButton = new JButton("Create Polygon");
        JButton clearButton = new JButton("Clear Map");
        JButton zoomInButton = new JButton("Zoom +");
        JButton zoomOutButton = new JButton("Zoom -");
        JButton resetButton = new JButton("Reset");
        
        panButton.addActionListener(e -> mapViewer.setDrawMode(DrawMode.NONE));
        lineButton.addActionListener(e -> mapViewer.setDrawMode(DrawMode.LINE));
        polygonButton.addActionListener(e -> mapViewer.setDrawMode(DrawMode.POLYGON));
        clearButton.addActionListener(e -> mapViewer.clearUserShapes());
        zoomInButton.addActionListener(e -> mapViewer.zoomIn());
        zoomOutButton.addActionListener(e -> mapViewer.zoomOut());
        resetButton.addActionListener(e -> mapViewer.resetView());
        
        panel.add(panButton);
        panel.add(lineButton);
        panel.add(polygonButton);
        panel.add(clearButton);
        panel.add(zoomInButton);
        panel.add(zoomOutButton);
        panel.add(resetButton);
        panel.add(statusLabel);
        
        return panel;
    }
}

