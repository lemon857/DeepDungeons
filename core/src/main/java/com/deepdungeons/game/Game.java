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
import com.deepdungeons.game.generator.Graph;
import com.deepdungeons.game.generator.GraphGenerator;
import com.deepdungeons.game.generator.MstGraphGenerator;
import com.deepdungeons.game.physics.*;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.Renderer;
import com.deepdungeons.game.room.*;
import com.deepdungeons.game.utils.LinearCameraMoveController;
import com.deepdungeons.game.utils.SimpleCameraMoveContorller;

public class Game implements Disposable {
  
  private static final float STEP = 1 / 128f;

  private final BaseRenderer renderer;

  private final Camera camera;

  private final World world;

  private final MoveController controller;

  private float accumulator;

  public Game(int width, int height) {
    System.out.printf("Game width: %d height: %d\n", width, height);

    world = new World(new Vector2(0f, 0f), false);

    PhysicsContactListener listener = new PhysicsContactListener();
    listener.addProcessor(new PlayerDoorCollisionProcessor());
    world.setContactListener(listener);

    camera = new OrthographicCamera(width, height);
    renderer = new PhysicsDebugRenderer(new Renderer(new SpriteBatch(), camera), world);

    Wall wall = new Wall(world, "textures/items/bone.png", new Vector2(240, 200), new Vector2(100, 100), false);
    renderer.addDrawable(wall);

    Player player = new Player(world, "textures/weapons/knife.png", new Vector2(-200, -200), new Vector2(100, 100));
    renderer.addDrawable(player);

//    controller = new LinearCameraMoveController(camera, 1 / 3f, new SmoothMoveController());
    controller = new SimpleCameraMoveContorller(camera, new SmoothMoveController());

    controller.setTarget(player);
    controller.setMaxSpeed(10);

    accumulator = 0;

    RoomsManager roomManager = new RoomsManager();
    renderer.addDrawable(roomManager);

    GraphGenerator graphGenerator = new MstGraphGenerator(5);

//    Graph graph = graphGenerator.generateGraph();

    Graph graph = new Graph(2);

    graph.addEdge(0, 1);
    graph.addEdge(0, 1);

    RoomConfiguration configuration = new RoomConfiguration(new Vector2(700, 700), 10);

    float padding = 20;

    configuration.addMirrorDoorConfiguration(
            new DoorConfiguration(new Vector2(350, padding), false, true),
            new DoorConfiguration(new Vector2(350, 700 - padding),false, false));

    configuration.addMirrorDoorConfiguration(
            new DoorConfiguration(new Vector2(padding, 350), true, true),
            new DoorConfiguration(new Vector2(700 - padding, 350),true, false));

    roomManager.createRoomsFromGraph(graph, world,"textures/wall.png", "textures/door.png", configuration);
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
