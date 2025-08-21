package com.deepdungeons.game;

import java.util.HashSet;
import java.util.Iterator;

import com.deepdungeons.game.effects.Effect;
import com.deepdungeons.game.utils.Ref;

public class Character {
  private Ref<Double> move_speed_modifier;
  private Ref<Double> attack_speed_modifier;
  private Ref<Double> strength_modifier;

  protected final HashSet<Effect> effects;

  private final double move_speed;
  private final double attack_speed;
  private final double strength;

  protected double health_points;

  public Character(double move_speed, double attack_speed, double strength) {
    this.move_speed = move_speed;
    this.attack_speed = attack_speed;
    this.strength = strength;

    this.effects = new HashSet<>();

    this.move_speed_modifier = new Ref<Double>(1.0);
    this.attack_speed_modifier = new Ref<Double>(1.0);
    this.strength_modifier = new Ref<Double>(1.0);
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
    Effect new_effect = Effect.getStaticEffect(name, getReferenceFromName(name), level, duration);
    
    for (Effect effect : effects) {
      if (effect.equals(new_effect)) {
        effect.addLevel(level, duration);
        return;
      }
    }
    effects.add(new_effect);
  }

  private Ref<Double> getReferenceFromName(String name) {
    switch (name) {
      case "effects/speed": return move_speed_modifier;
      case "effects/strength": return strength_modifier;
      case "effects/haste": return attack_speed_modifier;
    }
    return getReferenceFromOtherName(name);
  }
  
  protected Ref<Double> getReferenceFromOtherName(String name) {
    return null;
  }

  protected final void updateEffects(double delta) {
    Iterator<Effect> it = effects.iterator();

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