package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Vector2d;

public class ItemForCraft extends Item {

  private static final double SIZE_KOEF = 0.5;

  public ItemForCraft(Tier tier, String path_to_texture, String name) {
    super(Type.ForCraft, tier, name);
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth() * SIZE_KOEF, this.image.getHeight() * SIZE_KOEF);
    this.pos = new Vector2d();
    this.is_texture_from_file = true;
  }

  public ItemForCraft(Tier tier, Pixmap map, String name) {
    super(Type.ForCraft, tier, name);
    this.pos = new Vector2d();
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth() * SIZE_KOEF, this.image.getHeight() * SIZE_KOEF);
  }
  
  @Override
  public Item clone() {
    ItemForCraft item = new ItemForCraft(getTier(), getImage(), getName());
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}