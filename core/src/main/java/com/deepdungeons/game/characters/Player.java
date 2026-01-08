package com.deepdungeons.game.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.physics.Movable;
import com.deepdungeons.game.physics.PhysicsObject;
import com.deepdungeons.game.renderer.Drawable;
import com.deepdungeons.game.utils.Constants;

public class Player extends PhysicsObject implements Drawable, Movable {

  private final Sprite sprite;

  private final Vector2 pixelSize;

  public Vector2 newPos;

  public Player(World world, String spritePath, Vector2 position, Vector2 size) {
    super(world, BodyType.DynamicBody, position);

    if (size.x < 0 || size.y < 0) {
      throw new IllegalArgumentException("Size mustn't be negative");
    }

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(PixelsToMeters(size.x / 2f), PixelsToMeters(size.y / 2f));
    setShape(shape, 4f, 0.5f, Constants.PHYSICS_ENTITY_CATEGORY_BITS, Constants.PLAYER_MASK_BITS);

    setMassData(new Vector2(), 70, 10);

    is_active = true;

    sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
    sprite.setPosition(position.x, position.y);
    sprite.setSize(size.x, size.y);

    this.pixelSize = size;
    this.newPos = null;

    setUserData(this);
  }

  @Override
  public void draw(SpriteBatch batch) {
    if (newPos != null) {
      setPixelPosition(newPos);
      clearBodyVelocity();
      newPos = null;
    }
    if (!is_active) return;

//    System.out.printf("New sprite pos: %f, %f\n", getPosition().x - size.x / 2f, getPosition().y - size.y / 2f);
    sprite.setPosition(getPixelPosition().x - pixelSize.x / 2f, getPixelPosition().y - pixelSize.y / 2f);

    sprite.draw(batch);
  }

  @Override
  public Body getBody() {
    return body;
  }

  public void requestChangePosition(Vector2 pixelPosition, Vector2 direction) {
    newPos = pixelPosition.add(new Vector2(pixelSize.x * direction.x / 2f, pixelSize.y * direction.y / 2f));
  }
}
