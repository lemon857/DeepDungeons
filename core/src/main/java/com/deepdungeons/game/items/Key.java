package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class Key extends Item {
  private static final double SIZE_KOEF = 0.8;

  private int key;

  public Key(String path_to_texture, String path_to_sound) {
    super(Item.Type.Key, Item.Tier.Common,  "key", path_to_texture);
    this.key = 0;
    this.pos = new Vector2d();
    this.sound = Gdx.audio.newSound(Gdx.files.internal(path_to_sound));
    updateSize(SIZE_KOEF);

    generateRandomPos();
  }

  public Key(Pixmap map, Sound sound) {
    super(Item.Type.Key, Item.Tier.Common,  "key", map);
    this.key = 0;
    this.pos = new Vector2d();
    this.sound = sound;
    updateSize(SIZE_KOEF);
    generateRandomPos();
  }

  public void setParams(int key, Color color) {
    this.key = key;

    this.image = Utility.replacePixelsColor(image, Color.WHITE, color);
    this.texture = new Texture(this.image);
  }

  public int getKey() {
    return key;
  }

  @Override
  public Item clone() {
    Key item = new Key(image, sound);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Key)) return false;
    Key other = (Key) obj;
    return getId() == other.getId() && key == other.key;
  }
  @Override
  public int hashCode() {
    return getId() * key;
  }
}
