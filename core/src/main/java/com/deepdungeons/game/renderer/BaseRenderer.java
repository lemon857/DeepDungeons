package com.deepdungeons.game.renderer;

public interface BaseRenderer {
  public void render();
  public void resize(int width, int height);
  public void dispose();
  public void addDrawable(Drawable drawable);
  public int getWidth();
  public int getHeight();
}
