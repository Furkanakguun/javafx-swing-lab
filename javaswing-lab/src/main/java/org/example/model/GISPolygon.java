package org.example.model;

import java.awt.Color;

public class GISPolygon {
    
    private final double[] xPoints;
    private final double[] yPoints;
    private final Color fillColor;
    private final Color strokeColor;
    private final float strokeWidth;
    
    public GISPolygon(double[] xPoints, double[] yPoints, Color fillColor, Color strokeColor, float strokeWidth) {
        this.xPoints = xPoints;
        this.yPoints = yPoints;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.strokeWidth = strokeWidth;
    }
    
    public double[] getXPoints() {
        return xPoints;
    }
    
    public double[] getYPoints() {
        return yPoints;
    }
    
    public int getPointCount() {
        return xPoints.length;
    }
    
    public Color getFillColor() {
        return fillColor;
    }
    
    public Color getStrokeColor() {
        return strokeColor;
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
}

