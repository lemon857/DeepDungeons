package com.deepdungeons.game.weapons;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Vector2d;

public class CloseRangeWeapon extends Item implements Weapon {
  public double distance;
  public double angle;
  public double damage;
  public double cooldown;
  private final double size_koef;

  public CloseRangeWeapon(String path_to_texture, String name, double size_koef, double distance, double angle, double damage, double cooldown) {
    super(Item.Type.Weapon, Item.Tier.Common, name, path_to_texture);
    this.pos = new Vector2d();
    this.distance = distance;
    this.damage = damage;
    this.angle = angle;
    this.cooldown = cooldown;
    this.size_koef = size_koef;
    updateSize(size_koef);
  }

  public CloseRangeWeapon(Pixmap map, String name, double size_koef, double distance, double angle, double damage, double cooldown) {
    super(Item.Type.Weapon, Item.Tier.Common, name, map);
    this.pos = new Vector2d();
    this.distance = distance;
    this.damage = damage;
    this.angle = angle;
    this.cooldown = cooldown;
    this.size_koef = size_koef;
    updateSize(size_koef);
  }

  @Override
  public Item clone() {
    CloseRangeWeapon item = new CloseRangeWeapon(this.image, getName(), size_koef, distance, angle, damage, cooldown);
    item.pos = new Vector2d(this.pos);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }

  public double getDistance() {
    return distance;
  }

  public double getAngle() {
    return angle;
  }
  
  @Override
  public double getDamage() {
    return damage;
  }

  @Override
  public double getCooldown() {
    return cooldown;
  }

  @Override
  public Weapon.Type getWeaponType() {
    return Weapon.Type.CloseRangeWeapon;
  }
}