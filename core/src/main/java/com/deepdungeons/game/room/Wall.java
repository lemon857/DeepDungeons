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

public final class Wall extends PhysicsObject implements Drawable {

  private Sprite sprite;

  public Wall(World world, String spritePath, Vector2 position, Vector2 size, boolean isVertical) {
    super(world, BodyType.StaticBody);

    if (size.x < 0 || size.y < 0) {
      throw new IllegalArgumentException("Size mustn't be negative");
    }

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(size.x / 2f, size.y / 2f);
    setShape(shape, 4f, 0.5f);

    is_active = true;
    sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
    sprite.setPosition(position.x, position.y);
    sprite.setSize(size.x, size.y);
    if (isVertical) {
      sprite.rotate90(true);
    }

    setPosition(position.x + size.x / 2f, position.y + size.y / 2f);
    setUserData("wall");
  }

  @Override
  public final void draw(SpriteBatch batch) {
    if (!is_active) return;
    sprite.draw(batch);
  }
}