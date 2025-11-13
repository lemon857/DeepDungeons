package com.deepdungeons.game.utils;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.deepdungeons.game.physics.Movable;
import com.deepdungeons.game.physics.MoveController;
import com.deepdungeons.game.physics.PhysicsObject;

public class LinearCameraMoveController implements MoveController {

  private final Camera camera;
  private final MoveController controller;

  private final float frameSizeKoef;

  private Movable target;

  public LinearCameraMoveController(Camera camera, float frameSizeKoef, MoveController controller) {
    this.camera = camera;
    this.controller = controller;

    this.frameSizeKoef = frameSizeKoef;
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

    Vector2 targetPosition = PhysicsObject.MetersToPixels(target.getBody().getPosition());
    Vector2 cameraPosition = new Vector2(camera.position.x, camera.position.y);

    Vector2 requiredDelta = targetPosition.sub(cameraPosition);

    // System.out.printf("Req delta: x: %f, y: %f | Frame size: x: %f, y: %f\n",
    // requiredDelta.x, requiredDelta.y, camera.viewportWidth * frameSizeKoef, camera.viewportHeight * frameSizeKoef);

    if (Math.abs(requiredDelta.x) > camera.viewportWidth * frameSizeKoef
        || Math.abs(requiredDelta.y) > camera.viewportHeight * frameSizeKoef) {
      
      requiredDelta.scl(delta);

      camera.position.x += requiredDelta.x;
      camera.position.y += requiredDelta.y;
      camera.update();
    }
  }
}
