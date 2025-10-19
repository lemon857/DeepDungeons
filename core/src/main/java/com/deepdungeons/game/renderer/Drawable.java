package com.deepdungeons.game.renderer;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.utils.Activable;

public interface Drawable extends Activable {
  public void draw(SpriteBatch batch);
}
