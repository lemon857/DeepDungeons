package com.deepdungeons.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;

public class Wall extends PhysicsObject {

  private Sprite sprite;

  public Wall(String spritePath, Vector2 position, Vector2 size, boolean isVertical) {
    super(BodyType.StaticBody);

    if (size.x < 0 || size.y < 0) {
      throw new IllegalArgumentException("Size mustn't be negative");
    }

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(size.x / 2f, size.y / 2f);
    setShape(shape, 4f, 0.5f);

    sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
    sprite.setPosition(position.x, position.y);
    sprite.setSize(size.x, size.y);
    if (isVertical) {
      sprite.rotate90(true);
    }

    body.setTransform(new Vector2(position.x + size.x / 2f, position.y + size.y / 2f), 0);
  }

  public final void draw(SpriteBatch batch) {
    sprite.draw(batch);
  }
}