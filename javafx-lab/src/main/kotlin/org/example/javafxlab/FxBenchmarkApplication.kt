package org.example.javafxlab

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.geometry.Insets
import org.example.common.DrawMode
import org.example.common.GISTestConfig

class FxBenchmarkApplication : Application() {
    override fun start(stage: Stage) {
        val root = BorderPane()
        val canvas = MapViewerCanvas(GISTestConfig.CANVAS_WIDTH.toDouble(), GISTestConfig.CANVAS_HEIGHT.toDouble())
        root.center = canvas

        val info = Text("Layers: ${GISTestConfig.LAYER_COUNT} | Total shapes: ${canvas.totalShapeCount()}")
        info.fill = Color.BLACK
        info.font = Font.font(14.0)
        BorderPane.setMargin(info, javafx.geometry.Insets(6.0))
        root.top = info

        val statusLabel = Label("Refresh: 0.0 fps | CPU: N/A | Memory: 0/0 MB")
        root.bottom = createControlPanel(canvas, statusLabel)
        val monitor = FxPerformanceMonitor(statusLabel)
        monitor.start()

        val scene = Scene(root, 1200.0, 800.0)
        stage.title = "Pure JavaFX Benchmark"
        stage.scene = scene
        stage.show()

        stage.setOnCloseRequest {
            monitor.stop()
        }
    }

    private fun createControlPanel(canvas: MapViewerCanvas, statusLabel: Label): HBox {
        val panButton = Button("Pan")
        val lineButton = Button("Create Line")
        val polygonButton = Button("Create Polygon")
        val clearButton = Button("Clear Map")
        val zoomInButton = Button("Zoom +")
        val zoomOutButton = Button("Zoom -")
        val resetButton = Button("Reset")

        panButton.setOnAction { canvas.setDrawMode(DrawMode.NONE) }
        lineButton.setOnAction { canvas.setDrawMode(DrawMode.LINE) }
        polygonButton.setOnAction { canvas.setDrawMode(DrawMode.POLYGON) }
        clearButton.setOnAction { canvas.clearUserShapes() }
        zoomInButton.setOnAction { canvas.zoomIn() }
        zoomOutButton.setOnAction { canvas.zoomOut() }
        resetButton.setOnAction { canvas.resetView() }

        val box = HBox(8.0, panButton, lineButton, polygonButton, clearButton, zoomInButton, zoomOutButton, resetButton, statusLabel)
        box.padding = Insets(8.0)
        return box
    }
}

