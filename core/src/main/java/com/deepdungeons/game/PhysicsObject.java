package com.deepdungeons.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;

public class PhysicsObject {
  protected Body body;

  protected Vector2 size;

  protected PhysicsObject(BodyDef.BodyType type) {
    this.size = new Vector2();
    BodyDef def = new BodyDef();
    def.type = type;
    body = Main.world.createBody(def);
  }

  protected void setShape(Shape shape, float density, float restitution) {
    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;

    body.createFixture(fixtureDef);
  }
}