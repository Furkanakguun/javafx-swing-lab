package org.example.common;

public class GISTestConfig {
    
    // Shape and layer configuration
    public static final int SHAPES_PER_LAYER = 2000;
    public static final int LAYER_COUNT = 5;
    public static final int TOTAL_SHAPES = SHAPES_PER_LAYER * LAYER_COUNT;
    
    // Canvas dimensions
    public static final int CANVAS_WIDTH = 1200;
    public static final int CANVAS_HEIGHT = 800;
    
    // World coordinates (for random shape generation)
    public static final double WORLD_MIN_X = -1000.0;
    public static final double WORLD_MAX_X = 1000.0;
    public static final double WORLD_MIN_Y = -1000.0;
    public static final double WORLD_MAX_Y = 1000.0;
    
    // Performance monitoring
    public static final int METRICS_UPDATE_INTERVAL_MS = 100;
    
    // Rendering configuration
    public static final boolean ENABLE_ANTIALIASING = true;
    public static final boolean USE_BUFFERED_IMAGE = true;
    
    // Transform configuration
    public static final double INITIAL_SCALE = 0.3;
    public static final double MIN_SCALE = 0.05;
    public static final double MAX_SCALE = 5.0;
    public static final double ZOOM_FACTOR = 1.1;
    
    private GISTestConfig() {
        // Utility class
    }
}

