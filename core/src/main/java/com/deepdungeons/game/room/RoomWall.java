package com.deepdungeons.game.room;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.renderer.Drawable;

public abstract class RoomWall implements  Drawable {

  protected boolean isActive;

  @Override
  public final void setActive(boolean value) {
    isActive = value;
  }

  @Override
  public final boolean getActive() {
    return isActive;
  }

  @Override
  public abstract void draw(SpriteBatch batch);
}
