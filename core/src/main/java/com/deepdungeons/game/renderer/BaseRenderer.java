package com.deepdungeons.game.renderer;

import com.badlogic.gdx.graphics.Camera;

public interface BaseRenderer {
  public void render();
  public void resize(int width, int height);
  public void dispose();
  public void addDrawable(Drawable drawable);
  public float getWidth();
  public float getHeight();
  public Camera getCamera();
}
