package com.deepdungeons.game.items;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Point;

public class Key extends Item {
  public static final int WIDTH = 8;
  public static final int HEIGHT = 4;

  private final int key;
  private final Color color;

  public Key(int key, Color color, Point pos) {
    super(Item.Type.Key, Item.Tier.Common,  "key");
    this.key = key;
    this.color = color;
    this.pos = pos;
    this.size = new Point(WIDTH, HEIGHT);
    generateImage();
  }

  public int getKey() {
    return key;
  }

  @Override
  protected final void generateImage() {
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

    image.drawLine(4, 2, 7, 2);

    image.setColor(color);

    image.drawPixel(5, 1);
    image.drawPixel(6, 1);
    
    texture = new Texture(image);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Key)) return false;
    Key other = (Key) obj;
    return getId() == other.getId() && key == other.key;
  }
  @Override
  public int hashCode() {
    return getId() * key;
  }
}
