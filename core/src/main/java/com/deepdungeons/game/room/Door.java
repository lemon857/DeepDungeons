package com.deepdungeons.game.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.physics.PhysicsObject;
import com.deepdungeons.game.renderer.Drawable;

public final class Door extends PhysicsObject implements Drawable {
  private Sprite sprite;

  public Door(World world, String spritePath, int door_number, Vector2 position, Vector2 size, boolean isVertical) {
    super(world, BodyType.StaticBody, position);

    if (size.x < 0 || size.y < 0) {
      throw new IllegalArgumentException("Size mustn't be negative");
    }

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(PixelsToMeters(size.x / 2f), PixelsToMeters(size.y / 2f));
    setShape(shape, 4f, 0.5f);

    is_active = true;
    sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
    sprite.setPosition(position.x - size.x / 2f, position.y - size.x / 2f);
    sprite.setSize(size.x, size.y);
    if (isVertical) {
      sprite.rotate90(true);
    }

    setUserData("door-" + door_number);
  }

  @Override
  public final void draw(SpriteBatch batch) {
    if (!is_active) return;
    sprite.draw(batch);
  }
}
