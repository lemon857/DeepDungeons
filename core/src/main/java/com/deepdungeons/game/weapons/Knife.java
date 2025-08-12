package com.deepdungeons.game.weapons;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Vector2d;

public class Knife extends Item implements CloseRangeWeapon {
  public static final double DISTANCE = 8.5;
  public static final double ANGLE = Math.PI / 2.0;
  public static final double DAMAGE = 2;
  public static final double COOLDOWN = 0.5;

  private static final double SIZE_KOEF = 0.7;

  public Knife(String path_to_texture) {
    super(Item.Type.Weapon, Item.Tier.Common, "knife", path_to_texture);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }

  public Knife(Pixmap map) {
    super(Item.Type.Weapon, Item.Tier.Common, "knife", map);
    this.pos = new Vector2d();
    updateSize(SIZE_KOEF);
  }

  @Override
  public Item clone() {
    Knife item = new Knife(this.image);
    item.pos = new Vector2d(this.pos);
    item.is_texture_from_file = this.is_texture_from_file;

    return item;
  }

  @Override
  public double getDistance() {
    return DISTANCE;
  }

  @Override
  public double getAngle() {
    return ANGLE;
  }

  @Override
  public double getDamage() {
    return DAMAGE;
  }

  @Override
  public double getCooldown() {
    return COOLDOWN;
  }

  @Override
  public Weapon.Type getWeaponType() {
    return Weapon.Type.CloseRangeWeapon;
  }
}