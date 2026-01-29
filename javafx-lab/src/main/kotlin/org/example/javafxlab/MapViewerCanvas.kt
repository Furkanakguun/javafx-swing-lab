package org.example.javafxlab

import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.transform.Affine
import org.example.common.DrawMode
import org.example.common.GISTestConfig
import org.example.model.GISLine
import org.example.model.GISPolygon
import org.example.model.Layer
import org.example.model.RandomShapeFactory
import kotlin.random.Random

class MapViewerCanvas(width: Double, height: Double) : Canvas(width, height) {

    private val layers: MutableList<Layer> = mutableListOf()
    private var userLayer: Layer? = null
    private val random = Random.Default
    private val viewTransform = Affine()
    private var currentScale = GISTestConfig.INITIAL_SCALE
    private var drawMode = DrawMode.NONE
    private var lineStartWorld: Pair<Double, Double>? = null
    private var linePreviewWorld: Pair<Double, Double>? = null
    private val polygonPoints: MutableList<Pair<Double, Double>> = mutableListOf()
    private var polygonPreviewWorld: Pair<Double, Double>? = null
    private var lastMouseX: Double? = null
    private var lastMouseY: Double? = null

    init {
        initializeViewTransform()
        generateRandomLayers()
        installInteractionHandlers()
        render()
    }

    fun render() {
        val g = graphicsContext2D
        clearBackground(g)
        g.save()
        g.transform(viewTransform)
        renderLayers(g)
        renderOverlay(g)
        g.restore()
    }

    private fun clearBackground(g: GraphicsContext) {
        g.fill = Color.WHITE
        g.fillRect(0.0, 0.0, width, height)
    }

    private fun renderLayers(g: GraphicsContext) {
        for (layer in layers) {
            if (!layer.visible) continue
            renderPoints(g, layer)
            renderLines(g, layer)
            renderPolygons(g, layer)
        }
    }

    private fun renderPoints(g: GraphicsContext, layer: Layer) {
        for (point in layer.points) {
            val size = point.size
            val x = point.x - size / 2.0
            val y = point.y - size / 2.0
            g.fill = point.color
            g.fillOval(x, y, size.toDouble(), size.toDouble())
        }
    }

    private fun renderLines(g: GraphicsContext, layer: Layer) {
        for (line in layer.lines) {
            g.stroke = line.color
            g.lineWidth = line.strokeWidth
            g.strokeLine(line.x1, line.y1, line.x2, line.y2)
        }
    }

    private fun renderPolygons(g: GraphicsContext, layer: Layer) {
        for (polygon in layer.polygons) {
            val count = polygon.xPoints.size
            val xPoints = DoubleArray(count)
            val yPoints = DoubleArray(count)
            for (i in 0 until count) {
                xPoints[i] = polygon.xPoints[i]
                yPoints[i] = polygon.yPoints[i]
            }
            g.fill = polygon.fillColor
            g.fillPolygon(xPoints, yPoints, count)
            g.stroke = polygon.strokeColor
            g.lineWidth = polygon.strokeWidth
            g.strokePolygon(xPoints, yPoints, count)
        }
    }

    private fun generateRandomLayers() {
        layers.clear()
        for (i in 0 until GISTestConfig.LAYER_COUNT) {
            val layer = Layer("Layer-${i + 1}")
            generateRandomShapes(layer)
            layers.add(layer)
        }
        userLayer = Layer("User")
        layers.add(userLayer!!)
    }

    private fun generateRandomShapes(layer: Layer) {
        val perLayer = GISTestConfig.SHAPES_PER_LAYER
        val pointsCount = perLayer / 3
        val linesCount = perLayer / 3
        val polygonsCount = perLayer - pointsCount - linesCount

        repeat(pointsCount) { layer.addPoint(RandomShapeFactory.randomPoint(random)) }
        repeat(linesCount) { layer.addLine(RandomShapeFactory.randomLine(random)) }
        repeat(polygonsCount) { layer.addPolygon(RandomShapeFactory.randomPolygon(random)) }
    }

    fun totalShapeCount(): Int {
        return layers.sumOf { it.totalShapeCount() }
    }

    fun setDrawMode(mode: DrawMode) {
        drawMode = mode
        lineStartWorld = null
        linePreviewWorld = null
        polygonPoints.clear()
        polygonPreviewWorld = null
    }

    fun clearUserShapes() {
        userLayer?.clear()
        lineStartWorld = null
        linePreviewWorld = null
        polygonPoints.clear()
        polygonPreviewWorld = null
        render()
    }

    fun zoomIn() {
        applyZoom(GISTestConfig.ZOOM_FACTOR)
    }

    fun zoomOut() {
        applyZoom(1.0 / GISTestConfig.ZOOM_FACTOR)
    }

    fun resetView() {
        currentScale = GISTestConfig.INITIAL_SCALE
        initializeViewTransform()
        render()
    }

    private fun initializeViewTransform() {
        viewTransform.setToIdentity()
        viewTransform.appendTranslation(width / 2.0, height / 2.0)
        viewTransform.appendScale(currentScale, currentScale)
    }

