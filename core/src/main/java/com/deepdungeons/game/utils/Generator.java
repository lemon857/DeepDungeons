package com.deepdungeons.game.utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
  }

  private class NextRoomInfo {
    public Point pos;
    public int zone;
    public int from;
  }

  private static final int ROOMS_LIMIT = 10;

  private static int zone_pool = 0;

  private final Point start_pos;

  private final Random rand;

  private final Queue<NextRoomInfo> next_rooms;

  private final HashMap<Point, RoomMark> skeleton;

  public Generator(Point start_pos) {
    this.next_rooms = new LinkedList<>();
    this.skeleton = new HashMap<>();
    this.rand = new Random();
    this.start_pos = start_pos;
  }

  public void clearSkeleton() {
    next_rooms.clear();
    skeleton.clear();
  }

  public void generateSkeleton() {
    clearSkeleton();

    NextRoomInfo start_info = new NextRoomInfo();
    start_info.pos = start_pos;
    start_info.zone = zone_pool;
    start_info.from = -1;
    next_rooms.offer(start_info);

    while (!next_rooms.isEmpty()) {
      generateNextRoom(next_rooms.peek());
      next_rooms.poll();
    }
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

    System.out.println("[Generator] Zone: " + mark.zone);

    skeleton.put(info.pos, mark);

    if (skeleton.size() >= ROOMS_LIMIT - 1) {
      mark.doors = new boolean[Room.MAX_DOORS_COUNT];
      mark.doors[info.from] = true;
      mark.lock_doors = new int[Room.MAX_DOORS_COUNT];
      return;
    }

    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (mark.lock_doors[i] != 0 && must_doors[i] != 1) {
        NextRoomInfo new_info = new NextRoomInfo();
        new_info.pos = Point.sum(info.pos, getRoomDeltaFromDoor(i));
        new_info.zone = ++zone_pool;
        new_info.from = (i + 2) % 4;
        next_rooms.offer(new_info);
      }
    }

    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (mark.doors[i] && must_doors[i] != 1) {
        NextRoomInfo new_info = new NextRoomInfo();
        new_info.pos = Point.sum(info.pos, getRoomDeltaFromDoor(i));
        new_info.zone = mark.zone;
        new_info.from = (i + 2) % 4;
        next_rooms.offer(new_info);
      }
    }
  }

  private boolean[] generateNewDoors(boolean[] doors, int[] must_doors) {
    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (must_doors[i] == 1) {
        doors[i] = true;
      } else if (must_doors[i] == -1) {
        doors[i] = false;
      } else if (!doors[i]) {
        doors[i] = (rand.nextInt(0, 10000) < 3000);
      }
    }
    return doors;
  }

  private int[] generateLockDoors(int[] lock_doors, boolean[] doors, int from) {
    for (int i = 0; i < Room.MAX_DOORS_COUNT; ++i) {
      if (from != i && doors[i] && rand.nextInt(10000) < 1000) {
        while(lock_doors[i] == 0) {
          lock_doors[i] = rand.nextInt();
        }
      }
    }
    return lock_doors;
  }
}