package com.deepdungeons.game.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.Room;

public class Generator {

  public static Point getRoomDeltaFromDoor(int door_id) {
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

  public enum RoomType {
    Undefined,
    Empty,
    Items,
    Monsters,
    Other
  }

  public class RoomMark {
    public RoomType type;
    public int zone;
    public boolean[] doors;
    public int[] lock_doors;
    public Color[] lock_doors_color;
    public int require_key;
    public Color require_key_color;
  }

  private class NextRoomInfo {
    public Point pos;
    public int zone;
    public int from;
  }

  private static final int ROOMS_LIMIT = 5;
  private static final int ZONES_LIMIT = ROOMS_LIMIT * 4 + 1;

  private static int zone_pool = 0;

  private final Point start_pos;

  private final Random rand;

  private final Queue<NextRoomInfo> next_rooms;

  private final HashMap<Point, RoomMark> skeleton;
  
  private final HashMap<Integer, ArrayList<RoomMark>> zones;

  private int[] prev_zones;
  private int[] require_keys;
  private Color[] require_key_colors;

  private Texture image;

  private final Pixmap map;
  private final Point offset;

  public Generator(Point start_pos) {
    this.next_rooms = new LinkedList<>();
    this.skeleton = new HashMap<>();
    this.zones = new HashMap<>();
    this.rand = new Random();
    this.start_pos = start_pos;
    this.prev_zones = new int[ZONES_LIMIT];
    this.require_keys = new int[ZONES_LIMIT];
    this.require_key_colors = new Color[ZONES_LIMIT];

    this.map = new Pixmap(100, 100, Pixmap.Format.RGBA8888);
    this.offset = new Point(50, 50);
  }

  public void clearSkeleton() {
    next_rooms.clear();
    skeleton.clear();
    zone_pool = 0;
  }

  public void generateSkeleton() {
    clearSkeleton();


    prev_zones = new int[ZONES_LIMIT];
    require_keys = new int[ZONES_LIMIT];
    require_key_colors = new Color[ZONES_LIMIT];

    NextRoomInfo start_info = new NextRoomInfo();
    start_info.pos = start_pos;
    start_info.zone = zone_pool;
    start_info.from = -1;
    next_rooms.offer(start_info);

    while (!next_rooms.isEmpty()) {
      generateNextRoom(next_rooms.peek());
      next_rooms.poll();
    }

    generateKeys();

    for (Map.Entry<Point, RoomMark> e : skeleton.entrySet()) {
      Point pos = e.getKey();
      RoomMark mark = e.getValue();
      System.out.printf("Pos: x: %d, y: %d -- zone: %d -- doors: ", pos.x, pos.y, mark.zone);

      Point map_pos = Point.sum(offset, Point.mul(pos, 2));

      map.setColor(Color.WHITE);
      map.drawPixel(map_pos.x, map_pos.y);


      map.setColor(Color.BROWN);
      for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
        System.out.printf("%d ", mark.doors[i] ? 1 : 0);
        if (mark.doors[i]) {
          Point door_pos = Point.sum(map_pos, getRoomDeltaFromDoor(i));
          map.drawPixel(door_pos.x, door_pos.y);
        }
      }

      System.out.print(" -- lock doors: ");

      map.setColor(Color.RED);
      for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
        System.out.printf("%d ", mark.lock_doors[i]);
        if (mark.lock_doors[i] != 0) {
          Point door_pos = Point.sum(map_pos, getRoomDeltaFromDoor(i));
          map.drawPixel(door_pos.x, door_pos.y);
        }
      }

      System.out.print("\n");
    }

