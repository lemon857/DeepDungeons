package com.deepdungeons;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.deepdungeons.game.utils.Vector2d;

public class PhysicsObject {
  protected Body body;

  protected Vector2d pos;
  protected Vector2d size;

  protected PhysicsObject(BodyDef.BodyType type) {
    this.pos = new Vector2d();
    this.size = new Vector2d();
    BodyDef def = new BodyDef();
    def.type = type;
    // body = Main.world.createBody(def);
  }

  protected void setShape(Shape shape, float density, float restitution) {
    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;

    body.createFixture(fixtureDef);
  }

  public final void setVelocity(Vector2 vel) {
    body.setLinearVelocity(vel);
  }
}