package com.deepdungeons.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.items.Key;
import com.deepdungeons.game.mobs.Mob;
import com.deepdungeons.game.renderer.Drawable;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Vector2d;

public class Room implements Drawable {
  public static final int SCREEN_WIDTH = 150;
  public static final int SCREEN_HEIGHT = 100;

  public static final int WIDTH = 100;
  public static final int HEIGHT = 100;

  public static final int SHIFT_X = 25;

  public static final Point START_BORDER = new Point(SHIFT_X + 1, 1);
  public static final Point END_BORDER = Point.sum(START_BORDER, new Point(WIDTH - 3, HEIGHT - 3));

  // for horizontal position
  public static final int DOOR_WIDTH = 10;
  public static final int DOOR_KEY_WIDTH = DOOR_WIDTH - 8;
  public static final int DOOR_HEIGHT = 1;

  // x - for x axis, y - for y axis
  public static final Point DOOR_OFFSET = new Point(START_BORDER.x + (WIDTH - DOOR_WIDTH) / 2, START_BORDER.y + (HEIGHT - DOOR_WIDTH) / 2);
  public static final Point DOOR_KEY_OFFSET = new Point(START_BORDER.x + (WIDTH - DOOR_KEY_WIDTH) / 2, START_BORDER.y + (HEIGHT - DOOR_KEY_WIDTH) / 2);

  public static final int MAX_DOORS_COUNT = 4;

  public static final double PICK_UP_MAX_DISTANCE = 4.2;

  private static final Color BORDER_COLOR = new Color(1, 0, 0, 0.25f);
  private static final Color OPENED_DOOR_COLOR = new Color(0.5f, 0.3f, 0.13f, 1);
  private static final Color CLOSED_DOOR_COLOR = new Color(1, 0.3f, 0.3f, 0.5f);

  private static final boolean MOBS_AI = false;

  private final Point pos;

  private Texture background_texture;

  private boolean is_active;

  //private Texture debug_texture;

  private final Sound doors_close;
  private final Sound doors_open;

  private final Sound drop_sound;

  private boolean non_actual;
  private boolean is_fight;

  private final ArrayList<Item> items;

  private final ArrayList<Mob> mobs;

  private Item can_grab_item;

  private final boolean[] doors;
  private final int[] lock_doors;
  private final int[] before_block_doors;

  private final Color[] lock_doors_color;
  private final Color[] before_block_doors_color;

  private static final Color block_door_color = new Color(1f, 0f, 0.2f, 1f);

  private final Random rand;

  public static Point getDoorPosition(int door_id) {
    switch (door_id) {
      case 0: return new Point(DOOR_OFFSET.x + DOOR_WIDTH / 2, END_BORDER.y - DOOR_WIDTH);
      case 1: return new Point(END_BORDER.x - DOOR_WIDTH, DOOR_OFFSET.y + DOOR_WIDTH / 2);
      case 2: return new Point(DOOR_OFFSET.x + DOOR_WIDTH / 2, START_BORDER.y);
      case 3: return new Point(START_BORDER.x, DOOR_OFFSET.y + DOOR_WIDTH / 2);
      default: return new Point(-1, -1);
    }
  }

  // Room
  // must_doors: -1 - door must empty, 0 - nevermind, 1 - doors must be
  public Room(Point pos, boolean[] doors, int[] lock_doors, Color[] lock_doors_color) {
    this.rand = new Random();
    this.pos = pos;
    this.is_active = true;

    this.doors = doors;
    this.lock_doors = lock_doors;
    this.lock_doors_color = lock_doors_color;

    this.before_block_doors = new int[MAX_DOORS_COUNT];
    this.before_block_doors_color = new Color[MAX_DOORS_COUNT];

    this.non_actual = true;
    this.is_fight = false;
    this.items = new ArrayList<>();
    this.mobs = new ArrayList<>();

    this.doors_close = Gdx.audio.newSound(Gdx.files.internal("sounds/doors_close.mp3"));
    this.doors_open = Gdx.audio.newSound(Gdx.files.internal("sounds/doors_open.mp3"));
    this.drop_sound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.mp3"));
  }

  public Point getPos() {
    return pos;
  }

  public boolean canGoNextRoom(int door_id) {
    return doors[door_id] && (lock_doors[door_id] == 0);
    // return doors[door_id];
  }

