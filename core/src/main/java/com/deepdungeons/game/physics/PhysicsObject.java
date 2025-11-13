package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.utils.Activable;
import com.deepdungeons.game.utils.Constants;

public class PhysicsObject implements Activable, Transformable {
  protected Body body;

  protected boolean is_active;

  public static float PixelsToMeters(float pixels) {
    return pixels / Constants.PIXELS_PER_METER;
  }

  public static Vector2 PixelsToMeters(Vector2 pixels) {
    return new Vector2(pixels).scl(1f / Constants.PIXELS_PER_METER);
  }

  public static float MetersToPixels(float meters) {
    return meters * Constants.PIXELS_PER_METER;
  }

  public static Vector2 MetersToPixels(Vector2 meters) {
    return new Vector2(meters).scl(Constants.PIXELS_PER_METER);
  }

  protected PhysicsObject(World world, BodyDef.BodyType type, Vector2 position) {
    BodyDef def = new BodyDef();
    def.type = type;
    def.fixedRotation = true;
    def.bullet = true;
    def.position.set(PixelsToMeters(position));
    body = world.createBody(def);
    is_active = true;
  }

  protected void setShape(Shape shape, float density, float restitution) {
    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;

    body.createFixture(fixtureDef);
  }

  protected void setShape(Shape shape, float density, float restitution, short categoryBits, short maskBits) {
    FixtureDef fixtureDef = new FixtureDef();

    fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.restitution = restitution;
    fixtureDef.filter.categoryBits = categoryBits;
		fixtureDef.filter.maskBits = maskBits;

    body.createFixture(fixtureDef);
  }

  protected void setMassData(Vector2 center, float mass, float I) {
    MassData data = new MassData();
    data.center.set(center);
    data.mass = mass;
    data.I = I;
    body.setMassData(data);
  }

  protected void setUserData(Object data) {
    body.setUserData(data);
  }

  protected Object getUserData() {
    return body.getUserData();
  }
  
  protected void setPosition(float x, float y) {
    body.setTransform(PixelsToMeters(x), PixelsToMeters(y), 0);
  }

  @Override
  public final void setPosition(Vector2 pos) {
    body.setTransform(PixelsToMeters(pos), 0);
  }

  @Override
  public final Vector2 getPosition() {
    return MetersToPixels(body.getPosition());
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