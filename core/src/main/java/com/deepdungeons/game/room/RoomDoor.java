package com.deepdungeons.game.room;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.characters.Player;
import com.deepdungeons.game.physics.PhysicsObject;
import com.deepdungeons.game.renderer.Drawable;

public final class RoomDoor extends PhysicsObject implements Drawable, Door {

  private final Sprite sprite;
  private final Vector2 directionToOut;

  private Door door;

  public RoomDoor(World world, String spritePath, Vector2 position, Vector2 pixelSize, boolean isVertical, boolean isRight) {
    super(world, BodyType.StaticBody, position);

    if (pixelSize.x < 0 || pixelSize.y < 0) {
      throw new IllegalArgumentException("Size mustn't be negative");
    }

    PolygonShape shape = new PolygonShape();
    shape.setAsBox(PixelsToMeters(pixelSize.x / 2f), PixelsToMeters(pixelSize.y / 2f));
    setShape(shape, 4f, 0.5f);

    is_active = true;
    sprite = new Sprite(new Texture(Gdx.files.internal(spritePath)));
    sprite.setPosition(position.x - pixelSize.x / 2f, position.y - pixelSize.y / 2f);
    sprite.setSize(pixelSize.x, pixelSize.y);
    if (isVertical) {
      sprite.rotate90(true);
    }
    this.door = null;
    if (isVertical) {
      this.directionToOut = new Vector2(isRight ? 1 : -1, 0);
    } else {
      this.directionToOut = new Vector2(0, isRight ? 1 : -1);
    }

    setUserData(this);
  }

  @Override
  public final void draw(SpriteBatch batch) {
    if (!is_active) return;
    sprite.draw(batch);
  }

  @Override
  public void comeIn(Player player) {
    System.out.printf("Collision with player! %s\n", player.getClass().getCanonicalName());

    if (door == null) return;
    door.comeOut(player);
  }

  @Override
  public void comeOut(Player player) {
    player.requestChangePosition(getPixelPosition().add(new Vector2(directionToOut).scl(sprite.getY() * -1.5f)), directionToOut);
  }

  @Override
  public void setDoorPair(Door other) {
    door = other;
  }
}
