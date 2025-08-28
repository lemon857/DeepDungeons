package com.deepdungeons.game;

import java.util.HashMap;
import java.util.Iterator;

import com.deepdungeons.game.effects.CycleEffect;
import com.deepdungeons.game.effects.Effect;
import com.deepdungeons.game.utils.Ref;

public class Character {
  private final Ref<Double> move_speed_modifier;
  private final Ref<Double> attack_speed_modifier;
  private final Ref<Double> strength_modifier;

  protected final HashMap<String, Effect> effects;

  private final double move_speed;
  private final double attack_speed;
  private final double strength;

  protected final Ref<Double> health_points;

  public Character(double health_points, double move_speed, double attack_speed, double strength) {
    this.move_speed = move_speed;
    this.attack_speed = attack_speed;
    this.strength = strength;

    this.effects = new HashMap<>();

    this.move_speed_modifier = new Ref<>(1.0);
    this.attack_speed_modifier = new Ref<>(1.0);
    this.strength_modifier = new Ref<>(1.0);

    this.health_points = new Ref<>(health_points);
  }

  public final double getMoveSpeed() {
    return move_speed * move_speed_modifier.v;
  }

  public final double getAttackSpeed() {
    return attack_speed * attack_speed_modifier.v;
  }

  public final double getStrength() {
    return strength * strength_modifier.v;
  }

  public final void addEffect(String name, int level, double duration) {
    if (effects.containsKey(name)) {
      effects.get(name).addLevel(level, duration);
    } else {
      Effect new_effect = Effect.getStaticEffect(name, getReferenceFromName(name), level, duration);
      effects.put(name, new_effect);
    }
  }

  public final void addCycleEffect(String name, int level, double duration, double period, double damage) {
    if (effects.containsKey(name)) {
      effects.get(name).addLevel(level, duration); // need fix updating different period and damage levels
    } else {
      CycleEffect new_effect = (CycleEffect)Effect.getStaticEffect(name, getReferenceFromName(name), level, duration);
      new_effect.updateProperties(period, damage);
      effects.put(name, new_effect);
    }
  }

  public final boolean tryRemoveInfEffect(String name) {
    boolean res = false;
    if (effects.containsKey(name)) {
      Effect effect = effects.get(name);

    
      while (effect.isActive()) {
        if (!effect.isInfinity()) break;
         
        effect.removeCurrentLevel();
        res = true;
      }
    }
    return res;
  }

  private Ref<Double> getReferenceFromName(String name) {
    switch (name) {
      case "effects/speed": return move_speed_modifier;
      case "effects/strength": return strength_modifier;
      case "effects/haste": return attack_speed_modifier;
      case "effects/instant_damage": return health_points;
    }
    return getReferenceFromOtherName(name);
  }
  
  protected Ref<Double> getReferenceFromOtherName(String name) {
    return null;
  }

  protected final void updateEffects(double delta) {
    Iterator<Effect> it = effects.values().iterator();

    while (it.hasNext()) {
      Effect effect = it.next();
      effect.update(delta);
      if (!effect.isActive()) {
        it.remove();
      }
    }
  }


  // True if it dead
  public boolean damage(double dmg) { return false; }

  public void heal(double health) {}
}