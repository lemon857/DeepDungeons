package com.deepdungeons.game.effects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.utils.Vector2d;

// GUI panel for show active effects
public class EffectsPanel {
  private static HashMap<Integer, Texture> level_images;

  public static void initLevelImages() {
    level_images = new HashMap<>();
  }

  public static void addLevelTexture(int level, String path_to_texture) {
    level_images.put(level, new Texture(path_to_texture));
  }

  private final HashMap<String, Effect> effects;

  private final Vector2d position;
  private final Vector2d effect_size;
  private final double y_shift;

  public EffectsPanel(HashMap<String, Effect> ref_to_effects, Vector2d position, Vector2d effect_size, double y_shift) {
    this.effects = ref_to_effects;
    this.position = position;
    this.effect_size = effect_size;
    this.y_shift = y_shift;
  }

  public void draw(SpriteBatch batch) {
    int i = 0;
    for (Effect effect : effects.values()) {
      if (!effect.isActive()) continue;
      ++i;

      batch.draw(level_images.get(effect.getLevel()), (float)position.x - (float)effect_size.x, (float)position.y - (float)(y_shift + effect_size.y) * i, 
        (float)effect_size.x / 2.f, (float)effect_size.y / 2.f);

      batch.draw(effect.getTexture(), (float)position.x, (float)position.y - (float)(y_shift + effect_size.y) * i, 
        (float)effect_size.x, (float)effect_size.y);
    }
  }
}