package org.example.common

object GISTestConfig {
    const val SHAPES_PER_LAYER = 2000
    const val LAYER_COUNT = 5
    const val TOTAL_SHAPES = SHAPES_PER_LAYER * LAYER_COUNT

    const val CANVAS_WIDTH = 1200
    const val CANVAS_HEIGHT = 800

    const val WORLD_MIN_X = -1000.0
    const val WORLD_MAX_X = 1000.0
    const val WORLD_MIN_Y = -1000.0
    const val WORLD_MAX_Y = 1000.0

    const val METRICS_UPDATE_INTERVAL_MS = 100

    const val ENABLE_ANTIALIASING = true
    const val USE_WRITABLE_IMAGE = true

    const val INITIAL_SCALE = 0.3
    const val MIN_SCALE = 0.05
    const val MAX_SCALE = 5.0
    const val ZOOM_FACTOR = 1.1
}

