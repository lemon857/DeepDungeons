package com.deepdungeons.game.items;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Vector2d;

public class Coin extends Item {

  private static final double SIZE_KOEF = 0.4;

  public Coin(String path_to_texture) {
    super(Item.Type.Coin, Item.Tier.Common, "coin", path_to_texture);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }
  
  public Coin(Pixmap map) {
    super(Item.Type.Coin, Item.Tier.Common, "coin", map);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }

  @Override
  public Item clone() {
    Coin item = new Coin(getImage());
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}
