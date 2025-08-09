package com.deepdungeons.game.mobs;

import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.Room;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class Mob {
  protected Pixmap image;

  protected Vector2d pos;
  protected Direction dir;
  protected double speed;

  protected Texture texture;

  protected double health_points;

  protected final Random rand;

  protected Vector2d size;
  protected Point start_border;
  protected Point end_border;

  private final int id;

  private static int current_id = 1;

  private final Tier tier;

  public enum Tier {
    Humble, General, Wicked, Aggressive
  }

  public Mob(Tier tier, double speed) {
    this.tier = tier;
    this.speed = speed;
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

  public final Vector2d getSize() {
    return size;
  }

  public final Pixmap getImage() {
    return image;
  }

  public final void setSize(double width, double height) {
    start_border = Point.sum(Room.START_BORDER, new Point(1, 1));
    end_border = Point.sub(Room.END_BORDER, new Point((int)Math.ceil(width), (int)Math.ceil(height)));
    size = new Vector2d(width, height);
  }
  public final void translate(Vector2d vector) {
    translate(vector.x, vector.y);
  }

  public final void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

    if (pos.x < start_border.x) pos.x = start_border.x;
    else if (pos.x > end_border.x) pos.x = end_border.x;

    if (pos.y < start_border.y) pos.y = start_border.y;
    else if (pos.y > end_border.y) pos.y = end_border.y;

    dir = Utility.getTranslateDirection(x, y);
  }

  public final void draw(SpriteBatch batch) {
    batch.draw(texture, (float)pos.x, (float)pos.y + (float)size.y, (float)size.x, -(float)size.y);
  }

  protected void attack() {
    
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