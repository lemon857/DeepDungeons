package com.deepdungeons.game.effects;

import java.util.HashSet;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.utils.Vector2d;

// GUI panel for show active effects
public class EffectsPanel {
  private final HashSet<Effect> effects;

  private Vector2d position;
  private Vector2d effect_size;
  private double y_shift;

  public EffectsPanel(HashSet<Effect> ref_to_effects, Vector2d position, Vector2d effect_size, double y_shift) {
    this.effects = ref_to_effects;
    this.position = position;
    this.effect_size = effect_size;
    this.y_shift = y_shift;
  }

  public void draw(SpriteBatch batch) {
    int i = 0;
    for (Effect effect : effects) {
      if (!effect.isActive()) continue;
      ++i;

      batch.draw(effect.getTexture(), (float)position.x, (float)position.y - (float)(y_shift + effect_size.y) * i, (float)effect_size.x, (float)effect_size.y);
    }
  }
}