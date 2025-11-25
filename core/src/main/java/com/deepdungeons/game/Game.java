package com.deepdungeons.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
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
import com.deepdungeons.game.room.Room;
import com.deepdungeons.game.room.SquareRoomWall;
import com.deepdungeons.game.room.Wall;
import com.deepdungeons.game.utils.LinearCameraMoveController;
import com.deepdungeons.game.utils.SimpleCameraMoveContorller;
import com.deepdungeons.game.utils.SmoothCameraMoveController;

public class Game implements Disposable {
  
  private static final float STEP = 1 / 128f;

  private final BaseRenderer renderer;

  private final Camera camera;

  private final World world;

  private final Wall wall;

  private final Player player;

  private final MoveController controller;

  private final Room room;

  private float accumulator;

  public Game(int width, int height) {
    System.out.printf("Game width: %d height: %d\n", width, height);

    world = new World(new Vector2(0f, 0f), false);

    camera = new OrthographicCamera(width, height);
    // renderer = new Renderer(new SpriteBatch(), camera));
    renderer = new PhysicsDebugRenderer(new Renderer(new SpriteBatch(), camera), world);

    wall = new Wall(world, "textures/items/bone.png", new Vector2(200, 200), new Vector2(100, 100), false);
    renderer.addDrawable(wall);

    player = new Player(world, "textures/weapons/knife.png", new Vector2(0, 0), new Vector2(100, 100));
    renderer.addDrawable(player);

    controller = new SmoothCameraMoveController(camera, 1 / 3f, new SmoothMoveController());
//    controller = new SimpleCameraMoveContorller(camera, new SmoothMoveController());
    // controller = new LinearMoveController();
    controller.setTarget(player);
    controller.setMaxSpeed(10);

    accumulator = 0;

    room = new Room(new SquareRoomWall(world, "textures/wall.png", new Vector2(-350, -350), new Vector2(700, 10)));
    renderer.addDrawable(room);
  }

  public void input() {
    Vector2 direction = new Vector2(0f, 0f);
    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      direction.x -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      direction.x += 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      direction.y -= 1;
    }
    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
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
    camera.viewportWidth = width;
    camera.viewportHeight = height;
    camera.update();
  }

  @Override
  public void dispose() {
    renderer.dispose();
  }
}
