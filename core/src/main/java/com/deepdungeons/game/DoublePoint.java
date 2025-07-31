package com.deepdungeons.game;

public class DoublePoint {
  public double x;
  public double y;

  public DoublePoint() {
    this.x = 0;
    this.y = 0;
  }

  public DoublePoint(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public DoublePoint(DoublePoint point) {
    this.x = point.x;
    this.y = point.y;
  }

  public static DoublePoint sum(DoublePoint a, DoublePoint b) {
    return new DoublePoint(a.x + b.x, a.y + b.y);
  }
  public static DoublePoint sum(Point a, DoublePoint b) {
    return new DoublePoint(a.x + b.x, a.y + b.y);
  }
  public static DoublePoint sum(DoublePoint a, Point b) {
    return new DoublePoint(a.x + b.x, a.y + b.y);
  }
  public static DoublePoint sub(DoublePoint a, DoublePoint b) {
    return new DoublePoint(a.x - b.x, a.y - b.y);
  }
}