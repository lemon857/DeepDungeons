package com.deepdungeons.game.room;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public final class SquareRoomWall extends RoomWall {
  private final ArrayList<Wall> walls;

  public SquareRoomWall(World world, String wall_texture_path, Vector2 from, Vector2 size) {
    walls = new ArrayList<>();
    isActive = true;

    // left down corner
    walls.add(new Wall(world, wall_texture_path, new Vector2(from.x - size.y / 2f + size.x / 2f, from.y), new Vector2(size.x - size.y, size.y), false));
    walls.add(new Wall(world, wall_texture_path, new Vector2(from.x + size.y / 2f, from.y + size.x / 2f), new Vector2(size.y, size.x - size.y), true));

    // right up corner
    walls.add(new Wall(world, wall_texture_path, new Vector2(from.x + size.y / 2f + size.x / 2f, from.y + size.x - size.y), new Vector2(size.x - size.y, size.y), false));
    walls.add(new Wall(world, wall_texture_path, new Vector2(from.x - size.y / 2f + size.x, from.y + size.x / 2f - size.y), new Vector2(size.y, size.x - size.y), true));
  }


  @Override
  public final void draw(SpriteBatch batch) {
    if (!isActive) return;

    for (Wall wall : walls) {
      wall.draw(batch);
    }
  }
}