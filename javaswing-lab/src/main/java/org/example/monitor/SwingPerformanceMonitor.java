package org.example.monitor;

import org.example.common.PerformanceMetrics;

import java.lang.management.ManagementFactory;

public class SwingPerformanceMonitor {
    
    private long lastFpsTime;
    private long frameCount;
    private double fps;
    
    public SwingPerformanceMonitor() {
        this.lastFpsTime = System.nanoTime();
        this.frameCount = 0;
        this.fps = 0.0;
    }
    
    public void onFrameRendered() {
        frameCount++;
        long now = System.nanoTime();
        long elapsedNanos = now - lastFpsTime;
        if (elapsedNanos >= 1_000_000_000L) {
            fps = frameCount * 1_000_000_000.0 / elapsedNanos;
            frameCount = 0;
            lastFpsTime = now;
        }
    }
    
    public PerformanceMetrics getMetrics() {
        double cpuUsage = getProcessCpuLoad();
        long usedMB = getUsedMemoryMB();
        long totalMB = getTotalMemoryMB();
        return new PerformanceMetrics(fps, cpuUsage, usedMB, totalMB);
    }
    
    private double getProcessCpuLoad() {
        var osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            double load = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad();
            if (load >= 0) {
                return load * 100.0;
            }
        }
        return -1.0;
    }
    
    private long getUsedMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        long usedBytes = runtime.totalMemory() - runtime.freeMemory();
        return usedBytes / (1024 * 1024);
    }
    
    private long getTotalMemoryMB() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() / (1024 * 1024);
    }
}

