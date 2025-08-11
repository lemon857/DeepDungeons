package com.deepdungeons.game.items;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Vector2d;

public class CommonItemForCraft extends Item {

  public CommonItemForCraft(String path_to_texture, String name) {
    super(Type.ForCraft, Tier.Common, name);
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
    this.pos = new Vector2d();
    this.is_texture_from_file = true;
  }

  public CommonItemForCraft(String path_to_texture, String name, Vector2d pos) {
    super(Type.ForCraft, Tier.Common, name);
    this.pos = pos;
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
    this.is_texture_from_file = true;
  }

  public CommonItemForCraft(Pixmap map, String name) {
    super(Type.ForCraft, Tier.Common, name);
    this.pos = new Vector2d();
    this.image = new Pixmap(map.getPixels());
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
  }

  public CommonItemForCraft(Pixmap map, String name, Vector2d pos) {
    super(Type.ForCraft, Tier.Common, name);
    this.pos = pos;
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.texture = new Texture(this.image);
    this.size = new Vector2d(this.image.getWidth(), this.image.getHeight());
  }
  
  @Override
  public Item clone() {
    CommonItemForCraft item = new CommonItemForCraft(getImage(), getName(), this.pos);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }
}