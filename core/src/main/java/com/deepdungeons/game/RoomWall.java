package com.deepdungeons.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.renderer.Drawable;

public final class RoomWall implements Drawable {
  private final ArrayList<Wall> walls;

  private boolean is_active;

  public RoomWall(World world, String wall_texture_path) {
    walls = new ArrayList<>();
    is_active = true;

    walls.add(new Wall(world, wall_texture_path, new Vector2(Room.START_BORDER.x, 0), new Vector2(100, 2), false));
    walls.add(new Wall(world, wall_texture_path, new Vector2(Room.START_BORDER.x, Room.END_BORDER.y), new Vector2(100, 2), false));
    walls.add(new Wall(world, wall_texture_path, new Vector2(Room.START_BORDER.x, 2), new Vector2(2, 98), true));
    walls.add(new Wall(world, wall_texture_path, new Vector2(Room.END_BORDER.x + 1, 0), new Vector2(2, 98), true));
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
  public final void draw(SpriteBatch batch) {
    if (!is_active) return;

    for (Wall wall : walls) {
      wall.draw(batch);
    }
  }
}