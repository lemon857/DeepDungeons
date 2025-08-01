package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Mob {
  protected Pixmap image;

  protected DoublePoint pos;
  protected DoublePoint size;

  protected Texture texture;

  private final int id;

  private static int current_id = 1;

  public Mob() {
    this.id = current_id;
    ++current_id;
  }

  public void update(double delta) {}
  protected void generateImage() {
    image = new Pixmap(1, 1, Pixmap.Format.RGB888);

    texture = new Texture(image);
  }

  public final int getId() {
    return id;
  }

  public final DoublePoint getPos() {
    return pos;
  }

  public final Pixmap getImage() {
    return image;
  }

  public final void draw(SpriteBatch batch) {
    batch.draw(texture, (float)pos.x, (float)pos.y, (float)size.x, (float)size.y);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Mob)) return false;
    Mob other = (Mob) obj;
    return this.id == other.id;
  }
  @Override
  public int hashCode() {
    return id;
  }
}