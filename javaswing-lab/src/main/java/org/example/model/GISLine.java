package org.example.model;

import java.awt.Color;

public class GISLine {
    
    private final double x1;
    private final double y1;
    private final double x2;
    private final double y2;
    private final Color color;
    private final float strokeWidth;
    
    public GISLine(double x1, double y1, double x2, double y2, Color color, float strokeWidth) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.strokeWidth = strokeWidth;
    }
    
    public double getX1() {
        return x1;
    }
    
    public double getY1() {
        return y1;
    }
    
    public double getX2() {
        return x2;
    }
    
    public double getY2() {
        return y2;
    }
    
    public Color getColor() {
        return color;
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
}