  public boolean isDoorExist(int door_id) {
    return doors[door_id];
  }

  public int getLockedDoorKey(int door_id) {
    return lock_doors[door_id];
  }

  public int getDoorsCount() {
    int res = 0;
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      if (doors[i]) ++res;
    }
    return res;
  }

  public Key lockDoor(int door_id) {
    lock_doors[door_id] = rand.nextInt();
    lock_doors_color[door_id] = new Color(rand.nextFloat(1), rand.nextFloat(1), rand.nextFloat(1), 1);

    non_actual = true;

    Key res = (Key)Item.getStaticItem("special/key");

    res.setParams(lock_doors[door_id], lock_doors_color[door_id]);
    return res;
  }

  public void lockAllDoors() {
    doors_close.play();
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      if (lock_doors[i] != 0) {
        before_block_doors[i] = lock_doors[i];
        before_block_doors_color[i] = lock_doors_color[i];

      } else {
        before_block_doors[i] = 0;
      }
      lock_doors[i] = -1;
      lock_doors_color[i] = block_door_color;
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
    doors_open.play();
    for (int i = 0; i < MAX_DOORS_COUNT; ++i) {
      if (before_block_doors[i] == 0) {
        lock_doors[i] = 0;
      } else {
        lock_doors[i] = before_block_doors[i];
        lock_doors_color[i] = before_block_doors_color[i];
        before_block_doors[i] = 0;
      }
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
      //generateBackground();
      non_actual = false;
    }

    if (!mobs.isEmpty() && !is_fight) {
      is_fight = true;
      lockAllDoors();
    }
    if (mobs.isEmpty() && is_fight) {
      is_fight = false;
      unlockAllDoors();
    }
  }

  public boolean isFight() {
    return is_fight;
  }

  public void addMob(Mob mob) {
    mobs.add(mob);
  }

  public void addItem(Item item) {
    items.add(item);
  }

  public void addItem(String static_name) {
    items.add(Item.getStaticItem(static_name));
  }

  public Item grabItem() {
    items.remove(can_grab_item);
    return can_grab_item;
  }

  public boolean canGrabItem(Vector2d cur_pos) {
    for (Item item : items) {
      if (Vector2d.distance(cur_pos, item.getCenterPos()) < PICK_UP_MAX_DISTANCE) {
        can_grab_item = item;
        return true;
      }
    }
    return false;
  }

  public Item getNearestItem(Vector2d cur_pos) {
    double res = 50;
    for (Item item : items) {
      res = Math.min(res, Vector2d.distance(cur_pos, item.getCenterPos()));
    }
    for (Item item : items) {
      if (Vector2d.distance(cur_pos, item.getCenterPos()) == res) return item;
    }
    return null;    
  }

  public double distanceToNearestItem(Vector2d cur_pos) {
    double res = 50;
    for (Item item : items) {
      res = Math.min(res, Vector2d.distance(cur_pos, item.getCenterPos()));
    }
    return res;
  }

  public double distanceToNearestMob(Vector2d cur_pos, double offset) {
    double res = 50;
    for (Mob mob : mobs) {
      res = Math.min(res, Vector2d.distance(cur_pos, mob.getCenterPos()) - offset - mob.getSizeOffset());
    }
    return res;
  }

  public double angleToNearestMob(Vector2d cur_pos, Vector2d dir, double offset) {
    double distance = distanceToNearestMob(cur_pos, offset);

    int current_mob = -1;

    for (int i = 0; i < mobs.size(); ++i) {
      if (Vector2d.distance(cur_pos, mobs.get(i).getCenterPos()) == distance) {
        current_mob = i;
        break;
      }
    }

    if (current_mob == -1) return -1;

    Vector2d v2 = new Vector2d(cur_pos, mobs.get(current_mob).getCenterPos());

    return Vector2d.angle(dir, v2);
  }

  public boolean tryHitMob(Vector2d player_pos, Vector2d dir, double damage, double max_distance, double max_angle, double offset, boolean allow_splash) {
    boolean is_hit = false;
    for (int i = 0; i < mobs.size(); ++i) {
      Mob mob = mobs.get(i);
      Vector2d v2 = new Vector2d(player_pos, mob.getCenterPos());

      double dis = Vector2d.distance(player_pos, mob.getCenterPos());
      if (dis - offset - mob.getSizeOffset() > max_distance) {
        // System.out.println("[" + i + "] Too far: " + (dis - offset));
        continue;
      }
      if (Vector2d.angle(dir, v2) > max_angle) {
        // System.out.println("[" + i + "] Too much angle: " + Vector2d.angle(dir, v2));
        continue;
      }

      if (mob.damage(damage)) {
        Item item = mob.getDrop();
        if (item != null) {
          drop_sound.play();
          item.setCenterPos(mob.getCenterPos());
          items.add(item);
        }
        mobs.remove(i);
      }
      if (!allow_splash) {
        return true; // Hit only one mob
      } else {
        is_hit = true;
      }
    }
    return is_hit;
  }

  @Override
  public void setActive(boolean value) {
    is_active = value;
  }

  @Override
  public boolean getActive() {
    return is_active;
  }

  @Override
  public void draw(SpriteBatch batch) {
    if (!is_active) return;
    // correct coords for correct Pixmap drawing
    //batch.draw(background_texture, SHIFT_X, HEIGHT, WIDTH, -HEIGHT);

    for (Item item : items) {
      item.draw(batch);
    }

    for (Mob mob : mobs) {
      mob.draw(batch);
    }
  }

  public void dispose() {
    items.clear();
    mobs.clear();
    doors_close.dispose();
    doors_open.dispose();
    drop_sound.dispose();
  }

  public void generateBackground() {
    Pixmap map = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGB888);

    map.setColor(BORDER_COLOR);

    final Point start = new Point(START_BORDER.x - SHIFT_X, START_BORDER.y);
    final Point end = new Point(END_BORDER.x - START_BORDER.x + 1, END_BORDER.y);

    map.drawLine(start.x, start.y, start.x, end.y);
    map.drawLine(start.x, start.y, end.x, start.y);

    map.drawLine(end.x, end.y, start.x, end.y);
    map.drawLine(end.x, end.y, end.x, start.x);


    final Point door_offset = new Point(DOOR_OFFSET.x - SHIFT_X, DOOR_OFFSET.y);
    final Point door_key_offset = new Point(DOOR_KEY_OFFSET.x - SHIFT_X, DOOR_KEY_OFFSET.y);
    // Top door
    if (doors[0]) {
      if (lock_doors[0] != 0) { 
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(door_offset.x, HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[0]);
        map.drawRectangle(door_key_offset.x, HEIGHT - DOOR_HEIGHT - 1, DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(door_offset.x, HEIGHT - DOOR_HEIGHT - 1, DOOR_WIDTH, DOOR_HEIGHT);
      }
    }

    // Right door
    if (doors[1]) {
      if (lock_doors[1] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(WIDTH - DOOR_HEIGHT - 1, door_offset.y, DOOR_HEIGHT, DOOR_WIDTH);
        map.setColor(lock_doors_color[1]);
        map.drawRectangle(WIDTH - DOOR_HEIGHT - 1,  door_key_offset.y, DOOR_HEIGHT, DOOR_KEY_WIDTH);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(WIDTH - DOOR_HEIGHT - 1, door_offset.y, DOOR_HEIGHT, DOOR_WIDTH);
      }
    }

    // Bottom door
    if (doors[2]) {
      if (lock_doors[2] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(door_offset.x, 1, DOOR_WIDTH, DOOR_HEIGHT);
        map.setColor(lock_doors_color[2]);
        map.drawRectangle(door_key_offset.x, 1, DOOR_KEY_WIDTH, DOOR_HEIGHT);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(door_offset.x, 1, DOOR_WIDTH, DOOR_HEIGHT);
      }
    }

    // Left door
    if (doors[3]) {
      if (lock_doors[3] != 0) {
        map.setColor(CLOSED_DOOR_COLOR);
        map.drawRectangle(1, door_offset.y, DOOR_HEIGHT, DOOR_WIDTH);
        map.setColor(lock_doors_color[3]);
        map.drawRectangle(1, door_key_offset.y, DOOR_HEIGHT, DOOR_KEY_WIDTH);
      }
      else {
        map.setColor(OPENED_DOOR_COLOR);
        map.drawRectangle(1, door_offset.y, DOOR_HEIGHT, DOOR_WIDTH);
      }
    }

    background_texture = new Texture(map);
  }
}
