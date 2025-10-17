package com.deepdungeons.game.weapons;

public interface Weapon {
  boolean isAllowSplash();
  double getCooldown();
  double getDamage();
}