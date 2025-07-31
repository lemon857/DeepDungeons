package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Pixmap;

enum ItemType {
  None, Key
}

public class Item {
  protected Pixmap image;

  protected Point pos;
  protected Point size;

  private final int id;

  private static int current_id = 1;

  private final ItemType type;

  protected Item(ItemType type) {
    this.type = type;
    this.id = current_id;
    ++current_id;
  }

  public void update() {}

  public final void draw(Pixmap dest) {
    dest.drawPixmap(image, pos.x, pos.y);
  }

  public final int getId() {
    return id;
  }

  public final ItemType getType() {
    return type;
  }

  public final Point getPos() {
    return pos;
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
