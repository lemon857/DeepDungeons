package com.deepdungeons.game;

import java.util.HashSet;

import com.deepdungeons.game.effects.Effect;
import com.deepdungeons.game.utils.Ref;

public class Character {
  private Ref<Double> move_speed_modifier;
  private Ref<Double> attack_speed_modifier;
  private Ref<Double> strength_modifier;

  private final HashSet<Effect> effects;

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
    Effect new_effect = Effect.getStaticEffect(name, move_speed_modifier, level, duration);
    
    for (Effect effect : effects) {
      if (effect.equals(new_effect)) {
        effect.addLevel(level, duration);
        return;
      }
    }
    effects.add(new_effect);
  }

  protected final void updateEffects(double delta) {
    for (Effect effect : effects) {
      effect.update(delta);
      if (!effect.isActive()) {
        effects.remove(effect);
      }
    }
    //System.out.println("Move speed: " + move_speed_modifier.v);
  }


  // True if it dead
  public boolean damage(double dmg) { return false; }

  public void treat(double health) {}
}