package com.deepdungeons.game.renderer;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public final class Renderer implements BaseRenderer {

  private final SpriteBatch batch;
  private final FitViewport viewport;
  private final ArrayList<Drawable> drawables;

  public Renderer(SpriteBatch batch, float width, float height) {
    this.batch = batch;
    this.drawables = new ArrayList<>();
    this.viewport = new FitViewport(width, height);
  }

  @Override
  public void render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    
    batch.begin();

    for (Drawable drawable : drawables) {
      drawable.draw(batch);
    }

    batch.end();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  @Override
  public void addDrawable(Drawable drawable) {
    drawables.add(drawable);
  }

  @Override
  public void dispose() {
    batch.dispose();
  }
}
