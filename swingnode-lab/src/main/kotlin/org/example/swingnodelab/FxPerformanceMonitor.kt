package org.example.swingnodelab

import javafx.animation.AnimationTimer
import javafx.scene.control.Label
import java.lang.management.ManagementFactory

class FxPerformanceMonitor(private val statusLabel: Label) {
    private var lastFpsTime = System.nanoTime()
    private var lastUiUpdate = System.nanoTime()
    private var frameCount = 0L
    private var fps = 0.0

    private val timer = object : AnimationTimer() {
        override fun handle(now: Long) {
            frameCount++
            val elapsed = now - lastFpsTime
            if (elapsed >= 1_000_000_000L) {
                fps = frameCount * 1_000_000_000.0 / elapsed
                frameCount = 0
                lastFpsTime = now
            }

            if (now - lastUiUpdate >= 100_000_000L) {
                val cpu = getProcessCpuLoad()
                val usedMb = getUsedMemoryMb()
                val totalMb = getTotalMemoryMb()
                val cpuText = if (cpu < 0) "N/A" else String.format("%.1f%%", cpu)
                statusLabel.text = String.format(
                    "Refresh: %.1f fps | Pulse: ~60Hz | CPU: %s | Memory: %d/%d MB",
                    fps, cpuText, usedMb, totalMb
                )
                lastUiUpdate = now
            }
        }
    }

    fun start() {
        timer.start()
    }

    fun stop() {
        timer.stop()
    }

    private fun getProcessCpuLoad(): Double {
        val osBean = ManagementFactory.getOperatingSystemMXBean()
        return if (osBean is com.sun.management.OperatingSystemMXBean) {
            val load = osBean.processCpuLoad
            if (load >= 0) load * 100.0 else -1.0
        } else {
            -1.0
        }
    }

    private fun getUsedMemoryMb(): Long {
        val runtime = Runtime.getRuntime()
        val usedBytes = runtime.totalMemory() - runtime.freeMemory()
        return usedBytes / (1024 * 1024)
    }

    private fun getTotalMemoryMb(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() / (1024 * 1024)
    }
}

