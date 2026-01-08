package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;

public interface Transformable {
  void setPosition(Vector2 position);
  Vector2 getPosition();

  void setPixelPosition(Vector2 position);
  Vector2 getPixelPosition();
}
