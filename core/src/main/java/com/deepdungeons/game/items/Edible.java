package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Vector2d;

// Food or drink
public class Edible extends Item {
  private final int points;
  private static final double SIZE_KOEF = 0.5;

  public static void setSounds(String eating_path, String drinking_path) {
    eating = Gdx.audio.newSound(Gdx.files.internal(eating_path));
    drinking = Gdx.audio.newSound(Gdx.files.internal(drinking_path));
  }

  private static Sound eating;
  private static Sound drinking;

  public Edible(String path_to_texture, Tier tier, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name, path_to_texture);
    this.pos = new Vector2d();
    this.is_texture_from_file = true;
    this.points = points;
    this.sound = is_drink ? drinking : eating;
  
    updateSize(SIZE_KOEF);
  }

  public Edible(Pixmap map, Tier tier, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name, map);
    this.pos = new Vector2d();
    this.points = points;
    this.sound = is_drink ? drinking : eating;

    updateSize(SIZE_KOEF);
  }

  public int getPoints() {
    return points;
  }

  @Override
  public Item clone() {
    Edible item = new Edible(getImage(), getTier(), getName(), getType() == Type.Drink, points);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}