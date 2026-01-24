package org.example.viewer;

import org.example.common.GISTestConfig;
import org.example.model.Layer;
import org.example.model.RandomShapeFactory;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class MapViewerPanel extends JPanel {
    
    private final List<Layer> layers;
    private final Random random;
    private final AffineTransform viewTransform;
    
    public MapViewerPanel() {
        this.layers = new ArrayList<>();
        this.random = new Random();
        this.viewTransform = new AffineTransform();
        
        setPreferredSize(new Dimension(GISTestConfig.CANVAS_WIDTH, GISTestConfig.CANVAS_HEIGHT));
        setBackground(Color.WHITE);

        initializeViewTransform();
        generateRandomLayers();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        if (GISTestConfig.ENABLE_ANTIALIASING) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(viewTransform);
        renderLayers(g2d);
        g2d.setTransform(originalTransform);

        g2d.setColor(Color.BLACK);
        g2d.drawString("Layers: " + layers.size(), 10, 20);
        g2d.drawString("Total shapes: " + getTotalShapeCount(), 10, 40);
        g2d.drawString("Canvas size: " + getWidth() + "x" + getHeight(), 10, 60);
    }
    
    public int getLayerCount() {
        return layers.size();
    }

    public int getTotalShapeCount() {
        int total = 0;
        for (Layer layer : layers) {
            total += layer.getTotalShapeCount();
        }
        return total;
    }

    private void initializeViewTransform() {
        viewTransform.setToIdentity();
        viewTransform.translate(GISTestConfig.CANVAS_WIDTH / 2.0, GISTestConfig.CANVAS_HEIGHT / 2.0);
        viewTransform.scale(GISTestConfig.INITIAL_SCALE, GISTestConfig.INITIAL_SCALE);
    }

    private void generateRandomLayers() {
        layers.clear();
        for (int i = 0; i < GISTestConfig.LAYER_COUNT; i++) {
            Layer layer = new Layer("Layer-" + (i + 1));
            generateRandomShapes(layer);
            layers.add(layer);
        }
    }

    private void generateRandomShapes(Layer layer) {
        int perLayer = GISTestConfig.SHAPES_PER_LAYER;
        int pointsCount = perLayer / 3;
        int linesCount = perLayer / 3;
        int polygonsCount = perLayer - pointsCount - linesCount;

        for (int i = 0; i < pointsCount; i++) {
            layer.addPoint(RandomShapeFactory.randomPoint(random));
        }
        for (int i = 0; i < linesCount; i++) {
            layer.addLine(RandomShapeFactory.randomLine(random));
        }
        for (int i = 0; i < polygonsCount; i++) {
            layer.addPolygon(RandomShapeFactory.randomPolygon(random));
        }
    }

    private void renderLayers(Graphics2D g2d) {
        for (Layer layer : layers) {
            if (!layer.isVisible()) {
                continue;
            }
            renderPoints(g2d, layer);
            renderLines(g2d, layer);
            renderPolygons(g2d, layer);
        }
    }

    private void renderPoints(Graphics2D g2d, Layer layer) {
        layer.getPoints().forEach(point -> {
            int size = point.getSize();
            int x = (int) Math.round(point.getX() - size / 2.0);
            int y = (int) Math.round(point.getY() - size / 2.0);
            g2d.setColor(point.getColor());
            g2d.fillOval(x, y, size, size);
        });
    }

    private void renderLines(Graphics2D g2d, Layer layer) {
        layer.getLines().forEach(line -> {
            g2d.setColor(line.getColor());
            g2d.setStroke(new BasicStroke(line.getStrokeWidth()));
            g2d.drawLine(
                (int) Math.round(line.getX1()),
                (int) Math.round(line.getY1()),
                (int) Math.round(line.getX2()),
                (int) Math.round(line.getY2())
            );
        });
    }

    private void renderPolygons(Graphics2D g2d, Layer layer) {
        layer.getPolygons().forEach(polygon -> {
            int count = polygon.getPointCount();
            int[] xPoints = new int[count];
            int[] yPoints = new int[count];
            for (int i = 0; i < count; i++) {
                xPoints[i] = (int) Math.round(polygon.getXPoints()[i]);
                yPoints[i] = (int) Math.round(polygon.getYPoints()[i]);
            }
            Polygon awtPolygon = new Polygon(xPoints, yPoints, count);
            g2d.setColor(polygon.getFillColor());
            g2d.fillPolygon(awtPolygon);
            g2d.setColor(polygon.getStrokeColor());
            g2d.setStroke(new BasicStroke(polygon.getStrokeWidth()));
            g2d.drawPolygon(awtPolygon);
        });
    }
}

