package com.deepdungeons.game.utils;

public class Point {
  public int x;
  public int y;

  public Point() {
    this.x = 0;
    this.y = 0;
  }

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public Point(Point point) {
    this.x = point.x;
    this.y = point.y;
  }

  public Point(Vector2d point) {
    this.x = (int)point.x;
    this.y = (int)point.y;
  }

  public final void print() {
    System.out.println("X: " + x + " Y: " + y);
  }

  public static Point sum(Point a, Point b) {
    return new Point(a.x + b.x, a.y + b.y);
  }
  public static Point sub(Point a, Point b) {
    return new Point(a.x - b.x, a.y - b.y);
  }
  public static Point mul(Point a, int b) {
    return new Point(a.x * b, a.y * b);
  }
  public static Point div(Point a, int b) {
    return new Point(a.x / b, a.y / b);
  }
  public static double distance(Point a, Point b) {
    return Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
  }
  public static double distance(Vector2d a, Point b) {
    return Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Point)) return false;
    Point other = (Point) obj;
    return this.x == other.x && this.y == other.y;
  }
  @Override
  public int hashCode() {
    return 500 * x + y;
  }
}