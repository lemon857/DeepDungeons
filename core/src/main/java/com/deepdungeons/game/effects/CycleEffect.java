package com.deepdungeons.game.effects;

import com.badlogic.gdx.graphics.Pixmap;

public final class CycleEffect extends Effect {
  private double damage;
  private double period;
  private double period_timer;

  public CycleEffect(String path_to_positive_texture, String name, double positive_sign) {
    super(path_to_positive_texture, name);

    this.damage = 0;
    this.period = 0;
    this.period_timer = 0;
    this.positive_sign = positive_sign;
  }

  public CycleEffect(Pixmap positive_map, String name, double positive_sign) {
    super(positive_map, name);

    this.damage = 0;
    this.period = 0;
    this.period_timer = 0;
    this.positive_sign = positive_sign;
  }

  public void updateProperties(double period, double damage) {
    this.damage = damage;
    this.period = period;
  }

  public double getPeriod() {
    return period;
  }

  public double getDamage() {
    return damage;
  }

  @Override
  public void update(double delta) {
    if (!is_active) return;

    period_timer += delta;

    if (period_timer >= period) {
      System.out.printf("[CycleEffect] Update, delta %f, period: %f, timer: %f\n", delta, period, period_timer);
      period_timer = 0;
      
      change_func.accept(damage * getChangeKoef(current_level));
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
  public void addLevel(int level, double duration) {
    System.out.println("[Effect] Add level: " + level);

    is_active = true;
    current_level = sumLevels(current_level, level);
    current_sum += level;

    levels.add(new LevelPair(duration, level));
  }

  @Override
  public void removeCurrentLevel() {
    current_sum -= levels.peek().getPrevLevel();
    current_level = getCorrectLevel(current_sum);

    levels.remove();

    if (levels.isEmpty()) {
      current_level = 0;
      current_sum = 0;
      is_active = false;
    }
  }

  @Override
  protected double getChangeKoef(int level) {
    return 1 + (level - 1) * 0.1;
  }

  @Override
  public Object clone() {
    CycleEffect effect = new CycleEffect(positive_image, getName(), positive_sign);

    effect.damage = 0;
    effect.period = 0;
    effect.period_timer = 0;
    effect.positive_sign = positive_sign;

    if (is_player) {
      effect.setPlayerFunc(change_func_sig_player);
    } else {
      effect.setCharacterFunc(change_func_sig_character);
    }

    return effect;
  }
}
