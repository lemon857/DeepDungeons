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
  public void update(double delta) {
    if (!is_active) return;

    if (levels.peek().getDuration() != -1) {
      timer += delta;

      if (timer >= levels.peek().getDuration()) {
        timer = 0;
        
        removeCurrentLevel();
      }
    }
  }

  @Override
  public void addLevel(int level, double duration) {
    System.out.println("[Effect] Add level: " + level);

    is_active = true;
    current_level = sumLevels(current_level, level);
    current_sum += level;

    levels.add(new LevelPair(duration, level));

    change_func.accept(getChangeKoef(current_level));
  }

  @Override
  public void removeCurrentLevel() {
    current_sum -= levels.peek().getPrevLevel();
    current_level = getCorrectLevel(current_sum);

    levels.remove();

    if (levels.isEmpty()) {
      change_func.accept(getChangeKoef(0));
      current_level = 0;
      current_sum = 0;
      is_active = false;
    } else {
      change_func.accept(getChangeKoef(current_level));
    }
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
    return new SimpleEffect(positive_image, negative_image, getName());
  }
}