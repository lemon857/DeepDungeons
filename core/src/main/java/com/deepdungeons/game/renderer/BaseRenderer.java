package com.deepdungeons.game.renderer;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;

public interface BaseRenderer extends Disposable {
  public void render();
  public void resize(int width, int height);
  public void addDrawable(Drawable drawable);
  public float getWidth();
  public float getHeight();
  public Camera getCamera();
}
