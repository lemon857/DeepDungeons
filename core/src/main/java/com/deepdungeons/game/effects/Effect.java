package com.deepdungeons.game.effects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Ref;

class LevelPair {
  private double duration;
  private int level;

  public LevelPair(double duration, int level) {
    this.duration = duration;
    this.level = level;
  }

  public double getDuration() {
    return duration;
  }

  public int getPrevLevel() {
    return level;
  }
}

public class Effect {
  private static HashMap<String, Effect> static_effects;

  public static void initStaticEffects() {
    static_effects = new HashMap<>();
  }

  public static void addStaticEffect(String name, Effect effect) {
    static_effects.put(name, effect);
  }

  public static Effect getStaticEffect(String name, Ref<Double> ref) {
    Effect effect = static_effects.get(name).clone();
    if (effect == null) {
      System.err.println("Effect " + name + " is not found");
      return null;
    }
    effect.ref = ref;
    return effect;
  }

  public static Effect getStaticEffect(String name, Ref<Double> ref, int level, double duration) {
    Effect effect = static_effects.get(name).clone();
    if (effect == null) {
      System.err.println("Effect " + name + " is not found");
      return null;
    }
    effect.ref = ref;
    effect.addLevel(level, duration);
    return effect;
  }

  // Levels
  // I      change 10%
  // II     change 15%
  // III    change 20%

  protected Ref<Double> ref;

  private int current_level;

  private String name;

  private PriorityQueue<LevelPair> levels;

  private double timer;
  
  private boolean is_active;

  private Sprite sprite;
  private Pixmap image;

  public Effect(String path_to_texture, String name) {
    init(name);

    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.sprite = new Sprite(new Texture(image));
  }

  public Effect(Pixmap map, String name) {
    init(name);

    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.sprite = new Sprite(new Texture(image));
  }

  private void init(String name) {
    this.name = name;
    this.is_active = true;
    this.current_level = 0;
    this.timer = 0;

    this.levels = new PriorityQueue<>(Comparator.comparingDouble(LevelPair::getDuration));
    this.levels.add(new LevelPair(0, 0));
  }

  public void update(double delta) {
    if (!is_active) return;

    timer += delta;

    if (timer >= levels.peek().getDuration()) {
      timer = 0;
      current_level = sumLevels(current_level, -levels.peek().getPrevLevel());

      levels.remove();

      if (levels.isEmpty()) {
        ref.v = getChangeKoef(current_level);
        System.out.println("[Effect] Change ref final: " + ref.v);
        is_active = false;
      } else {
        ref.v = getChangeKoef(levels.peek().getPrevLevel());
        System.out.println("[Effect] Change ref next: " + ref.v);
      }
    }
  }

  public final boolean isActive() {
    return is_active;
  }

  public final String getName() {
    return name;
  }

  public void addLevel(int level, double duration) {
    System.out.println("[Effect] Add level: " + level);

    current_level = sumLevels(current_level, level);

    levels.add(new LevelPair(duration, level));

    if (level < -3) {
      level = -3;
    } else if (level > 3) {
      level = 3;
    }

    ref.v = getChangeKoef(current_level);
    System.out.println("[Effect] Change ref add level: " + ref.v);
  }

  private static int sumLevels(int level_a, int level_b) {
    if (Math.signum(level_a) != Math.signum(level_b)) {
      level_a += level_b;
      if (level_a < -3) {
        level_a = -3;
      } else if (level_a > 3) {
        level_a = 3;
      }
    } else if (Math.signum(level_a) > 0) {
      if (level_b < -3) {
        level_b = -3;
      } else if (level_b > 3) {
        level_b = 3;
      }
      level_a = Math.max(level_a, level_b);
    } else {
      if (level_b < -3) {
        level_b = -3;
      } else if (level_b > 3) {
        level_b = 3;
      }
      level_a = Math.min(level_a, level_b);
    }
    return level_a;
  }

  protected double getChangeKoef(int level) {
    switch (level) {
    case 1: return 1.1;
    case 2: return 1.15;
    case 3: return 1.2;

    case -1: return 0.9;
    case -2: return 0.85;
    case -3: return 0.8;
    }
    return 1;
  }

  @Override
  public Effect clone() {
    return new Effect(image, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Effect)) return false;
    Effect other = (Effect)obj;
    return this.name == other.name;
  }
  @Override
  public int hashCode() {
    return name.hashCode();
  }
}