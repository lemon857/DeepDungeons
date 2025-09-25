package com.deepdungeons.game;

import java.util.HashMap;
import java.util.Iterator;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.deepdungeons.PhysicsObject;
import com.deepdungeons.game.effects.CycleEffect;
import com.deepdungeons.game.effects.Effect;

public class Character extends PhysicsObject {
  private double move_speed_modifier;
  private double attack_speed_modifier;
  private double strength_modifier;

  protected final HashMap<String, Effect> effects;

  private final double move_speed;
  private final double attack_speed;
  private final double strength;

  protected double health_points;

  public Character(double health_points, double move_speed, double attack_speed, double strength) {
    super(BodyDef.BodyType.DynamicBody);

    this.move_speed = move_speed;
    this.attack_speed = attack_speed;
    this.strength = strength;

    this.effects = new HashMap<>();

    this.move_speed_modifier = 1.0;
    this.attack_speed_modifier = 1.0;
    this.strength_modifier = 1.0;

    this.health_points = health_points;
  }

  public final double getMoveSpeed() {
    return move_speed * move_speed_modifier;
  }

  public final double getAttackSpeed() {
    return attack_speed * attack_speed_modifier;
  }

  public final double getStrength() {
    return strength * strength_modifier;
  }

  public final void addEffect(String name, int level, double duration) {
    if (effects.containsKey(name)) {
      Effect effect = effects.get(name);

      effect.addLevel(level, duration);
    } else {
      effects.put(name, Effect.getStaticEffect(name, this, level, duration));
    }
  }

  public final void addCycleEffect(String name, int level, double duration, double period, double damage) {
    if (effects.containsKey(name)) {
      CycleEffect effect = (CycleEffect)effects.get(name);

      effect.addLevel(level, duration); // need fix updating different period and damage levels
    } else {
      CycleEffect new_effect = (CycleEffect)Effect.getStaticEffect(name, this, level, duration);
      new_effect.updateProperties(period, damage);
      effects.put(name, new_effect);
    }
  }

  public final void removeInfEffect(String name, int level) {
    if (effects.containsKey(name)) {
      Effect effect = effects.get(name);
      effect.removeInfLevel(level);
    }
  }

  public final void setMoveSpeedModifier(double value) {
    move_speed_modifier = value;
  }

  public final void setAttackSpeedModifier(double value) {
    attack_speed_modifier = value;
  }
  
  public final void setStrengthModifier(double value) {
    strength_modifier = value;
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