    private fun applyZoom(factor: Double) {
        val newScale = currentScale * factor
        if (newScale < GISTestConfig.MIN_SCALE || newScale > GISTestConfig.MAX_SCALE) return
        currentScale = newScale
        val cx = width / 2.0
        val cy = height / 2.0
        viewTransform.appendTranslation(cx, cy)
        viewTransform.appendScale(factor, factor)
        viewTransform.appendTranslation(-cx, -cy)
        render()
    }

    private fun installInteractionHandlers() {
        setOnMousePressed { e ->
            if (drawMode != DrawMode.NONE) return@setOnMousePressed
            lastMouseX = e.x
            lastMouseY = e.y
        }
        setOnMouseDragged { e ->
            if (drawMode != DrawMode.NONE) return@setOnMouseDragged
            if (lastMouseX == null || lastMouseY == null) {
                lastMouseX = e.x
                lastMouseY = e.y
                return@setOnMouseDragged
            }
            val dx = e.x - lastMouseX!!
            val dy = e.y - lastMouseY!!
            viewTransform.appendTranslation(dx, dy)
            lastMouseX = e.x
            lastMouseY = e.y
            render()
        }
        setOnMouseReleased {
            lastMouseX = null
            lastMouseY = null
        }
        setOnMouseMoved { e ->
            if (drawMode == DrawMode.LINE) {
                if (lineStartWorld == null) {
                    linePreviewWorld = null
                    return@setOnMouseMoved
                }
                linePreviewWorld = screenToWorld(e.x, e.y)
                render()
            } else if (drawMode == DrawMode.POLYGON) {
                if (polygonPoints.isEmpty()) {
                    polygonPreviewWorld = null
                    return@setOnMouseMoved
                }
                polygonPreviewWorld = screenToWorld(e.x, e.y)
                render()
            }
        }
        setOnMouseClicked { e ->
            if (drawMode == DrawMode.NONE || !e.isPrimaryButtonDown) return@setOnMouseClicked
            val world = screenToWorld(e.x, e.y) ?: return@setOnMouseClicked
            val finishPolygon = e.clickCount >= 2
            handleDrawClick(world.first, world.second, finishPolygon)
        }
    }

    private fun handleDrawClick(x: Double, y: Double, finishPolygon: Boolean) {
        if (drawMode == DrawMode.LINE) {
            handleLineClick(x, y)
        } else if (drawMode == DrawMode.POLYGON) {
            handlePolygonClick(x, y, finishPolygon)
        }
    }

    private fun handleLineClick(x: Double, y: Double) {
        if (lineStartWorld == null) {
            lineStartWorld = x to y
            return
        }
        val start = lineStartWorld!!
        val line = GISLine(start.first, start.second, x, y, Color.rgb(220, 60, 60), 2.0)
        userLayer?.addLine(line)
        lineStartWorld = null
        linePreviewWorld = null
        render()
    }

    private fun handlePolygonClick(x: Double, y: Double, finishPolygon: Boolean) {
        polygonPoints.add(x to y)
        if (!finishPolygon || polygonPoints.size < 3) return
        val count = polygonPoints.size
        val xPoints = DoubleArray(count)
        val yPoints = DoubleArray(count)
        for (i in 0 until count) {
            xPoints[i] = polygonPoints[i].first
            yPoints[i] = polygonPoints[i].second
        }
        val polygon = GISPolygon(
            xPoints,
            yPoints,
            Color.rgb(80, 140, 220, 0.5),
            Color.rgb(40, 90, 180),
            2.0
        )
        userLayer?.addPolygon(polygon)
        polygonPoints.clear()
        polygonPreviewWorld = null
        render()
    }

    private fun screenToWorld(x: Double, y: Double): Pair<Double, Double>? {
        return try {
            val inv = viewTransform.createInverse()
            val point = inv.transform(x, y)
            point.x to point.y
        } catch (ex: Exception) {
            null
        }
    }

    private fun renderOverlay(g: GraphicsContext) {
        if (drawMode == DrawMode.LINE && lineStartWorld != null && linePreviewWorld != null) {
            val start = lineStartWorld!!
            val end = linePreviewWorld!!
            g.stroke = Color.rgb(220, 60, 60, 0.6)
            g.lineWidth = 1.5
            g.strokeLine(start.first, start.second, end.first, end.second)
        } else if (drawMode == DrawMode.POLYGON && polygonPoints.isNotEmpty() && polygonPreviewWorld != null) {
            g.stroke = Color.rgb(40, 90, 180, 0.6)
            g.lineWidth = 1.5
            var prev = polygonPoints[0]
            for (i in 1 until polygonPoints.size) {
                val curr = polygonPoints[i]
                g.strokeLine(prev.first, prev.second, curr.first, curr.second)
                prev = curr
            }
            val preview = polygonPreviewWorld!!
            g.strokeLine(prev.first, prev.second, preview.first, preview.second)
        }
    }
}

