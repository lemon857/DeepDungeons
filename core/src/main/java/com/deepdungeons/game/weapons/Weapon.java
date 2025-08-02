package com.deepdungeons.game.weapons;

public interface Weapon {
  double getDamage();
  Type getWeaponType();

  public enum Type {
    CloseRangeWeapon
  }
}