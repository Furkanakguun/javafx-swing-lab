package org.example.common;

public class PerformanceMetrics {
    
    private final double fps;
    private final double cpuUsage;
    private final long memoryUsedMB;
    private final long memoryTotalMB;
    private final long timestamp;
    
    public PerformanceMetrics(double fps, double cpuUsage, long memoryUsedMB, long memoryTotalMB) {
        this.fps = fps;
        this.cpuUsage = cpuUsage;
        this.memoryUsedMB = memoryUsedMB;
        this.memoryTotalMB = memoryTotalMB;
        this.timestamp = System.currentTimeMillis();
    }
    
    public double getFps() {
        return fps;
    }
    
    public double getCpuUsage() {
        return cpuUsage;
    }
    
    public long getMemoryUsedMB() {
        return memoryUsedMB;
    }
    
    public long getMemoryTotalMB() {
        return memoryTotalMB;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    @Override
    public String toString() {
        return String.format("FPS: %.1f | CPU: %.1f%% | Memory: %d/%d MB", 
                           fps, cpuUsage, memoryUsedMB, memoryTotalMB);
    }
}

