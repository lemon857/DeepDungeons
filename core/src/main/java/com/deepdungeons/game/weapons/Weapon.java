package com.deepdungeons.game.weapons;

public interface Weapon {
  double getCooldown();
  double getDamage();
  Type getWeaponType();

  public enum Type {
    CloseRangeWeapon
  }
}