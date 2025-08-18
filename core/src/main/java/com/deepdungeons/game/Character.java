package com.deepdungeons.game;

public class Character {
  private double move_speed_modifier;
  private double attack_speed_modifier;
  private double strength_modifier;

  private final double move_speed;
  private final double attack_speed;
  private final double strength;

  public Character(double move_speed, double attack_speed, double strength) {
    this.move_speed = move_speed;
    this.attack_speed = attack_speed;
    this.strength = strength;

    this.move_speed_modifier = 1;
    this.attack_speed_modifier = 1;
    this.strength_modifier = 1;
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

  protected final void updateEffects(double delta) {

  }
}