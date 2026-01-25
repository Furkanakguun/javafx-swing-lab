package org.example.swingnodelab

import javafx.application.Application
import javafx.embed.swing.SwingNode
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.HBox
import javafx.scene.layout.BorderPane
import javafx.geometry.Insets
import javafx.stage.Stage
import org.example.common.DrawMode
import org.example.viewer.MapViewerPanel
import javax.swing.SwingUtilities

class HelloApplication : Application() {
    override fun start(stage: Stage) {
        val root = BorderPane()
        val swingNode = SwingNode()

        val mapViewer = MapViewerPanel()
        val statusLabel = Label("Refresh: 0.0 fps | CPU: N/A | Memory: 0/0 MB")

        SwingUtilities.invokeLater {
            swingNode.content = mapViewer
        }

        root.center = swingNode
        root.bottom = createControlPanel(mapViewer, statusLabel)
        val scene = Scene(root, 1200.0, 800.0)
        stage.title = "SwingNode Map Viewer"
        stage.scene = scene
        stage.show()

        val fxMonitor = FxPerformanceMonitor(statusLabel)
        fxMonitor.start()

        stage.setOnCloseRequest {
            fxMonitor.stop()
        }
    }

    private fun createControlPanel(mapViewer: MapViewerPanel, statusLabel: Label): HBox {
        val panButton = Button("Pan")
        val lineButton = Button("Create Line")
        val polygonButton = Button("Create Polygon")
        val clearButton = Button("Clear Map")
        val zoomInButton = Button("Zoom +")
        val zoomOutButton = Button("Zoom -")
        val resetButton = Button("Reset")

        panButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.setDrawMode(DrawMode.NONE) }
        }
        lineButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.setDrawMode(DrawMode.LINE) }
        }
        polygonButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.setDrawMode(DrawMode.POLYGON) }
        }
        clearButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.clearUserShapes() }
        }
        zoomInButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.zoomIn() }
        }
        zoomOutButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.zoomOut() }
        }
        resetButton.setOnAction {
            SwingUtilities.invokeLater { mapViewer.resetView() }
        }

        val box = HBox(8.0, panButton, lineButton, polygonButton, clearButton, zoomInButton, zoomOutButton, resetButton, statusLabel)
        box.padding = Insets(8.0)
        return box
    }
}
  
