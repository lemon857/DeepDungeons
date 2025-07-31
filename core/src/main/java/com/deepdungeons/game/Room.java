package com.deepdungeons.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.Key;

public class Room {
  public static final int SCREEN_WIDTH = 50;
  public static final int SCREEN_HEIGHT = 50;

  public  static final int START_BORDER = 1;
  public static final int END_BORDER = SCREEN_WIDTH - 2;

  // for horizontal position
  public static final int DOOR_WIDTH = 10;
  public static final int DOOR_KEY_WIDTH = DOOR_WIDTH - 8;
  public static final int DOOR_HEIGHT = 2;

  public static final int DOOR_OFFSET = (SCREEN_WIDTH - DOOR_WIDTH) / 2;
  public static final int DOOR_KEY_OFFSET = (SCREEN_WIDTH - DOOR_KEY_WIDTH) / 2;

  public static final int MAX_DOORS_COUNT = 4;

  private static final Color BORDER_COLOR = new Color(1, 0, 0, 1);
  private static final Color OPENED_DOOR_COLOR = new Color(0, 1, 0, 1);
  private static final Color CLOSED_DOOR_COLOR = new Color(1, 0, 1, 1);

  private final Point pos;

  // -1 - door doesn't exist
  // 0 - door to ungeneratet room
  // 1+ - door to exist room
  private final boolean[] doors;
  private final int[] lock_doors;
  private Color[] lock_doors_color;

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
    this.lock_doors = new int[] {0, 0, 0, 0};
    this.lock_doors_color = new Color[4];

    generateNewDoors(must_doors);
  }

  public Point getPos() {
    return pos;
  }

  public boolean canGoNextRoom(int door_id) {
    return doors[door_id] && (lock_doors[door_id] == 0);
  }

  public int getLockedDoorKey(int door_id) {
    return lock_doors[door_id];
  }

  public Key lockDoor(int door_id) {
    Random r = new Random(System.currentTimeMillis());
    lock_doors[door_id] = r.nextInt();
    lock_doors_color[door_id] = new Color(r.nextFloat(1), r.nextFloat(1), r.nextFloat(1), 1);
    return new Key(lock_doors[door_id], lock_doors_color[door_id]);
  }

  public void lockAllDoors() {
    Random r = new Random(System.currentTimeMillis());
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      lock_doors[i] = r.nextInt();
      lock_doors_color[i] = new Color(r.nextFloat(1), r.nextFloat(1), r.nextFloat(1), 1);
    }
  }

  public void unlockDoor(int door_id, Key key) {
    if (key.getKey() == lock_doors[door_id]) {
      lock_doors[door_id] = 0;
    }
  }

  public void unlockAllDoors() {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      lock_doors[i] = 0;
    }
  }

  public Pixmap generateImage() {
    Pixmap map = new Pixmap(SCREEN_WIDTH, SCREEN_HEIGHT, Pixmap.Format.RGB888);

    map.setColor(BORDER_COLOR);

    map.drawLine(START_BORDER, START_BORDER, START_BORDER, END_BORDER);
    map.drawLine(START_BORDER, START_BORDER, END_BORDER, START_BORDER);

    map.drawLine(END_BORDER, END_BORDER, START_BORDER, END_BORDER);
    map.drawLine(END_BORDER, END_BORDER, END_BORDER, START_BORDER);


    // Top door
    if (doors[0]) {
      if (lock_doors[0] != 0) { 
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[0]);
        map.drawRectangle(DOOR_KEY_OFFSET, 1, DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, 1, DOOR_WIDTH, DOOR_HEIGHT);
      }
    }

    // Right door
    if (doors[1]) {
      if (lock_doors[1] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(SCREEN_WIDTH - DOOR_HEIGHT - 1, DOOR_OFFSET, DOOR_HEIGHT, DOOR_WIDTH);
        map.setColor(lock_doors_color[1]);
        map.drawRectangle(SCREEN_WIDTH - DOOR_HEIGHT - 1,  DOOR_KEY_OFFSET, DOOR_HEIGHT, DOOR_KEY_WIDTH);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(SCREEN_WIDTH - DOOR_HEIGHT - 1, DOOR_OFFSET, DOOR_HEIGHT, DOOR_WIDTH);
      }
    }

    // Bottom door
    if (doors[2]) {
      if (lock_doors[2] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[2]);
        map.drawRectangle(DOOR_KEY_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1 , DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
      }
    }

    // Left door
    if (doors[3]) {
      if (lock_doors[3] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(1, DOOR_OFFSET, DOOR_HEIGHT, DOOR_WIDTH);
        map.setColor(lock_doors_color[3]);
        map.drawRectangle(1, DOOR_KEY_OFFSET, DOOR_HEIGHT, DOOR_KEY_WIDTH);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(1, DOOR_OFFSET, DOOR_HEIGHT, DOOR_WIDTH);
      }
    }

    return map;
  }

  public void printDoors() {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      System.out.print(doors[i] + " ");
    }
    System.out.print("\n");
  }

  private void generateNewDoors(int[] must_doors) {
    Random r = new Random(System.currentTimeMillis());

    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
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
