package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Key extends Item {
  private static final int KEY_WIDTH = 8;
  private static final int KEY_HEIGHT = 4;

  private  static final Point POS_OFFSET = new Point(-KEY_WIDTH / 2, -KEY_HEIGHT / 2);

  private final int key;
  private final Color color;

  public Key(int key, Color color, Point pos) {
    this.key = key;
    this.color = color;
    this.pos = pos;
    this.size = new Point(KEY_WIDTH, KEY_HEIGHT);
    generateImage();
  }

  public int getKey() {
    return key;
  }

  public Point getCenterPos() {
    return Point.sum(pos, POS_OFFSET);
  }

  public final void generateImage() {
    image = new Pixmap(size.x, size.y, Pixmap.Format.RGBA8888);

    image.setColor(1, 1, 1, 1);

    image.drawPixel(1, 0);
    image.drawPixel(2, 0);

    image.drawPixel(1, 3);
    image.drawPixel(2, 3);

    image.drawPixel(0, 1);
    image.drawPixel(0, 2);

    image.drawPixel(3, 1);
    image.drawPixel(3, 2);

    image.drawLine(4, 1, 7, 1);

    image.setColor(color);

    image.drawPixel(5, 2);
    image.drawPixel(6, 2);
  }
}
