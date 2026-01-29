package org.example.model

class Layer(val name: String) {
    val points: MutableList<GISPoint> = mutableListOf()
    val lines: MutableList<GISLine> = mutableListOf()
    val polygons: MutableList<GISPolygon> = mutableListOf()
    var visible: Boolean = true

    fun addPoint(point: GISPoint) {
        points.add(point)
    }

    fun addLine(line: GISLine) {
        lines.add(line)
    }

    fun addPolygon(polygon: GISPolygon) {
        polygons.add(polygon)
    }

    fun clear() {
        points.clear()
        lines.clear()
        polygons.clear()
    }

    fun totalShapeCount(): Int {
        return points.size + lines.size + polygons.size
    }
}

