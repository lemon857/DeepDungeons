package com.deepdungeons.game.effects;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.Character;
import com.deepdungeons.game.Player;

public class Effect {
  // Levels
  // change simple  cycle
  // I      10%     100%
  // II     15%     110%
  // III    20%     120%
  // IV     25%     130%
  private static final int MAX_LEVEL = 4;
  private static final int LEVELS_SIZE = 2 * MAX_LEVEL + 1;

  private static HashMap<String, Effect> static_effects;

  public static void initStaticEffects() {
    static_effects = new HashMap<>();
  }

  public static Effect addStaticEffect(String name, Effect effect) {
    static_effects.put(name, effect);
    return static_effects.get(name);
  }

  public static Effect getStaticEffect(String name, Character character) {
    Effect effect = (Effect)static_effects.get(name).clone();
    if (effect == null) {
      System.err.println("Effect " + name + " is not found");
      return null;
    }
    if (effect.is_player) {
      effect.change_func = (value) -> effect.change_func_sig_player.accept((Player)character, value);
    } else {
      effect.change_func = (value) -> effect.change_func_sig_character.accept(character, value);
    }
    return effect;
  }

  public static Effect getStaticEffect(String name, Character character, int level, double duration) {
    Effect effect = (Effect)static_effects.get(name).clone();
    if (effect == null) {
      System.err.printf("Effect %s is not found", name);
      return null;
    }

    if (effect.is_player) {
      effect.change_func = (value) -> effect.change_func_sig_player.accept((Player)character, value);
    } else {
      effect.change_func = (value) -> effect.change_func_sig_character.accept(character, value);
    }
    effect.addLevel(level, duration);
    return effect;
  }
  
  private String name;
  
  // set for simple effect
  // change with delta for cycle effect
  protected Consumer<Double> change_func;

  protected BiConsumer<com.deepdungeons.game.Character, Double> change_func_sig_character;
  protected BiConsumer<com.deepdungeons.game.Player, Double> change_func_sig_player;
  protected boolean is_player;

  protected int current_level;
  protected int current_sum;

  protected int[] inf_levels;
  protected int[] levels;
  
  protected boolean is_active;

  protected final Texture positive_texture;
  protected final Pixmap positive_image;

  protected double positive_sign;

  // For character
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
    
    this.inf_levels = new int[LEVELS_SIZE];
    this.levels = new int[LEVELS_SIZE];
  }

  public final void update(double delta) {
    if (!is_active) return;

    for (int i = 0; i < LEVELS_SIZE; ++i) {
      if (levels[i] != 0) {
        levels[i] -= delta;
      }
      if (levels[i] <= 0) {
        levels[i] = 0;
        recalculateLevel();
      }
    }

    updateEffect(delta);
  }

  public final void setCharacterFunc(BiConsumer<com.deepdungeons.game.Character, Double> func) {
    change_func_sig_character = func;
    is_player = false;
  }

  public final void setPlayerFunc(BiConsumer<com.deepdungeons.game.Player, Double> func) {
    change_func_sig_player = func;
    is_player = true;
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

  public final double getPositiveSign() {
    return positive_sign;
  }

  public Texture getTexture() {
    return positive_texture;
  }

  public final void addLevel(int level, double duration) {
    level = getCorrectLevel(level);

    is_active = true;
    if (duration < 0) {
      ++inf_levels[level + MAX_LEVEL];
    } else {
      levels[level + MAX_LEVEL] += duration;
    }
    recalculateLevel();
  }

  public final void removeInfLevel(int level) {
    level = getCorrectLevel(level);

    if (--inf_levels[level + MAX_LEVEL] < 0) {
      inf_levels[level + MAX_LEVEL] = 0;
    }
    recalculateLevel();
  }

  private void recalculateLevel() {
    boolean is_empty = true;
    for (int i : levels) {
      if (i != 0) {
        is_empty = false;
        break;
      }
    }
    for (int i : inf_levels) {
      if (i != 0) {
        is_empty = false;
        break;
      }
    }

    if (is_empty) {
      is_active = false;
      applyLevel(0);
      return;
    }

    int negative_level = 0;
    for (int i = MAX_LEVEL - 1; i > 0; --i) {
      if (levels[i] != 0 || inf_levels[i] != 0) {
        negative_level = i - 4;
      }
    }

    int positive_level = 0;
    for (int i = MAX_LEVEL + 1; i < 2 * MAX_LEVEL + 1; ++i) {
      if (levels[i] != 0 || inf_levels[i] != 0) {
        positive_level = i - MAX_LEVEL;
      }
    }

    current_level = negative_level + positive_level;
    applyLevel(current_level);
  }

  protected void applyLevel(int level) {}

  protected void updateEffect(double delta) {}

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

  public Object clone() {
    Effect effect = new Effect(positive_image, name);

    if (is_player) {
      effect.setPlayerFunc(change_func_sig_player);
    } else {
      effect.setCharacterFunc(change_func_sig_character);
    }

    System.out.printf("Clone effect Is char method null: %b\n", effect.change_func_sig_character == null);

    return effect;
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