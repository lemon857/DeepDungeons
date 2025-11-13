package com.deepdungeons.game.renderer;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public final class Renderer implements BaseRenderer {

  private final SpriteBatch batch;
  private final ArrayList<Drawable> drawables;
  private final Camera camera;

  public Renderer(SpriteBatch batch, Camera camera) {
    this.batch = batch;
    this.drawables = new ArrayList<>();
    this.camera = camera;
    this.camera.update();
  }

  @Override
  public Camera getCamera() {
    return camera;
  }

  @Override
  public void render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f);

    camera.update();
    batch.setProjectionMatrix(camera.combined);
    
    batch.begin();

    for (Drawable drawable : drawables) {
      drawable.draw(batch);
    }

    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    camera.update();
  }

  @Override
  public void addDrawable(Drawable drawable) {
    drawables.add(drawable);
  }

  @Override
  public float getWidth() {
    return camera.viewportWidth;
  }

  @Override
  public float getHeight() {
    return camera.viewportHeight;
  }

  @Override
  public void dispose() {
    batch.dispose();
  }
}
