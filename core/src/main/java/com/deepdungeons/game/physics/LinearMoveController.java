package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;

public class LinearMoveController implements MoveController {

  private final Vector2 direction;

  private float maxSpeed;
  private Movable target;

  public LinearMoveController() {
    direction = new Vector2();
  }

  @Override
  public void setMaxSpeed(float speed) {
    maxSpeed = speed;
  }

  @Override
  public void setTarget(Movable movable) {
    target = movable;
  }

  @Override
  public void setDirection(Vector2 direction) {
    this.direction.set(direction);
  }

  @Override
  public void move(float delta) {
    if (target == null) return;

    if (!direction.isZero()) {
      direction.nor();

      target.getBody().setLinearVelocity(direction.scl(maxSpeed));
      
//       System.out.printf("Cur vel: %f\n", target.getBody().getLinearVelocity().len());

    } else {
      target.getBody().setLinearVelocity(direction);
    }
  }  
}
