package com.deepdungeons.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.items.Key;
import com.deepdungeons.game.mobs.Mob;

public class Room {
  public static final int SCREEN_WIDTH = 100;
  public static final int SCREEN_HEIGHT = 100;

  public  static final int START_BORDER = 1;
  public static final int END_BORDER = SCREEN_WIDTH - 2;

  // for horizontal position
  public static final int DOOR_WIDTH = 10;
  public static final int DOOR_KEY_WIDTH = DOOR_WIDTH - 8;
  public static final int DOOR_HEIGHT = 1;

  public static final int DOOR_OFFSET = (SCREEN_WIDTH - DOOR_WIDTH) / 2;
  public static final int DOOR_KEY_OFFSET = (SCREEN_WIDTH - DOOR_KEY_WIDTH) / 2;

  public static final int MAX_DOORS_COUNT = 4;

  public static final double PICK_UP_MAX_DISTANCE = 4.2;

  private static final Color BORDER_COLOR = new Color(1, 0, 0, 0.25f);
  private static final Color OPENED_DOOR_COLOR = new Color(0.5f, 0.3f, 0.13f, 1);
  private static final Color CLOSED_DOOR_COLOR = new Color(1, 0.3f, 0.3f, 0.5f);

  private static final boolean MOBS_AI = true;

  private final Point pos;

  private Texture background_texture;

  //private Texture debug_texture;

  private boolean non_actual;

  private final ArrayList<Item> items;

  private final ArrayList<Mob> mobs;

  private Item can_grab_item;

  private final boolean[] doors;
  private final int[] lock_doors;
  private final Color[] lock_doors_color;

  private final Random rand;

  public static Point GetRoomDeltaFromDoor(int door_id) {
    Point delta = new Point();
    switch (door_id) {
      case 0: delta.y = 1; break; // top
      case 1: delta.x = 1; break; // right
      case 2: delta.y = -1; break; // bottom
      case 3: delta.x = -1; break; // left
      default: break;
    }
    return delta;
  }

  public static Point GetDoorPosition(int door_id) {
    switch (door_id) {
      case 0: return new Point(DOOR_OFFSET, SCREEN_HEIGHT - DOOR_WIDTH);
      case 1: return new Point(SCREEN_WIDTH - DOOR_WIDTH, DOOR_OFFSET);
      case 2: return new Point(DOOR_OFFSET, 1);
      case 3: return new Point(1, DOOR_OFFSET);
      default: return new Point(-1, -1);
    }
  }

  // Room
  // must_doors: -1 - door must empty, 0 - nevermind, 1 - doors must be
  public Room(Point pos, int[] must_doors) {
    this.rand = new Random();
    this.pos = pos;

    this.doors = new boolean[] {false, false, false, false};
    this.lock_doors = new int[] {0, 0, 0, 0};
    this.lock_doors_color = new Color[4];
    this.non_actual = true;
    this.items = new ArrayList<>();
    this.mobs = new ArrayList<>();

    // Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGB888);
    // pixmap.setColor(1, 1, 1, 1);
    // pixmap.drawPixel(0, 0);
    // debug_texture = new Texture(pixmap);

    generateNewDoors(must_doors);
  }

  public Point getPos() {
    return pos;
  }

  public boolean canGoNextRoom(int door_id) {
    return doors[door_id] && (lock_doors[door_id] == 0);
  }

  public boolean isDoorExist(int door_id) {
    return doors[door_id];
  }

  public int getLockedDoorKey(int door_id) {
    return lock_doors[door_id];
  }

  public Key lockDoor(int door_id) {
    lock_doors[door_id] = rand.nextInt();
    lock_doors_color[door_id] = new Color(rand.nextFloat(1), rand.nextFloat(1), rand.nextFloat(1), 1);
    Point new_pos = new Point();
    do {
    new_pos.x = rand.nextInt(Key.WIDTH + 1, SCREEN_WIDTH - Key.WIDTH - 1);
    new_pos.y = rand.nextInt(Key.HEIGHT + 1, SCREEN_HEIGHT - Key.HEIGHT - 1);
    } while(distanceToNearestItem(new_pos) < 7);

    non_actual = true;
  
    return new Key(lock_doors[door_id], lock_doors_color[door_id], new_pos);
  }

