package com.deepdungeons.game.renderer;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class DebugRenderer implements BaseRenderer {

  private final SpriteBatch batch;
  private final FitViewport viewport;
  private final ArrayList<Drawable> drawables;

  private final World world;
	private final Box2DDebugRenderer debugRenderer;
	private Matrix4 debugMatrix;

  public DebugRenderer(SpriteBatch batch, float width, float height, World world) {
    this.batch = batch;
    this.drawables = new ArrayList<>();
    this.viewport = new FitViewport(width, height);
    this.world = world;
    this.debugRenderer = new Box2DDebugRenderer();
    this.debugMatrix = new Matrix4();
  }

  @Override
  public void render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);

		debugMatrix = batch.getProjectionMatrix().cpy();
    
    batch.begin();

    for (Drawable drawable : drawables) {
      drawable.draw(batch);
    }

    batch.end();
    
    debugRenderer.render(world, debugMatrix);
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
    debugRenderer.dispose();
  }
}
