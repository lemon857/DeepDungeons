package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Vector2d;

public class Coin extends Item {

  private static final double SIZE_KOEF = 0.4;

  public Coin(String path_to_texture, String path_to_sound) {
    super(Item.Type.Coin, Item.Tier.Common, "coin", path_to_texture);
    this.pos = new Vector2d();
    this.sound = Gdx.audio.newSound(Gdx.files.internal(path_to_sound));
    updateSize(SIZE_KOEF);
  }
  
  public Coin(Pixmap map, Sound sound) {
    super(Item.Type.Coin, Item.Tier.Common, "coin", map);
    this.pos = new Vector2d();
    this.sound = sound;
    updateSize(SIZE_KOEF);
  }

  @Override
  public Item clone() {
    Coin item = new Coin(getImage(), sound);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}
