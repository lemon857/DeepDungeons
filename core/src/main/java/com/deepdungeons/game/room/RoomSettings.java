package com.deepdungeons.game.room;

import com.deepdungeons.game.utils.Point;

public class RoomSettings {
  public int screen_width = 150;
  public int screen_height = 100;

  public int width = 100;
  public int height = 100;

  public int shift_x = 25;

  public Point start_border = new Point(shift_x + 1, 1);
  public Point end_border = Point.sum(start_border, new Point(width - 3, height - 3));

  // for horizontal position
  public int door_width = 10;
  public int door_key_with = door_width - 8;
  public int door_height = 1;

  public Point door_offset = new Point(start_border.x + (width - door_width) / 2, start_border.y + (height - door_width) / 2);
  public Point door_key_offset = new Point(start_border.x + (width - door_key_with) / 2, start_border.y + (height - door_key_with) / 2);

  public int max_doors_count = 4;

  public double PICK_UP_MAX_DISTANCE = 4.2;

  public boolean enable_mobs_ai = false;
}
