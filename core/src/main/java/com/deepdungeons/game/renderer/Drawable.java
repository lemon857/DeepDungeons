package com.deepdungeons.game.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Drawable {
  public void draw(SpriteBatch batch);
  public void setActive(boolean value);
}
