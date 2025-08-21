package com.deepdungeons.game.effects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
  // Levels
  // I      change 10%
  // II     change 15%
  // III    change 20%
  // IV     change 25%
  private static final int MAX_LEVEL = 4;

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

  protected Ref<Double> ref;

  private int current_level;
  private int current_sum;

  private String name;

  private PriorityQueue<LevelPair> levels;

  private double timer;
  
  private boolean is_active;

  private Texture positive_texture;
  private Pixmap positive_image;

  private Texture negative_texture;
  private Pixmap negative_image;

  public Effect(String path_to_positive_texture, String path_to_negative_texture, String name) {
    init(name);

    this.positive_image = new Pixmap(Gdx.files.internal(path_to_positive_texture));
    this.positive_texture = new Texture(positive_image);

    this.negative_image = new Pixmap(Gdx.files.internal(path_to_negative_texture));
    this.negative_texture = new Texture(negative_image);
  }

  public Effect(Pixmap positive_map, Pixmap negative_map, String name) {
    init(name);

    this.positive_image = new Pixmap(positive_map.getWidth(), positive_map.getHeight(), positive_map.getFormat());
    this.positive_image.drawPixmap(positive_map, 0, 0);
    this.positive_texture = new Texture(positive_image);

    this.negative_image = new Pixmap(negative_map.getWidth(), negative_map.getHeight(), negative_map.getFormat());
    this.negative_image.drawPixmap(negative_map, 0, 0);
    this.negative_texture = new Texture(negative_image);
  }

  private void init(String name) {
    this.name = name;
    this.is_active = true;
    this.current_level = 0;
    this.current_sum = 0;
    this.timer = 0;

    this.levels = new PriorityQueue<>(Comparator.comparingDouble(LevelPair::getDuration));
  }

  public void update(double delta) {
    if (!is_active) return;

    timer += delta;

    if (timer >= levels.peek().getDuration()) {
      timer = 0;
      current_sum -= levels.peek().getPrevLevel();
      current_level = getCorrectLevel(current_sum);

      levels.remove();

      if (levels.isEmpty()) {
        ref.v = getChangeKoef(0);
        current_level = 0;
        current_sum = 0;
        System.out.println("[Effect] Change ref final: " + ref.v);
        is_active = false;
      } else {
        ref.v = getChangeKoef(current_level);
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

    is_active = true;
    current_level = sumLevels(current_level, level);
    current_sum += level;

    levels.add(new LevelPair(duration, level));

    ref.v = getChangeKoef(current_level);
    System.out.println("[Effect] Change ref add level: " + ref.v);
  }

  public Texture getTexture() {
    if (current_level < 0) {
      return negative_texture;
    }

    return positive_texture;
  }

  private static int sumLevels(int level_a, int level_b) {
    if (Math.signum(level_a) != Math.signum(level_b)) {
      level_a += level_b;
      level_a = getCorrectLevel(level_a);
    } else if (Math.signum(level_a) > 0) {
      level_b = getCorrectLevel(level_b);
      level_a = Math.max(level_a, level_b);
    } else {
      level_b = getCorrectLevel(level_b);
      level_a = Math.min(level_a, level_b);
    }
    return level_a;
  }

  protected static int getCorrectLevel(int level) {
    if (level < -MAX_LEVEL) {
      level = -MAX_LEVEL;
    } else if (level > MAX_LEVEL) {
      level = MAX_LEVEL;
    }
    return level;
  }

  protected static double getChangeKoef(int level) {
    if (level > 0) {
      return 1.05 + level * 0.05;
    } else if (level < 0) {
      return 0.95 + level * 0.05;
    }
    return 1;
  }

  @Override
  public Effect clone() {
    return new Effect(positive_image, negative_image, name);
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