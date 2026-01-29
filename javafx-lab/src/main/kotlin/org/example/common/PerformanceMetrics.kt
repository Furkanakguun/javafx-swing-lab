package org.example.common

data class PerformanceMetrics(
    val fps: Double,
    val cpuUsage: Double,
    val memoryUsedMB: Long,
    val memoryTotalMB: Long,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun toString(): String {
        val cpuText = if (cpuUsage < 0) "N/A" else String.format("%.1f%%", cpuUsage)
        return String.format(
            "Refresh: %.1f fps | CPU: %s | Memory: %d/%d MB",
            fps,
            cpuText,
            memoryUsedMB,
            memoryTotalMB
        )
    }
}

