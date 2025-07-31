package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Pixmap;

public class Item {
  protected Pixmap image;

  protected Point pos;
  protected Point size;

  private final int id;

  private static int current_id = 1;

  public Item() {
    this.id = current_id;
    ++current_id;
  }

  public void update() {
    
  }

  public final void draw(Pixmap dest) {
    dest.drawPixmap(image, pos.x, pos.y);
  }

  public final int getId() {
    return id;
  }
}
