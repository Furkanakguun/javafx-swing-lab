package org.example.model

import javafx.scene.paint.Color

data class GISPolygon(
    val xPoints: DoubleArray,
    val yPoints: DoubleArray,
    val fillColor: Color,
    val strokeColor: Color,
    val strokeWidth: Double
)

