package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class Layer {
    
    private final String name;
    private final List<GISPoint> points;
    private final List<GISLine> lines;
    private final List<GISPolygon> polygons;
    private boolean visible;
    
    public Layer(String name) {
        this.name = name;
        this.points = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.polygons = new ArrayList<>();
        this.visible = true;
    }
    
    public String getName() {
        return name;
    }
    
    public List<GISPoint> getPoints() {
        return points;
    }
    
    public List<GISLine> getLines() {
        return lines;
    }
    
    public List<GISPolygon> getPolygons() {
        return polygons;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public void addPoint(GISPoint point) {
        points.add(point);
    }
    
    public void addLine(GISLine line) {
        lines.add(line);
    }
    
    public void addPolygon(GISPolygon polygon) {
        polygons.add(polygon);
    }
    
    public void clear() {
        points.clear();
        lines.clear();
        polygons.clear();
    }
    
    public int getTotalShapeCount() {
        return points.size() + lines.size() + polygons.size();
    }
}

