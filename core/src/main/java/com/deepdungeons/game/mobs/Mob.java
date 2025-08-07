package com.deepdungeons.game.mobs;

import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.Vector2d;

public class Mob {
  protected Pixmap image;

  protected Vector2d pos;
  protected Vector2d size;

  protected Texture texture;

  protected double health_points;

  protected final Random rand;

  private final int id;

  private static int current_id = 1;

  private final Tier tier;

  public enum Tier {
    Humble, General, Wicked, Aggressive
  }

  public Mob(Tier tier) {
    this.tier = tier;
    this.id = current_id;
    ++current_id;
    this.rand = new Random();
  }

  public void update(double delta) {}
  protected void generateImage() {
    image = new Pixmap(1, 1, Pixmap.Format.RGB888);

    texture = new Texture(image);
  }

  public final int getId() {
    return id;
  }

  public final Tier getTier() {
    return tier;
  }

  public final double getHealthPoints() {
    return id;
  }

  public final Vector2d getPos() {
    return pos;
  }

  public final Pixmap getImage() {
    return image;
  }

  public final void draw(SpriteBatch batch) {
    batch.draw(texture, (float)pos.x, (float)pos.y + (float)size.y, (float)size.x, -(float)size.y);
  }

  // True if it dead
  public boolean damage(double dmg) {
    health_points -= dmg;
    return health_points <= 0;
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