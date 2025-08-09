package com.deepdungeons.game.utils;

public class Vector2d {
  public double x;
  public double y;

  public Vector2d() {
    this.x = 0;
    this.y = 0;
  }

  public Vector2d(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  public Vector2d(Vector2d point) {
    this.x = point.x;
    this.y = point.y;
  }

  public Vector2d(Vector2d start, Vector2d end) {
    this.x = end.x - start.x;
    this.y = end.y - start.y;
  }

  public Vector2d(Point point) {
    this.x = point.x;
    this.y = point.y;
  }

  public double length() {
    return Math.sqrt((x) * (x) + (y) * (y));
  }

  public void normalize() {
    double len = length();
    x /= len;
    y /= len;
  }

  public static Vector2d sum(Vector2d a, Vector2d b) {
    return new Vector2d(a.x + b.x, a.y + b.y);
  }
  public static Vector2d sum(Point a, Vector2d b) {
    return new Vector2d(a.x + b.x, a.y + b.y);
  }
  public static Vector2d sum(Vector2d a, Point b) {
    return new Vector2d(a.x + b.x, a.y + b.y);
  }
  public static Vector2d sub(Vector2d a, Vector2d b) {
    return new Vector2d(a.x - b.x, a.y - b.y);
  }
  public static Vector2d mul(Vector2d a, double b) {
    return new Vector2d(a.x * b, a.y * b);
  }
  public static Vector2d div(Vector2d a, double b) {
    return new Vector2d(a.x / b, a.y / b);
  }
  public static double distance(Vector2d a, Vector2d b) {
    return Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
  }
  public static double angle(Vector2d a, Vector2d b) {
    return Math.acos((a.x * b.x + a.y * b.y) / (a.length() * b.length()));
  }
}