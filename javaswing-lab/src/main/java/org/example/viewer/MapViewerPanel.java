package org.example.viewer;

import org.example.common.DrawMode;
import org.example.common.GISTestConfig;
import org.example.model.Layer;
import org.example.model.GISLine;
import org.example.model.GISPolygon;
import org.example.model.RandomShapeFactory;
import org.example.monitor.SwingPerformanceMonitor;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

public class MapViewerPanel extends JPanel {
    
    private final List<Layer> layers;
    private Layer userLayer;
    private final Random random;
    private final AffineTransform viewTransform;
    private BufferedImage offscreenBuffer;
    private boolean bufferDirty;
    private Point lastMousePoint;
    private double currentScale;
    private DrawMode drawMode;
    private Point2D.Double lineStartWorld;
    private Point2D.Double linePreviewWorld;
    private Point2D.Double polygonPreviewWorld;
    private final List<Point2D.Double> polygonPoints;
    private SwingPerformanceMonitor performanceMonitor;
    
    public MapViewerPanel() {
        this.layers = new ArrayList<>();
        this.random = new Random();
        this.viewTransform = new AffineTransform();
        this.bufferDirty = true;
        this.currentScale = GISTestConfig.INITIAL_SCALE;
        this.drawMode = DrawMode.NONE;
        this.polygonPoints = new ArrayList<>();
        
        setPreferredSize(new Dimension(GISTestConfig.CANVAS_WIDTH, GISTestConfig.CANVAS_HEIGHT));
        setBackground(Color.WHITE);

        initializeViewTransform();
        generateRandomLayers();
        installPanMouseHandlers();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        if (GISTestConfig.ENABLE_ANTIALIASING) {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }

        ensureBuffer();
        if (bufferDirty) {
            renderToBuffer();
            bufferDirty = false;
        }
        if (offscreenBuffer != null) {
            g2d.drawImage(offscreenBuffer, 0, 0, null);
        }
        
        renderOverlay(g2d);

        if (performanceMonitor != null) {
            performanceMonitor.onFrameRendered();
        }

        g2d.setColor(Color.BLACK);
        g2d.drawString("Layers: " + layers.size(), 10, 20);
        g2d.drawString("Total shapes: " + getTotalShapeCount(), 10, 40);
        g2d.drawString("Screen size: " + getWidth() + "x" + getHeight(), 10, 60);
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
        viewTransform.scale(currentScale, currentScale);
    }

