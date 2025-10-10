package com.deepdungeons.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class RoomWall {
  private final ArrayList<Wall> walls;

  public RoomWall(String wall_texture_path) {
    walls = new ArrayList<>();

    walls.add(new Wall(wall_texture_path, new Vector2(Room.START_BORDER.x, 0), new Vector2(100, 2), false));
    walls.add(new Wall(wall_texture_path, new Vector2(Room.START_BORDER.x, Room.END_BORDER.y), new Vector2(100, 2), false));
    walls.add(new Wall(wall_texture_path, new Vector2(Room.START_BORDER.x, 2), new Vector2(2, 98), true));
    walls.add(new Wall(wall_texture_path, new Vector2(Room.END_BORDER.x + 1, 0), new Vector2(2, 98), true));
  }

  public void draw(SpriteBatch batch) {
    for (Wall wall : walls) {
      wall.draw(batch);
    }
  }
}