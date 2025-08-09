package com.deepdungeons.game.weapons;

import java.util.Random;

public final class Hand {
  protected static final double MIN_DAMAGE = 0.5;
  protected static final double MAX_DAMAGE = 1.1;

  protected static final double MIN_COOLDOWN = 0.27;
  protected static final double MAX_COOLDOWN = 0.4;

  public static final double DISTANCE = 5;

  public static final double ANGLE = Math.PI;

  public static final double getDamage(Random rand) {
    return rand.nextDouble(MIN_DAMAGE, MAX_DAMAGE);
  }

  public static final double getCooldown(Random rand) {
    return rand.nextDouble(MIN_COOLDOWN, MAX_COOLDOWN);
  }
}