package com.deepdungeons.game.items;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.Point;
import com.deepdungeons.game.Room;

public class Item {
  protected Pixmap image;

  protected Texture texture;

  protected Point pos;
  protected Point size;

  private final int id;

  private static int current_id = 1;

  private final Type type;

  private final String name;

  public enum Type {
    None, Key, Weapon
  }

  protected Item(Type type, String name) {
    this.type = type;
    this.name = name;
    this.id = current_id;
    ++current_id;
  }

  public void update(double delta) {}

  protected void generateImage() {
    image = new Pixmap(1, 1, Pixmap.Format.RGB888);

    texture = new Texture(image);
  }

  public final void draw(SpriteBatch batch) {
    batch.draw(texture, (float)pos.x, (float)pos.y + (float)size.y, (float)size.x, -(float)size.y);
  }

  public final Pixmap getImage() {
    return image;
  }

  public final int getId() {
    return id;
  }

  public final Type getType() {
    return type;
  }

  public final String getName() {
    return name;
  }

  public final Point getPos() {
    return pos;
  }

  // For text drawing
  public final Point getCenterPos() {
    return Point.sum(pos, Point.div(size, 2));
  }

  public final Point getSize() {
    return size;
  }

  public final void setPos(Point new_pos) {
    pos = Point.sum(new_pos, Point.div(size, 2));
    if (pos.x + size.x > Room.END_BORDER) {
      pos.x = Room.END_BORDER - size.x - 1;
    }
    if (pos.y + size.y > Room.END_BORDER) {
      pos.y = Room.END_BORDER - size.y - 1;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Item)) return false;
    Item other = (Item) obj;
    return this.id == other.id;
  }
  @Override
  public int hashCode() {
    return id;
  }
}