  public void lockAllDoors() {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      lock_doors[i] = rand.nextInt();
      lock_doors_color[i] = new Color(rand.nextFloat(1), rand.nextFloat(1), rand.nextFloat(1), 1);
    }
    non_actual = true;
  }

  public boolean tryUnlockDoor(int door_id, int key) {
    if (key == lock_doors[door_id]) {
      lock_doors[door_id] = 0;
      non_actual = true;
      return true;
    }
    return false;
  }

  public void unlockAllDoors() {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      lock_doors[i] = 0;
    }
    non_actual = true;
  }

  public void update(double delta) {
    if (MOBS_AI) {
      for (Mob mob : mobs) {
        mob.update(delta);
      }
    }
    for (Item item : items) {
      item.update(delta);
    }

    if (non_actual) {
      generateBackground();
      non_actual = false;
    }
  }

  public void addMob(Mob mob) {
    mobs.add(mob);
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public Item grabItem() {
    items.remove(can_grab_item);
    return can_grab_item;
  }

  public boolean canGrabItem(Vector2d cur_pos) {
    for (Item item : items) {
      if (Point.distance(cur_pos, item.getPos()) < PICK_UP_MAX_DISTANCE) {
        can_grab_item = item;
        return true;
      }
    }
    return false;
  }

  public Item getNearestItem(Vector2d cur_pos) {
    double res = 50;
    for (Item item : items) {
      res = Math.min(res, Point.distance(cur_pos, item.getPos()));
    }
    for (Item item : items) {
      if (Point.distance(cur_pos, item.getPos()) == res) return item;
    }
    return null;    
  }

  public double distanceToNearestItem(Point cur_pos) {
    double res = 50;
    for (Item item : items) {
      res = Math.min(res, Point.distance(cur_pos, item.getPos()));
    }
    return res;
  }

  public double distanceToNearestItem(Vector2d cur_pos) {
    double res = 50;
    for (Item item : items) {
      res = Math.min(res, Point.distance(cur_pos, item.getPos()));
    }
    return res;
  }

  public double distanceToNearestMob(Vector2d cur_pos) {
    double res = 50;
    for (Mob mob : mobs) {
      res = Math.min(res, Vector2d.distance(cur_pos, mob.getPos()));
    }
    return res;
  }

  public double angleToNearestMob(Vector2d cur_pos, Vector2d dir) {
    double distance = distanceToNearestMob(cur_pos);

    int current_mob = -1;

    for (int i = 0; i < mobs.size(); ++i) {
      if (Vector2d.distance(cur_pos, mobs.get(i).getPos()) == distance) {
        current_mob = i;
        break;
      }
    }

    if (current_mob == -1) return -1;

    Vector2d v2 = new Vector2d(cur_pos, mobs.get(current_mob).getPos());

    return Vector2d.angle(dir, v2);
  }

  public boolean  tryHitMob(Vector2d player_pos, Vector2d dir, double damage, double max_distance, double max_angle) {
    for (int i = 0; i < mobs.size(); ++i) {
      Vector2d v2 = new Vector2d(player_pos, mobs.get(i).getPos());

      if (v2.lenght() > max_distance) {
        System.out.println("[" + i + "] Too far: " + v2.lenght());
        continue;
      }
      if (Vector2d.angle(dir, v2) > max_angle) {
        System.out.println("[" + i + "] Too much angle: " + Vector2d.angle(dir, v2));
        continue;
      }

      if (mobs.get(i).damage(damage)) {
        mobs.remove(i);
      }
      return true; // Hit only one mob
    }
    return false;
  }

  public void draw(SpriteBatch batch) {
    // correct coords for correct Pixmap drawing
    batch.draw(background_texture, 0, SCREEN_HEIGHT, SCREEN_WIDTH, -SCREEN_HEIGHT);

    for (Item item : items) {
      item.draw(batch);
    }

    for (Mob mob : mobs) {
      mob.draw(batch);
    }
  }

  public void generateBackground() {
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
        map.drawRectangle(DOOR_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[0]);
        map.drawRectangle(DOOR_KEY_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1, DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, SCREEN_HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
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
        map.drawRectangle(DOOR_OFFSET, 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[2]);
        map.drawRectangle(DOOR_KEY_OFFSET, 1, DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(DOOR_OFFSET, 1, DOOR_WIDTH, DOOR_HEIGHT);
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

    background_texture = new Texture(map);
  }

  public void printDoors() {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      System.out.print(doors[i] + " ");
    }
    System.out.print("\n");
  }

  private void generateNewDoors(int[] must_doors) {
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      System.out.print(must_doors[i] + " ");
      if (must_doors[i] == 1) {
        doors[i] = true;
      } else if (must_doors[i] == -1) {
        doors[i] = false;
      } else if (!doors[i]) {
        doors[i] = (rand.nextInt(100) > 70);
      }
    }
    System.out.print("\n");
  }
}
