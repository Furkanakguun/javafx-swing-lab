package org.example.model

import javafx.scene.paint.Color

data class GISLine(
    val x1: Double,
    val y1: Double,
    val x2: Double,
    val y2: Double,
    val color: Color,
    val strokeWidth: Double
)

