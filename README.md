# JavaFX - Swing and SwingNode

A performance comparison study for migrating large Swing-based GIS applications to JavaFX using SwingNode.

## Overview

This project benchmarks three different approaches for rendering complex graphics operations:

1. **Pure Swing** - Traditional Swing application using Graphics2D, BufferedImage, and AffineTransform
2. **SwingNode Hybrid** - JavaFX application with Swing rendering component embedded via SwingNode
3. **Pure JavaFX** - Native JavaFX implementation using Canvas, GraphicsContext, and Transform API

The goal is to evaluate whether SwingNode is a viable strategy for incremental migration of existing Swing applications, or if a complete rewrite is necessary.

## Key Questions

- How does Graphics2D rendering perform inside SwingNode?
- What is the overhead of thread coordination between JavaFX and Swing EDT?
- Are there compatibility issues with AffineTransform or BufferedImage in SwingNode?
- What is the performance difference between Swing and JavaFX rendering?

## License

This is a research project for evaluating migration strategies.

