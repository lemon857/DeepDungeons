package com.deepdungeons.game;

import java.util.HashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
  private SpriteBatch batch;
  private FitViewport viewport;

  private Stage stage;
  private Label room_id_label;

  private HashMap<Point, Room> rooms;

  private Point current_room_pos;
  private Room current_room;

  private Player player;
  private Label player_pos_label;

  private Label key_require_label;
  private Label info_label;

  private int req_door_id;

  @Override
  public void create() {
    batch = new SpriteBatch();
    viewport = new FitViewport(50, 50);
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    stage.addListener(new InputListener() {
      @Override
      public boolean keyUp(InputEvent event, int keycode) {
        switch (keycode) {
          case Input.Keys.UP:
            tryGoToNextRoom(0);  
            break;
          case Input.Keys.RIGHT:
            tryGoToNextRoom(1);
            break;
          case Input.Keys.DOWN:
            tryGoToNextRoom(2);
            break;
          case Input.Keys.LEFT:
            tryGoToNextRoom(3);
            break;
    
          default: return false;
        }
        return true;
      }
    });

    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    room_id_label = new Label("Room X: 0 Y: 0", skin);
    room_id_label.setPosition(370, 30);
    stage.addActor(room_id_label);

    player_pos_label = new Label("Player X: 0 Y: 0", skin);
    player_pos_label.setPosition(370, 10);
    stage.addActor(player_pos_label);

    key_require_label = new Label("-", skin);
    key_require_label.setPosition(10, 480);
    stage.addActor(key_require_label);

    info_label = new Label("-", skin);
    info_label.setPosition(370, 480);
    stage.addActor(info_label);

    rooms = new HashMap<>();

    Room new_room = new Room(new Point(0, 0), new int[]{1, 1, 1, 1});

    Key key = new_room.lockDoor(0);
    Key key2 = new_room.lockDoor(2);

    new_room.addItem(key);
    new_room.addItem(key2);

    current_room_pos = new_room.getPos();
    rooms.put(current_room_pos, new_room);

    player = new Player(24, 24);

    current_room = rooms.get(current_room_pos);

    req_door_id = -1;
  }

  @Override
  public void render() {
    input();
    logic();
    draw();
  }

  private void input() {
    float speed = 20f;
    float delta = Gdx.graphics.getDeltaTime();

    Point pos = new Point(player.getPos());

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      player.translate(0, -speed * delta);
      pos.y -= 1;
      // Top door
      if (pos.x >= Room.DOOR_OFFSET - 5 && pos.x <= Room.DOOR_OFFSET + 6 && pos.y < Room.DOOR_HEIGHT) {
        if (tryGoToNextRoom(0)) player.setY(Room.SCREEN_HEIGHT - Player.PLAYER_HEIGHT - Room.DOOR_HEIGHT - 1);
      }

    } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      player.translate(0, speed * delta);
      
      pos.y += 1;
      // Bottom door
      if (pos.x >= Room.DOOR_OFFSET - 5 && pos.x <= Room.DOOR_OFFSET + 6 && pos.y > Room.SCREEN_HEIGHT - Player.PLAYER_HEIGHT - Room.DOOR_HEIGHT - 1) {
        if (tryGoToNextRoom(2)) player.setY(Room.DOOR_HEIGHT);
      }
    }
    
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      player.translate(speed * delta, 0);

      pos.x += 1;
      // Right door
      if (pos.y >= Room.DOOR_OFFSET - 5 && pos.y <= Room.DOOR_OFFSET + 6 && pos.x > Room.SCREEN_WIDTH - Player.PLAYER_WIDTH - Room.DOOR_HEIGHT - 1) {
        if (tryGoToNextRoom(1)) player.setX(Room.DOOR_HEIGHT);
      }
      
    } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      player.translate(-speed * delta, 0);

      pos.x -= 1;
      // Left door
      if (pos.y >= Room.DOOR_OFFSET - 5 && pos.y <= Room.DOOR_OFFSET + 6 && pos.x < Room.DOOR_HEIGHT) {
        if (tryGoToNextRoom(3)) player.setX(Room.SCREEN_WIDTH - Player.PLAYER_WIDTH - Room.DOOR_HEIGHT - 1);
      }
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (req_door_id != -1) {
        if (Point.distance(player.getPos(), Room.GetDoorPosition(req_door_id)) < 4 && player.getItem() instanceof Key) {
          if (current_room.tryUnlockDoor(req_door_id, ((Key)player.getItem()).getKey())) {
            current_room.generateBackground();
            req_door_id = -1;
          }
        } else {
          req_door_id = -1;
        }
      } else {
        if (current_room.canGrabItem(player.getPos())) {
          if (player.isDropAvailable()) {
            Item item = player.dropItem();

            item.setPos(new Point(player.getPos()));
            current_room.addItem(item);
          }
          player.grabItem(current_room.grabItem());
          key_require_label.setText("Grabbed!");
          
        } else {
          if (player.isDropAvailable()) {
          Item item = player.dropItem();

          item.setPos(new Point(player.getPos()));
          current_room.addItem(item);
          }
          key_require_label.setText("Can't grab!");
        }
      }
    }
  }

  private void logic() {
    player.update();
    current_room.update();
    info_label.setText("Dis: " + current_room.distanceToNearestItem(player.getPos()));
    //info_label.setText("To door: " + Point.distance(player.getPos(), Room.GetDoorPosition(req_door_id)));
    Point pos = new Point(player.getPos());

    player_pos_label.setText("Player X: " + pos.x + " Y: " + pos.y);
  }

  private void draw() {
    ScreenUtils.clear(0f, 0f, 0f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    current_room.draw(batch);
    player.draw(batch);
    batch.end();

    stage.act(Gdx.graphics.getDeltaTime());
	  stage.draw();
  }

  @Override
  public void dispose() {
    batch.dispose();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  private boolean  tryGoToNextRoom(int door_id) {
    if (!rooms.get(current_room_pos).canGoNextRoom(door_id)) {
      int req = rooms.get(current_room_pos).getLockedDoorKey(door_id);
      if (req != 0) {
        key_require_label.setText("Require key: " + req);
        req_door_id = door_id;
      }
      return false;
    }

    Point new_pos = Point.sum(current_room_pos, Room.GetRoomDeltaFromDoor(door_id));
    System.out.printf("----------------------\nNext pos: x: %d y: %d\nContains: %b\n", new_pos.x, new_pos.y, rooms.containsKey(new_pos));

    if (!rooms.containsKey(new_pos)) {
      System.out.printf("Create room from: x: %d y: %d\n", current_room_pos.x, current_room_pos.y);

      current_room_pos = Point.sub(current_room_pos, Room.GetRoomDeltaFromDoor((door_id + 2) % 4));

      int[] must_doors = new int[4];
      for (int i = 0; i < 4; ++i) {
        Point cur_room = Point.sum(current_room_pos, Room.GetRoomDeltaFromDoor(i));
        if (rooms.containsKey(cur_room)) {
          System.out.println("Check pos: X: " + cur_room.x + " Y: " + cur_room.y + " Check door: " + ((i + 2) % 4) + " from: " + i + " res: " + rooms.get(cur_room).isDoorExist((i + 2) % 4));

          if (rooms.get(cur_room).isDoorExist((i + 2) % 4)) {
            must_doors[i] = 1;
          } else {
            must_doors[i] = -1;
          }
        } else {
          must_doors[i] = 0;
        }
      }

      Room new_room = new Room(current_room_pos, must_doors);

      rooms.put(current_room_pos, new_room);

    } else {
      current_room_pos = new_pos;
    }

    current_room = rooms.get(current_room_pos);

    rooms.get(current_room_pos).printDoors();
    room_id_label.setText("Room X: " + current_room_pos.x + " Y: " + current_room_pos.y);
    return true;
  }
}