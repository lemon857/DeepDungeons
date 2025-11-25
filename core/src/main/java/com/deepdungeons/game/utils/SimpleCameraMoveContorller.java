package com.deepdungeons.game.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.deepdungeons.game.physics.Movable;
import com.deepdungeons.game.physics.MoveController;
import com.deepdungeons.game.physics.PhysicsObject;

public class SimpleCameraMoveContorller implements MoveController {

  private final Camera camera;
  private final MoveController controller;

  private Movable target;

  public SimpleCameraMoveContorller(Camera camera, MoveController playerController) {
    this.camera = camera;
    this.controller = playerController;
  }

  @Override
  public void setMaxSpeed(float speed) {
    controller.setMaxSpeed(speed);
  }

  @Override
  public void setTarget(Movable movable) {
    target = movable;
    controller.setTarget(movable);
  }

  @Override
  public void setDirection(Vector2 direction) {
    controller.setDirection(direction);
  }

  @Override
  public void move(float delta) {
    controller.move(delta);

    Vector2 position = PhysicsObject.MetersToPixels(target.getBody().getPosition());
    camera.position.x = position.x;
    camera.position.y = position.y;
    camera.update();
  }
}
