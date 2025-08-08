package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Point;

public class CommonItemForCraft extends Item {

  public CommonItemForCraft(String path_to_texture, String name) {
    super(Type.ForCraft, Tier.Common, name);
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Point(this.image.getWidth(), this.image.getHeight());
    this.is_texture_from_file = true;
  }

  public CommonItemForCraft(String path_to_texture, String name, Point pos) {
    super(Type.ForCraft, Tier.Common, name);
    this.pos = pos;
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Point(this.image.getWidth(), this.image.getHeight());
    this.is_texture_from_file = true;
  }

  public CommonItemForCraft(Pixmap map, String name, Point pos) {
    super(Type.ForCraft, Tier.Common, name);
    this.pos = pos;
    this.image = new Pixmap(map.getPixels());
    this.texture = new Texture(this.image);
    this.size = new Point(this.image.getWidth(), this.image.getHeight());
  }

  public CommonItemForCraft(CommonItemForCraft other, Point pos) {
    super(Type.ForCraft, Tier.Common, other.getName());
    this.pos = pos;
    this.size = other.size;
    this.is_texture_from_file = other.is_texture_from_file;
    this.image = new Pixmap(other.image.getWidth(), other.image.getHeight(), other.image.getFormat());
    this.image.drawPixmap(other.image, 0, 0);
    this.texture = new Texture(this.image);
  }
}