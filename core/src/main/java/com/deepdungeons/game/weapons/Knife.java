package com.deepdungeons.game.weapons;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Vector2d;

public class Knife extends Item implements CloseRangeWeapon {

  public static final int WIDTH = 4;
  public static final int HEIGHT = 7;

  public static final double DISTANCE = 8.5;
  public static final double ANGLE = Math.PI / 2.0;
  public static final double DAMAGE = 2;
  public static final double COOLDOWN = 0.5;

  public Knife() {
    super(Item.Type.Weapon, Item.Tier.Common, "knife");
    this.pos = new Vector2d();
    this.size = new Vector2d(WIDTH, HEIGHT);

    generateRandomPos();
    generateImage();
  }

  public Knife(Vector2d pos) {
    super(Item.Type.Weapon, Item.Tier.Common, "knife");
    this.pos = pos;
    this.size = new Vector2d(WIDTH, HEIGHT);

    generateImage();
  }
 
  @Override
  protected final void generateImage() {
    image = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);

    image.setColor(1, 1, 1, 1);
    
    image.drawPixel(0, 0);
    image.drawPixel(0, 1);
    image.drawPixel(1, 1);
    image.drawPixel(1, 2);
    image.drawPixel(2, 3);
    image.drawPixel(2,4);
    image.drawPixel(2, 5);
    image.drawPixel(1, 6);

    texture = new Texture(image);
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