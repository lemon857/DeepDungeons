package com.deepdungeons.game.effects;

import com.badlogic.gdx.graphics.Pixmap;

public final class CycleEffect extends Effect {
  private double damage;
  private double period;
  private double period_timer;

  public CycleEffect(String path_to_positive_texture, String path_to_negative_texture, String name) {
    super(path_to_positive_texture, path_to_negative_texture, name);

    this.damage = 0;
    this.period = 0;
    this.period_timer = 0;
  }

  public CycleEffect(Pixmap positive_map, Pixmap negative_map, String name) {
    super(positive_map, negative_map, name);

    this.damage = 0;
    this.period = 0;
    this.period_timer = 0;
  }

  public void updateProperties(double period, double damage) {
    this.damage = damage;
    this.period = period;
  }

  @Override
  public void update(double delta) {
    if (!is_active) return;

    period_timer += delta;

    if (period_timer >= period) {
      period_timer = 0;
      
      ref.v -= damage;
    }

    if (levels.peek().getDuration() != -1) {
      timer += delta;

      if (timer >= levels.peek().getDuration()) {
        timer = 0;
        
        removeCurrentLevel();
      }
    }
  }

  @Override
  public Effect clone() {
    Effect effect = new CycleEffect(getPositiveImage(), getNegativeImage(), getName());

    return effect;
  }
}
