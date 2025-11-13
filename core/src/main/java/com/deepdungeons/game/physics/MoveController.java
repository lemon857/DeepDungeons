package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;

public interface MoveController {
  public void setMaxSpeed(float speed);
  public void setTarget(Movable movable);
  public void setDirection(Vector2 direction);
  public void move(float delta);
}
