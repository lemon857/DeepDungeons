package com.deepdungeons.game.physics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.Drawable;
import com.deepdungeons.game.utils.Constants;

public final class PhysicsDebugRenderer implements BaseRenderer {

  private final BaseRenderer renderer;

  private final World world;
  private final Box2DDebugRenderer debugRenderer;
	private Matrix4 debugMatrix;

  public PhysicsDebugRenderer(BaseRenderer renderer, World world) {
    this.renderer = renderer;
    this.world = world;
    this.debugRenderer = new Box2DDebugRenderer();
    this.debugMatrix = new Matrix4();
  }

  @Override
  public Camera getCamera() {
    return renderer.getCamera();
  }

  @Override
  public void render() {
    renderer.render();

		debugMatrix = renderer.getCamera().combined.cpy();
    debugMatrix.scl(Constants.PIXELS_PER_METER, Constants.PIXELS_PER_METER, 1f);
    
    debugRenderer.render(world, debugMatrix);
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  @Override
  public void addDrawable(Drawable drawable) {
    renderer.addDrawable(drawable);
  }

  @Override
  public float getWidth() {
    return renderer.getWidth();
  }

  @Override
  public float getHeight() {
    return renderer.getHeight();
  }

  @Override
  public void dispose() {
    renderer.dispose();
    debugRenderer.dispose();
  }
}
