package com.deepdungeons.game.room;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.renderer.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Room implements Drawable {
  
  private final RoomWall wall;

  private final List<RoomDoor> doors;

  private boolean isActive;

  public Room(RoomWall wall) {
    this.wall = wall;
    this.doors = new ArrayList<>();
    this.isActive = true;
  }

  public void addDoor(RoomDoor door) {
    doors.add(door);
  }

  @Override
  public void draw(SpriteBatch batch) {
    if (!isActive) return;

    wall.draw(batch);
    for (RoomDoor door : doors) {
      door.draw(batch);
    }
  }

  @Override
  public void setActive(boolean value) {
    isActive = value;
    wall.setActive(value);
  }

  @Override
  public boolean getActive() {
    return isActive;
  }
}
