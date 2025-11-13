package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class SmoothMoveController implements MoveController {

  private final Vector2 direction;

  private Movable target;
  private float maxSpeed;

  public SmoothMoveController() {
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

    Body body = target.getBody();

    Vector2 vel = body.getLinearVelocity();
    float mass = body.getMass();

    if (!direction.isZero()) {
      direction.nor();

      float currentSpeed = vel.dot(direction);
      
      if (currentSpeed < maxSpeed) {
        float requiredAddSpeed = Math.max(maxSpeed - currentSpeed, delta);
        float requiredAccel = requiredAddSpeed / (10f * delta);

        float force = mass * requiredAccel;
        Vector2 forceVec = new Vector2(direction).scl(force);

        body.applyForceToCenter(forceVec, true);

        System.out.printf("Force: %f, Cur speed: %f, Delta vel: %f\n", force, currentSpeed, requiredAddSpeed);
      }
    } else if (!vel.isZero()) {
      float requireBrake = Math.max(0.5f * (vel.len()), 10f);

      Vector2 brake = new Vector2(vel).scl(-requireBrake * mass);

      body.applyForceToCenter(brake, true);
    }
  }
}