    private void installPanMouseHandlers() {
        MouseAdapter adapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (drawMode != DrawMode.NONE) {
                    return;
                }
                lastMousePoint = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawMode != DrawMode.NONE) {
                    return;
                }
                if (lastMousePoint == null) {
                    lastMousePoint = e.getPoint();
                    return;
                }
                int dx = e.getX() - lastMousePoint.x;
                int dy = e.getY() - lastMousePoint.y;
                viewTransform.translate(dx, dy);
                lastMousePoint = e.getPoint();
                bufferDirty = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                lastMousePoint = null;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (drawMode == DrawMode.NONE) {
                    return;
                }
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                boolean finishPolygon = e.getClickCount() >= 2;
                handleDrawClick(e.getPoint(), finishPolygon);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (drawMode == DrawMode.LINE) {
                    if (lineStartWorld == null) {
                        linePreviewWorld = null;
                        return;
                    }
                    Point2D.Double worldPoint = screenToWorld(e.getPoint());
                    if (worldPoint == null) {
                        return;
                    }
                    linePreviewWorld = worldPoint;
                    repaint();
                } else if (drawMode == DrawMode.POLYGON) {
                    if (polygonPoints.isEmpty()) {
                        polygonPreviewWorld = null;
                        return;
                    }
                    Point2D.Double worldPoint = screenToWorld(e.getPoint());
                    if (worldPoint == null) {
                        return;
                    }
                    polygonPreviewWorld = worldPoint;
                    repaint();
                }
            }
        };
        addMouseListener(adapter);
        addMouseMotionListener(adapter);
    }

    public void zoomIn() {
        applyZoom(GISTestConfig.ZOOM_FACTOR);
    }

    public void zoomOut() {
        applyZoom(1.0 / GISTestConfig.ZOOM_FACTOR);
    }

    public void resetView() {
        currentScale = GISTestConfig.INITIAL_SCALE;
        initializeViewTransform();
        bufferDirty = true;
        repaint();
    }

    private void applyZoom(double factor) {
        double newScale = currentScale * factor;
        if (newScale < GISTestConfig.MIN_SCALE || newScale > GISTestConfig.MAX_SCALE) {
            return;
        }
        currentScale = newScale;
        Point center = new Point(getWidth() / 2, getHeight() / 2);
        viewTransform.translate(center.x, center.y);
        viewTransform.scale(factor, factor);
        viewTransform.translate(-center.x, -center.y);
        bufferDirty = true;
        repaint();
    }

    public void setDrawMode(DrawMode mode) {
        this.drawMode = mode;
        this.lineStartWorld = null;
        this.linePreviewWorld = null;
        this.polygonPreviewWorld = null;
        this.polygonPoints.clear();
    }

    public void setPerformanceMonitor(SwingPerformanceMonitor monitor) {
        this.performanceMonitor = monitor;
    }

    public void clearUserShapes() {
        if (userLayer != null) {
            userLayer.clear();
        }
        this.lineStartWorld = null;
        this.linePreviewWorld = null;
        this.polygonPreviewWorld = null;
        this.polygonPoints.clear();
        bufferDirty = true;
        repaint();
    }

    private void handleDrawClick(Point screenPoint, boolean finishPolygon) {
        Point2D.Double worldPoint = screenToWorld(screenPoint);
        if (worldPoint == null) {
            return;
        }
        if (drawMode == DrawMode.LINE) {
            handleLineClick(worldPoint);
        } else if (drawMode == DrawMode.POLYGON) {
            handlePolygonClick(worldPoint, finishPolygon);
        }
    }

    private void handleLineClick(Point2D.Double worldPoint) {
        if (lineStartWorld == null) {
            lineStartWorld = worldPoint;
            return;
        }
        GISLine line = new GISLine(
            lineStartWorld.x,
            lineStartWorld.y,
            worldPoint.x,
            worldPoint.y,
            new Color(220, 60, 60),
            2.0f
        );
        userLayer.addLine(line);
        lineStartWorld = null;
        linePreviewWorld = null;
        bufferDirty = true;
        repaint();
    }

    private void handlePolygonClick(Point2D.Double worldPoint, boolean finishPolygon) {
        polygonPoints.add(worldPoint);
        if (!finishPolygon || polygonPoints.size() < 3) {
            return;
        }
        int count = polygonPoints.size();
        double[] xPoints = new double[count];
        double[] yPoints = new double[count];
        for (int i = 0; i < count; i++) {
            xPoints[i] = polygonPoints.get(i).x;
            yPoints[i] = polygonPoints.get(i).y;
        }
        GISPolygon polygon = new GISPolygon(
            xPoints,
            yPoints,
            new Color(80, 140, 220, 120),
            new Color(40, 90, 180),
            2.0f
        );
        userLayer.addPolygon(polygon);
        polygonPoints.clear();
        polygonPreviewWorld = null;
        bufferDirty = true;
        repaint();
    }

    private void renderOverlay(Graphics2D g2d) {
        AffineTransform originalTransform = g2d.getTransform();
        g2d.transform(viewTransform);

        if (drawMode == DrawMode.LINE && lineStartWorld != null && linePreviewWorld != null) {
            g2d.setColor(new Color(220, 60, 60, 160));
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(
                (int) Math.round(lineStartWorld.x),
                (int) Math.round(lineStartWorld.y),
                (int) Math.round(linePreviewWorld.x),
                (int) Math.round(linePreviewWorld.y)
            );
        } else if (drawMode == DrawMode.POLYGON && !polygonPoints.isEmpty() && polygonPreviewWorld != null) {
            g2d.setColor(new Color(40, 90, 180, 160));
            g2d.setStroke(new BasicStroke(1.5f));
            Point2D.Double prev = polygonPoints.get(0);
            for (int i = 1; i < polygonPoints.size(); i++) {
                Point2D.Double curr = polygonPoints.get(i);
                g2d.drawLine(
                    (int) Math.round(prev.x),
                    (int) Math.round(prev.y),
                    (int) Math.round(curr.x),
                    (int) Math.round(curr.y)
                );
                prev = curr;
            }
            g2d.drawLine(
                (int) Math.round(prev.x),
                (int) Math.round(prev.y),
                (int) Math.round(polygonPreviewWorld.x),
                (int) Math.round(polygonPreviewWorld.y)
            );
        }

        g2d.setTransform(originalTransform);
    }

    private Point2D.Double screenToWorld(Point screenPoint) {
        try {
            AffineTransform inverse = viewTransform.createInverse();
            Point2D.Double result = new Point2D.Double();
            inverse.transform(new Point2D.Double(screenPoint.x, screenPoint.y), result);
            return result;
        } catch (Exception ex) {
            return null;
        }
    }

    private void generateRandomLayers() {
        layers.clear();
        for (int i = 0; i < GISTestConfig.LAYER_COUNT; i++) {
            Layer layer = new Layer("Layer-" + (i + 1));
            generateRandomShapes(layer);
            layers.add(layer);
        }
        userLayer = new Layer("User");
        layers.add(userLayer);
        bufferDirty = true;
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

    private void ensureBuffer() {
        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) {
            return;
        }
        if (offscreenBuffer == null || offscreenBuffer.getWidth() != width || offscreenBuffer.getHeight() != height) {
            offscreenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            bufferDirty = true;
        }
    }

    private void renderToBuffer() {
        if (offscreenBuffer == null) {
            return;
        }
        Graphics2D bufferG2d = offscreenBuffer.createGraphics();
        bufferG2d.setColor(getBackground());
        bufferG2d.fillRect(0, 0, offscreenBuffer.getWidth(), offscreenBuffer.getHeight());
        if (GISTestConfig.ENABLE_ANTIALIASING) {
            bufferG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        AffineTransform originalTransform = bufferG2d.getTransform();
        bufferG2d.transform(viewTransform);
        renderLayers(bufferG2d);
        bufferG2d.setTransform(originalTransform);
        bufferG2d.dispose();
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

