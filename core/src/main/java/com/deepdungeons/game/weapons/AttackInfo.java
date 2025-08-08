package com.deepdungeons.game.weapons;

import com.deepdungeons.game.utils.Vector2d;

public class AttackInfo {
  public double damage;
  public Vector2d vector;

  public AttackInfo(double damage, Vector2d vector) {
    this.damage = damage;
    this.vector = vector;
  }
}