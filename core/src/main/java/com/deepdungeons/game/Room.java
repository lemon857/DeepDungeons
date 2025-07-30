package com.deepdungeons.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;

public class Room {
  private static final int START_BORDER = 1;
  private static final int END_BORDER = 48;

  // for horizontal position
  private static final int DOOR_WIDTH = 10;
  private static final int DOOR_HEIGHT = 4;

  private final Point pos;

  // -1 - door doesn't exist
  // 0 - door to ungeneratet room
  // 1+ - door to exist room
  private final boolean[] doors;

  public static Point GetDeltaFromDoor(int door_id) {
    Point delta = new Point();
    switch (door_id) {
      case 0: delta.y = -1; break; // top
      case 1: delta.x = 1; break; // right
      case 2: delta.y = 1; break; // bottom
      case 3: delta.x = -1; break; // left
      default: break;
    }
    return delta;
  }

  // Room
  // must_doors: -1 - door must empty, 0 - nevermind, 1 - doors must be
  public Room(Point pos, int[] must_doors) {
    this.pos = pos;

    this.doors = new boolean[] {false, false, false, false};

    GenerateNewDoors(must_doors);
  }

  public Point GetPos() {
    return pos;
  }

  public boolean CanGoNextRoom(int door_id) {
    return doors[door_id];
  }

  public Pixmap GenerateImage() {
    Pixmap map = new Pixmap(50, 50, Pixmap.Format.RGB888);

    map.setColor(1, 0, 0, 1);

    map.drawLine(START_BORDER, START_BORDER, START_BORDER, END_BORDER);
    map.drawLine(START_BORDER, START_BORDER, END_BORDER, START_BORDER);

    map.drawLine(END_BORDER, END_BORDER, START_BORDER, END_BORDER);
    map.drawLine(END_BORDER, END_BORDER, END_BORDER, START_BORDER);

    map.setColor(0, 1, 0, 1);


    if (doors[0]) {
      // Top door
      map.drawRectangle(20, 1, DOOR_WIDTH, DOOR_HEIGHT);
    }

    if (doors[1]) {
      // Right door
      map.drawRectangle(45, 20, DOOR_HEIGHT, DOOR_WIDTH);
    }

    if (doors[2]) {
      // Bottom door
      map.drawRectangle(20, 45, DOOR_WIDTH, DOOR_HEIGHT);
    }

    if (doors[3]) {
      // Left door
      map.drawRectangle(1, 20, DOOR_HEIGHT, DOOR_WIDTH);
    }

    return map;
  }

  public void PrintDoors() {
    for (int i = 0; i < 4; ++i) {
      System.out.print(doors[i] + " ");
    }
    System.out.print("\n");
  }

  private void GenerateNewDoors(int[] must_doors) {
    Random r = new Random(System.currentTimeMillis());

    for (int i = 0; i < 4; ++i) {
      System.out.print(must_doors[i] + " ");
      if (must_doors[i] == 1) {
        doors[i] = true;
      } else if (must_doors[i] == -1) {
        doors[i] = false;
      } else if (!doors[i]) {
        doors[i] = (r.nextInt(100) > 70);
      }
    }
    System.out.print("\n");
  }
}
