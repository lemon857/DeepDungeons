package com.deepdungeons.game.utils;

public class Constants {
  public static final float PIXELS_PER_METER = 64f;

  public static final short PHYSICS_ENTITY_CATEGORY_BITS = 0b01;

  public static final short WALL_MASK_BITS = 0b00000001;
  public static final short PLAYER_MASK_BITS = WALL_MASK_BITS;
  public static final short ITEM_MASK_BITS = 0b00000010;
}
