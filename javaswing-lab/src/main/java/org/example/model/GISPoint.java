package org.example.model;

import java.awt.Color;

public class GISPoint {
    
    private final double x;
    private final double y;
    private final Color color;
    private final int size;
    
    public GISPoint(double x, double y, Color color, int size) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.size = size;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public Color getColor() {
        return color;
    }
    
    public int getSize() {
        return size;
    }
}

