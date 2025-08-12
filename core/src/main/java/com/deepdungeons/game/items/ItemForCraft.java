package com.deepdungeons.game.items;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Vector2d;

public class ItemForCraft extends Item {

  private static final double SIZE_KOEF = 0.5;

  public ItemForCraft(String path_to_texture, Tier tier, String name) {
    super(Type.ForCraft, tier, name, path_to_texture);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }

  public ItemForCraft(Pixmap map, Tier tier, String name) {
    super(Type.ForCraft, tier, name, map);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }
  
  @Override
  public Item clone() {
    ItemForCraft item = new ItemForCraft(getImage(), getTier(), getName());
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}