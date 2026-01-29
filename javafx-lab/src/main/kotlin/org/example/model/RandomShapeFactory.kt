package org.example.model

import javafx.scene.paint.Color
import org.example.common.GISTestConfig
import kotlin.random.Random

object RandomShapeFactory {
    fun randomPoint(random: Random): GISPoint {
        val x = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X)
        val y = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y)
        val color = randomColor(random, 120, 220, 1.0)
        val size = 3 + random.nextInt(5)
        return GISPoint(x, y, color, size)
    }

    fun randomLine(random: Random): GISLine {
        val x1 = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X)
        val y1 = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y)
        val x2 = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X)
        val y2 = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y)
        val color = randomColor(random, 80, 200, 1.0)
        val strokeWidth = 1.0 + random.nextDouble() * 2.0
        return GISLine(x1, y1, x2, y2, color, strokeWidth)
    }

    fun randomPolygon(random: Random): GISPolygon {
        val points = 3 + random.nextInt(4)
        val xPoints = DoubleArray(points)
        val yPoints = DoubleArray(points)
        val centerX = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X)
        val centerY = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y)
        val radius = 20 + random.nextDouble() * 80

        for (i in 0 until points) {
            val angle = 2 * Math.PI * i / points + random.nextDouble() * 0.3
            xPoints[i] = centerX + Math.cos(angle) * radius
            yPoints[i] = centerY + Math.sin(angle) * radius
        }

        val fillColor = randomColor(random, 80, 180, 0.5)
        val strokeColor = randomColor(random, 60, 160, 1.0)
        val strokeWidth = 1.0 + random.nextDouble() * 2.5
        return GISPolygon(xPoints, yPoints, fillColor, strokeColor, strokeWidth)
    }

    private fun randomCoord(random: Random, min: Double, max: Double): Double {
        return min + (max - min) * random.nextDouble()
    }

    private fun randomColor(random: Random, min: Int, max: Int, alpha: Double): Color {
        val r = min + random.nextInt(max - min + 1)
        val g = min + random.nextInt(max - min + 1)
        val b = min + random.nextInt(max - min + 1)
        return Color.rgb(r, g, b, alpha)
    }
}

