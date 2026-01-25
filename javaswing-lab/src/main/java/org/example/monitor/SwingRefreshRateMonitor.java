package org.example.monitor;

import javax.swing.JLabel;
import javax.swing.Timer;

public class SwingRefreshRateMonitor {
    
    private final SwingPerformanceMonitor swingMonitor;
    private final JLabel statusLabel;
    private final Timer timer;
    
    public SwingRefreshRateMonitor(SwingPerformanceMonitor swingMonitor, JLabel statusLabel) {
        this.swingMonitor = swingMonitor;
        this.statusLabel = statusLabel;
        this.timer = new Timer(100, e -> updateLabel());
    }
    
    public void start() {
        timer.start();
    }
    
    public void stop() {
        timer.stop();
    }
    
    private void updateLabel() {
        var metrics = swingMonitor.getMetrics();
        String cpuText = metrics.getCpuUsage() < 0 ? "N/A" : String.format("%.1f%%", metrics.getCpuUsage());
        statusLabel.setText(String.format(
            "Refresh: %.1f fps | CPU: %s | Memory: %d/%d MB",
            metrics.getFps(),
            cpuText,
            metrics.getMemoryUsedMB(),
            metrics.getMemoryTotalMB()
        ));
    }
}

