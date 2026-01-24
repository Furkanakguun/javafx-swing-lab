package org.example.model;

import org.example.common.GISTestConfig;

import java.awt.Color;
import java.util.Random;

public final class RandomShapeFactory {
    
    private RandomShapeFactory() {
    }
    
    public static GISPoint randomPoint(Random random) {
        double x = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X);
        double y = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y);
        Color color = randomColor(random, 120, 220);
        int size = 3 + random.nextInt(5);
        return new GISPoint(x, y, color, size);
    }
    
    public static GISLine randomLine(Random random) {
        double x1 = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X);
        double y1 = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y);
        double x2 = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X);
        double y2 = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y);
        Color color = randomColor(random, 80, 200);
        float strokeWidth = 1.0f + random.nextFloat() * 2.0f;
        return new GISLine(x1, y1, x2, y2, color, strokeWidth);
    }
    
    public static GISPolygon randomPolygon(Random random) {
        int points = 3 + random.nextInt(4);
        double[] xPoints = new double[points];
        double[] yPoints = new double[points];
        double centerX = randomCoord(random, GISTestConfig.WORLD_MIN_X, GISTestConfig.WORLD_MAX_X);
        double centerY = randomCoord(random, GISTestConfig.WORLD_MIN_Y, GISTestConfig.WORLD_MAX_Y);
        double radius = 20 + random.nextDouble() * 80;
        
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points + random.nextDouble() * 0.3;
            xPoints[i] = centerX + Math.cos(angle) * radius;
            yPoints[i] = centerY + Math.sin(angle) * radius;
        }
        
        Color fillColor = randomColor(random, 80, 180);
        Color strokeColor = randomColor(random, 60, 160);
        float strokeWidth = 1.0f + random.nextFloat() * 2.5f;
        return new GISPolygon(xPoints, yPoints, fillColor, strokeColor, strokeWidth);
    }
    
    private static double randomCoord(Random random, double min, double max) {
        return min + (max - min) * random.nextDouble();
    }
    
    private static Color randomColor(Random random, int min, int max) {
        int r = min + random.nextInt(max - min + 1);
        int g = min + random.nextInt(max - min + 1);
        int b = min + random.nextInt(max - min + 1);
        return new Color(r, g, b);
    }
}

