package com.deepdungeons.game.items;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Vector2d;

// Food or drink
public class Edible extends Item {
  private final int points;
  private static final double SIZE_KOEF = 0.5;

  public Edible(Tier tier, String path_to_texture, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name, path_to_texture);
    this.pos = new Vector2d();
    this.is_texture_from_file = true;
    this.points = points;
    updateSize(SIZE_KOEF);
  }

  public Edible(Tier tier, Pixmap map, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name, map);
    this.pos = new Vector2d();
    this.points = points;
    updateSize(SIZE_KOEF);
  }

  public int getPoints() {
    return points;
  }

  @Override
  public Item clone() {
    Edible item = new Edible(getTier(), getImage(), getName(), getType() == Type.Drink, points);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}