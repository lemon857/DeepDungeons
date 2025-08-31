package com.deepdungeons.game.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public final class SimpleEffect extends Effect {

  private final Texture negative_texture;
  private final Pixmap negative_image;

  public SimpleEffect(String path_to_positive_texture, String path_to_negative_texture, String name) {
    super(path_to_positive_texture, name);

    this.negative_image = new Pixmap(Gdx.files.internal(path_to_negative_texture));
    this.negative_texture = new Texture(negative_image);
    this.positive_sign = 1;
  }

  public SimpleEffect(Pixmap positive_map, Pixmap negative_map, String name) {
    super(positive_map, name);

    this.negative_image = new Pixmap(negative_map.getWidth(), negative_map.getHeight(), negative_map.getFormat());
    this.negative_image.drawPixmap(negative_map, 0, 0);
    this.negative_texture = new Texture(negative_image);
    this.positive_sign = 1;
  }
  
  @Override
  protected void applyLevel(int level) {
    change_func.accept(getChangeKoef(level));
  }

  @Override
  public Texture getTexture() {
    if (current_level < 0) {
      return negative_texture;
    }

    return positive_texture;
  }

  @Override
  protected double getChangeKoef(int level) {
    if (level > 0) {
      return 1.05 + level * 0.05;
    } else if (level < 0) {
      return 0.95 + level * 0.05;
    }
    return 1;
  }

  @Override
  public Effect clone() {
    SimpleEffect effect = new SimpleEffect(positive_image, negative_image, getName());
    
    if (is_player) {
      effect.setPlayerFunc(change_func_sig_player);
    } else {
      effect.setCharacterFunc(change_func_sig_character);
    }

    return effect;
  }
}