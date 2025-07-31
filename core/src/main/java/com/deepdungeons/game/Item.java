package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Pixmap;

public class Item {
  protected Pixmap image;

  protected Point pos;
  protected Point size;

  private final int id;

  private static int current_id = 1;

  private final Type type;

  public enum Type {
    None, Key
  }

  protected Item(Type type) {
    this.type = type;
    this.id = current_id;
    ++current_id;
  }

  public void update() {}

  public final void draw(Pixmap dest) {
    dest.drawPixmap(image, pos.x, pos.y);
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

  public final Point getPos() {
    return pos;
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
