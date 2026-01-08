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
import com.deepdungeons.game.physics.*;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.Renderer;
import com.deepdungeons.game.room.*;
import com.deepdungeons.game.utils.LinearCameraMoveController;

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

    PhysicsContactListener listener = new PhysicsContactListener();
    listener.addProcessor(new PlayerDoorCollisionProcessor());
    world.setContactListener(listener);

    camera = new OrthographicCamera(width, height);
    renderer = new PhysicsDebugRenderer(new Renderer(new SpriteBatch(), camera), world);

    wall = new Wall(world, "textures/items/bone.png", new Vector2(240, 200), new Vector2(100, 100), false);
    renderer.addDrawable(wall);

    player = new Player(world, "textures/weapons/knife.png", new Vector2(-200, -200), new Vector2(100, 100));
    renderer.addDrawable(player);

    controller = new LinearCameraMoveController(camera, 1 / 3f, new SmoothMoveController());

    controller.setTarget(player);
    controller.setMaxSpeed(10);

    accumulator = 0;

    room = new Room(new SquareRoomWall(world, "textures/wall.png", new Vector2(-350, -350), new Vector2(700, 10)));
    renderer.addDrawable(room);

    RoomDoor door1 = new RoomDoor(world, "textures/door.png", new Vector2(500, 0), new Vector2(100, 10), false, true);
    RoomDoor door2 = new RoomDoor(world, "textures/door.png", new Vector2(0, 0), new Vector2(100, 10), false, false);

    door1.setDoorPair(door2);
    door2.setDoorPair(door1);

    room.addDoor(door1);
    room.addDoor(door2);
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
