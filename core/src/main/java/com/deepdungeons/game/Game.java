package com.deepdungeons.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.deepdungeons.game.characters.Player;
import com.deepdungeons.game.physics.MoveController;
import com.deepdungeons.game.physics.PhysicsDebugRenderer;
import com.deepdungeons.game.physics.SmoothMoveController;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.Renderer;
import com.deepdungeons.game.room.Wall;

public class Game implements Disposable {
  
  private static final float STEP = 1/128f;

  private final BaseRenderer renderer;

  private final World world;

  private final Wall wall;

  private final Player player;

  private final MoveController controller;

  private float accumulator;

  public Game(int width, int height) {
    world = new World(new Vector2(0f, 0f), false);
    // renderer = new Renderer(new SpriteBatch(), new OrthographicCamera(width, height));
    renderer = new PhysicsDebugRenderer(new Renderer(new SpriteBatch(), new OrthographicCamera(width, height)), world);

    wall = new Wall(world, "textures/items/bone.png", new Vector2(100, 100), new Vector2(100, 100), false);
    renderer.addDrawable(wall);

    player = new Player(world, "textures/weapons/knife.png", new Vector2(0, 0), new Vector2(100, 100));
    renderer.addDrawable(player);

    controller = new SmoothMoveController();
    // controller = new LinearMoveController();
    controller.setTarget(player);
    controller.setMaxSpeed(5);

    accumulator = 0;
  }

  public void input() {
    Vector2 direction = new Vector2(0f, 0f);
    if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
      direction.x -= 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
      direction.x += 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
      direction.y -= 1;
		}
		if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
      direction.y += 1;
		}
    controller.setDirection(direction);
  }

  public void logic() {
    float frameTime = Math.min(Gdx.graphics.getDeltaTime(), STEP);

    controller.move(frameTime);

    accumulator += frameTime;
    while (accumulator >= STEP) {
      world.step(STEP, 10, 5);
      accumulator -= STEP;
    }
  }

  public void draw() {
    renderer.render();
  }

  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  @Override
  public void dispose() {
    renderer.dispose();
  }
}
