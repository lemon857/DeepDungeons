package com.deepdungeons.game.weapons;

public interface Weapon {
  boolean isAllowSplash();
  double getCooldown();
  double getDamage();
  Type getWeaponType();

  public enum Type {
    CloseRangeWeapon
  }
}