    image = new Texture(map);
  }

  public Texture getTexture() {
    return image;
  }

  public RoomMark getRoomMark(int x, int y) {
    return skeleton.get(new Point(x, y));
  }  
  
  public RoomMark getRoomMark(Point pos) {
    return skeleton.get(pos);
  }

  private void generateNextRoom(NextRoomInfo info) {
    int[] must_doors = new int[4];
    System.out.println("----------------------\n[Generator] Pos: X: " + info.pos.x + " Y: " + info.pos.y);
    System.out.print("[Generator] Must doors: ");
    for (int i = 0; i < 4; ++i) {
      Point cur_room = Point.sum(info.pos, getRoomDeltaFromDoor(i));

      if (skeleton.containsKey(cur_room)) {
        if (skeleton.get(cur_room).doors[(i + 2) % 4]) {
          must_doors[i] = 1;
        } else {
          must_doors[i] = -1;
        }
      } else {
        must_doors[i] = 0;
      }
      System.out.print(must_doors[i] + " ");
    }
    System.out.println("");

    RoomMark mark = new RoomMark();
    mark.zone = info.zone;
    mark.type = RoomType.Items; // use random here

    mark.doors = generateNewDoors(new boolean[Room.MAX_DOORS_COUNT], must_doors);
    mark.lock_doors = generateLockDoors(new int[Room.MAX_DOORS_COUNT], mark.doors, info.from);
    mark.lock_doors_color = new Color[Room.MAX_DOORS_COUNT];
    mark.require_key = 0;

    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (mark.lock_doors[i] == 0) continue;

      mark.lock_doors_color[i] = new Color(rand.nextFloat(1), rand.nextFloat(1), rand.nextFloat(1), 1);
    }
    
    if (!zones.containsKey(mark.zone)) {
      zones.put(mark.zone, new ArrayList<>());
    }
    zones.get(mark.zone).add(mark);

    System.out.println("[Generator] Zone: " + mark.zone);

    if (skeleton.size() >= ROOMS_LIMIT - 1) {
      mark.doors = new boolean[Room.MAX_DOORS_COUNT];
      mark.doors[info.from] = true;
      mark.lock_doors = new int[Room.MAX_DOORS_COUNT];
      skeleton.put(info.pos, mark);
      return;

    } else {
      skeleton.put(info.pos, mark);
    }



    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (mark.lock_doors[i] != 0 && must_doors[i] != 1) {
        NextRoomInfo new_info = new NextRoomInfo();
        new_info.pos = Point.sum(info.pos, getRoomDeltaFromDoor(i));
        new_info.zone = ++zone_pool;
        //new_info.from = (i + 2) % 4;
        new_info.from = i;
        next_rooms.offer(new_info);

        System.out.println("New zone: " + new_info.zone);

        prev_zones[new_info.zone] = mark.zone;
        require_keys[new_info.zone] = mark.lock_doors[i];
        require_key_colors[new_info.zone] = mark.lock_doors_color[i];
      }
    }

    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (mark.doors[i] && must_doors[i] != 1) {
        NextRoomInfo new_info = new NextRoomInfo();
        new_info.pos = Point.sum(info.pos, getRoomDeltaFromDoor(i));
        new_info.zone = mark.zone;
        //new_info.from = (i + 2) % 4;
        new_info.from = i;
        next_rooms.offer(new_info);
      }
    }
  }

  private void generateKeys() {
    for (int i = 0; i < ZONES_LIMIT; ++i) {
      if (require_keys[i] == 0) continue;

      int zone = prev_zones[i];

      int index = rand.nextInt(zones.get(zone).size());

      zones.get(zone).get(index).require_key = require_keys[i];
      zones.get(zone).get(index).require_key_color = require_key_colors[i];
    }
  }

  private boolean[] generateNewDoors(boolean[] doors, int[] must_doors) {
    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (must_doors[i] == 1) {
        doors[i] = true;
      } else if (must_doors[i] == -1) {
        doors[i] = false;
      } else if (!doors[i]) {
        doors[i] = (rand.nextInt(0, 10000) < 5000);
      }
    }
    return doors;
  }

  private int[] generateLockDoors(int[] lock_doors, boolean[] doors, int from) {
    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (from != i && doors[i] && rand.nextInt(10000) < 7000) {
        while(lock_doors[i] == 0) {
          lock_doors[i] = rand.nextInt();
        }
      }
    }
    return lock_doors;
  }
}