package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Vector2d;

// Food or drink
public class Edible extends Item {
  private final int points;

  public Edible(Tier tier, String path_to_texture, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name);
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
    this.pos = new Vector2d();
    this.is_texture_from_file = true;
    this.points = points;
  }

  public Edible(Tier tier, Pixmap map, String name, boolean is_drink, int points) {
    super(is_drink ? Type.Drink : Type.Food, tier, name);
    this.pos = new Vector2d();
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
    this.points = points;
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