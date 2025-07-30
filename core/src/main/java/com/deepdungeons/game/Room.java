package com.deepdungeons.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Pixmap;

public class Room {
  private static final int START_BORDER = 1;
  private static final int END_BORDER = 48;

  // for horizontal position
  private static final int DOOR_WIDTH = 10;
  private static final int DOOR_HEIGHT = 4;

  private static int current_id = 1;

  private final int id;

  // -1 - door doesn't exist
  // 0 - door to ungeneratet room
  // 1+ - door to exist room
  public int[] doors;

  // Room with random doors
  public Room(int count_new_doors) {
    this.id = current_id;
    current_id++;

    this.doors = new int[] {-1, -1, -1, -1};

    GenerateNewDoors(count_new_doors);
  }

  // Room with one input door
  public Room(int from_door, int from_id) {
    this.id = current_id;
    current_id++;

    this.doors = new int[] {-1, -1, -1, -1};

    if (from_door >= 0 && from_door <= 3) {
      doors[from_door] = from_id;
    }
  }

  // Room with input door and random doors
  public Room(int count_new_doors, int from_door, int from_id) {
    this.id = current_id;
    ++current_id;

    this.doors = new int[] {-1, -1, -1, -1};

    if (from_door >= 0 && from_door <= 3) {
      doors[from_door] = from_id;
    }

    if (count_new_doors > 3) {
      count_new_doors = 3;
    }

    GenerateNewDoors(count_new_doors);
  }

  public int GetID() {
    return id;
  }

  public int GetNextRoomID(int door_id) {
    return doors[door_id];
  }

  public void SetNextRoomID(int door_id, int id) {
    if (door_id < 0 && door_id > 3) {
      // invalid door id
    }
    if (id < 1) {
      // invalid id
    }
    doors[door_id] = id;
  }

  public Pixmap GenerateImage() {
    Pixmap map = new Pixmap(50, 50, Pixmap.Format.RGB888);
  
    map.setColor(1, 0, 0, 1);

    map.drawLine(START_BORDER, START_BORDER, START_BORDER, END_BORDER);
    map.drawLine(START_BORDER, START_BORDER, END_BORDER, START_BORDER);

    map.drawLine(END_BORDER, END_BORDER, START_BORDER, END_BORDER);
    map.drawLine(END_BORDER, END_BORDER, END_BORDER, START_BORDER);

    map.setColor(0, 1, 0, 1);


    if (doors[0] != -1) {
      // Top door
      map.drawRectangle(20, 1, DOOR_WIDTH, DOOR_HEIGHT);
    }

    if (doors[1] != -1) {
      // Right door
      map.drawRectangle(45, 20, DOOR_HEIGHT, DOOR_WIDTH);
    }

    if (doors[2] != -1) {
      // Bottom door
      map.drawRectangle(20, 45, DOOR_WIDTH, DOOR_HEIGHT);
    }

    if (doors[3] != -1) {
      // Left door
      map.drawRectangle(1, 20, DOOR_HEIGHT, DOOR_WIDTH);
    }

    return map;
  }

  private void GenerateNewDoors(int count) {
    Random r = new Random(System.currentTimeMillis());

    for (int i = 0; i < count; ++i) {
      int new_index = r.nextInt(4);
      while (doors[new_index] != -1) {
        ++new_index;
        if (new_index > 3) {
          new_index = 0;
        }
      }
      doors[new_index] = 0;
    }
  }
}
