package com.deepdungeons.game.effects;

import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Effect {
  // Levels
  // change simple  cycle
  // I      10%     100%
  // II     15%     110%
  // III    20%     120%
  // IV     25%     130%
  private static final int MAX_LEVEL = 4;

  private static HashMap<String, Effect> static_effects;

  public static void initStaticEffects() {
    static_effects = new HashMap<>();
  }

  public static void addStaticEffect(String name, Effect effect) {
    static_effects.put(name, effect);
  }

  public static Effect getStaticEffect(String name, Consumer<Double> change_func) {
    Effect effect = static_effects.get(name).clone();
    if (effect == null) {
      System.err.println("Effect " + name + " is not found");
      return null;
    }
    effect.change_func = change_func;
    return effect;
  }

  public static Effect getStaticEffect(String name, Consumer<Double> change_func, int level, double duration) {
    Effect effect = static_effects.get(name).clone();
    if (effect == null) {
      System.err.println("Effect " + name + " is not found");
      return null;
    }
    effect.change_func = change_func;
    effect.addLevel(level, duration);
    return effect;
  }

  protected class LevelPair {
    // -1 if infinity
    private final double duration;
    private final int level;
  
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
  
  private String name;
  
  // set for simple effect
  // change with delta for cycle effect
  protected Consumer<Double> change_func;

  protected int current_level;
  protected int current_sum;

  protected PriorityQueue<LevelPair> levels;

  protected double timer;
  
  protected boolean is_active;

  protected final Texture positive_texture;
  protected final Pixmap positive_image;

  protected double positive_sign;

  public Effect(String path_to_positive_texture, String name) {
    init(name);

    this.positive_image = new Pixmap(Gdx.files.internal(path_to_positive_texture));
    this.positive_texture = new Texture(positive_image);
  }

  public Effect(Pixmap positive_map, String name) {
    init(name);

    this.positive_image = new Pixmap(positive_map.getWidth(), positive_map.getHeight(), positive_map.getFormat());
    this.positive_image.drawPixmap(positive_map, 0, 0);
    this.positive_texture = new Texture(positive_image);
  }

  private void init(String name) {
    this.name = name;
    this.is_active = true;
    this.current_level = 0;
    this.current_sum = 0;
    this.timer = 0;

    this.levels = new PriorityQueue<>(Comparator.comparingDouble(LevelPair::getDuration));
  }

  public void update(double delta) {}

  public final boolean isInfinity() {
    return levels.peek().getDuration() == -1; 
  }

  public final boolean isActive() {
    return is_active;
  }

  public final String getName() {
    return name;
  }

  public final int getLevel() {
    return current_level;
  }

  public final double getDuration() {
    if (!is_active) return 0;

    return levels.peek().getDuration();
  }

  public final double getPositiveSign() {
    return positive_sign;
  }

  public void addLevel(int level, double duration) {}

  public void removeCurrentLevel() {}

  public Texture getTexture() {
    return positive_texture;
  }

  protected static int sumLevels(int level_a, int level_b) {
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

  protected double getChangeKoef(int level) {
    return 1;
  }

  protected static int getCorrectLevel(int level) {
    if (level < -MAX_LEVEL) {
      level = -MAX_LEVEL;
    } else if (level > MAX_LEVEL) {
      level = MAX_LEVEL;
    }
    return level;
  }

  @Override
  public Effect clone() {
    return new Effect(positive_image, name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Effect)) return false;
    Effect other = (Effect)obj;
    return this.name.equals(other.name);
  }
  @Override
  public int hashCode() {
    return name.hashCode();
  }
}