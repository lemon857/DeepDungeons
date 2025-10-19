package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.utils.Activable;

public class PhysicsObject implements Activable {
  protected Body body;

  protected Vector2 size;

  protected boolean is_active;

  protected PhysicsObject(World world, BodyDef.BodyType type) {
    this.size = new Vector2();
    BodyDef def = new BodyDef();
    def.type = type;
    body = world.createBody(def);
    is_active = true;
  }

  protected void setShape(Shape shape, float density, float restitution) {
    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
    // fixtureDef.filter.categoryBits = 1;
		// fixtureDef.filter.maskBits = 1 << 2;

    body.createFixture(fixtureDef);
  }

  protected void setUserData(String data) {
    body.setUserData(data);
  }

  protected void setPosition(Vector2 pos) {
    body.setTransform(pos, 0);
  }
  
  protected void setPosition(float x, float y) {
    body.setTransform(x, y, 0);
  }

  @Override
  public void setActive(boolean value) {
    is_active = value;
    body.setActive(value);
  }

  @Override
  public boolean getActive() {
    return is_active;
  }
}