package com.deepdungeons.game;

import java.util.HashMap;
import java.util.Random;

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
import com.deepdungeons.game.items.CommonItemForCraft;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.items.Key;
import com.deepdungeons.game.mobs.Mob;
import com.deepdungeons.game.mobs.Skeleton;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Vector2d;
import com.deepdungeons.game.weapons.CloseRangeWeapon;
import com.deepdungeons.game.weapons.Hand;
import com.deepdungeons.game.weapons.Knife;

// ! WARNING ! 
// IDK why, start coords on Pixmap located at Up-Left corner,
// but start coords when draw texture located at Down-Left corner
// for minimize misunderstanding I'll correct all of Pixmap drawing
// with mirroring it
// here always 
// X from left to right
// Y from down to up
// But if load Pixmap from file all is correct,
// I fix it with crutch - flag for check loaded from file

public class Main extends ApplicationAdapter {
  private SpriteBatch batch;
  private FitViewport viewport;

  private Stage stage;

  private HashMap<Point, Room> rooms;

  private Point current_room_pos;
  private Room current_room;

  public static Player player;
  private boolean is_pause;
  private double timer;
  private double cooldown;

  private int req_door_id;

  private Key generate_key_require;
  private int generate_key_chance;

  private Label die_info;
  private Label die_tip;

  private Random rand;

  private boolean show_item_info;

  private Label[] debug_info;
  private static final Point DEBUG_START = new Point(10, 10);
  private static final int DEBUG_Y_STEP = 15;

  private static final int DEBUG_LINES = 6;

  private static final int DEBUG_LINE_INFO = 0;
  private static final int DEBUG_LINE_DISTANCE = 1;
  private static final int DEBUG_LINE_ANGLE = 2;
  private static final int DEBUG_LINE_PLAYER_POS = 3;
  private static final int DEBUG_LINE_ROOM_POS = 4;
  private static final int DEBUG_LINE_ITEM_NAME = 5;

  public static CommonItemForCraft[] static_items;
  public static final int BONE_ITEM = 0;
  public static final int ROW_ITEM = 1;
  public static final int LEATHER_ITEM = 2;

  @Override
  public void create() {
    rand = new Random();
    batch = new SpriteBatch();
    viewport = new FitViewport(Room.SCREEN_WIDTH, Room.SCREEN_HEIGHT);
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

    static_items = new CommonItemForCraft[3];
    static_items[BONE_ITEM] = new CommonItemForCraft("bone.png", "bone");
    static_items[ROW_ITEM] = new CommonItemForCraft("row.png", "row");
    static_items[LEATHER_ITEM] = new CommonItemForCraft("leather.png", "leather");

    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    debug_info = new Label[DEBUG_LINES];
    int current_y = DEBUG_START.y;
    for (int i = 0; i < DEBUG_LINES; ++i) {
      debug_info[i] = new Label("---", skin);
      debug_info[i].setPosition(DEBUG_START.x, current_y);
      stage.addActor(debug_info[i]);
      current_y += DEBUG_Y_STEP;
    }

    die_info = new Label("You die", skin);
    die_info.setPosition(1500 / 2 - 360, 1000 / 2);
    die_info.setColor(1, 0, 0, 1);
    die_info.setFontScale(15);
    stage.addActor(die_info);

    die_tip = new Label("Press SPACE to restart", skin);
    die_tip.setPosition(1500 / 2 - 200, 1000 / 2 - 200);
    die_tip.setColor(1, 1, 1, 1);
    die_tip.setFontScale(3);
    stage.addActor(die_tip);

    debug_info[DEBUG_LINE_ITEM_NAME].setPosition(DEBUG_START.x, 100);
    
    debug_info[DEBUG_LINE_ITEM_NAME].setColor(1, 0, 1, 1);
    debug_info[DEBUG_LINE_ITEM_NAME].setFontScale(3);

    start();
  }

  private void start() {
    die_info.setVisible(false);
    die_tip.setVisible(false);

    player = new Player(20, 20);

    rooms = new HashMap<>();

    Room new_room = new Room(new Point(0, 0), new int[]{1, 1, 1, 1});
    Key key = new_room.lockDoor(0);
    generate_key_require = new_room.lockDoor(2);
    generate_key_chance = 10;
    new_room.addItem(key);

    int count_new_mobs = rand.nextInt(10);

    for (int i = 0; i < count_new_mobs; ++i) {
      Mob mob = new Skeleton();
      if (rand.nextInt(10000) < 800) {
        mob.pickUpItem(new Knife());
      }
      new_room.addMob(mob);
    }

    new_room.addItem(new Knife(new Point(Room.START_BORDER.x + 10, Room.START_BORDER.y + 10)));

    new_room.addItem(new CommonItemForCraft(static_items[BONE_ITEM], new Point(Room.START_BORDER.x + 50, Room.START_BORDER.y + 50)));
    new_room.addItem(new CommonItemForCraft(static_items[ROW_ITEM], new Point(Room.START_BORDER.x + 60, Room.START_BORDER.y + 60)));
    new_room.addItem(new CommonItemForCraft(static_items[LEATHER_ITEM], new Point(Room.START_BORDER.x + 70, Room.START_BORDER.y + 70)));

    current_room_pos = new_room.getPos();
    rooms.put(current_room_pos, new_room);

    current_room = rooms.get(current_room_pos);
    debug_info[DEBUG_LINE_ROOM_POS].setText("Room X: " + current_room_pos.x + " Y: " + current_room_pos.y);

    debug_info[DEBUG_LINE_ITEM_NAME].setText("");

    req_door_id = -1;
    timer = 0;
    cooldown = 0;
    show_item_info = false;
    is_pause = false;
  }

  @Override
  public void render() {
    input();
    logic();
    draw();
  }

  private void input() {
    if (is_pause) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
        start();
      }
      
      if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
        is_pause = false;
      }

      return;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      is_pause = true;
    }
    float speed = 40f;
    float delta = Gdx.graphics.getDeltaTime();

    Point pos = new Point(player.getPos());
    Vector2d vector = new Vector2d();

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      pos.y += 1;
      vector.y += speed * delta;
      // Top door
      if (pos.x >= Room.DOOR_OFFSET.x - 3 && pos.x <= Room.DOOR_OFFSET.x + 4 && pos.y > Room.END_BORDER.y - Player.HEIGHT - Room.DOOR_HEIGHT) {
        if (tryGoToNextRoom(0)) player.setY(Room.START_BORDER.y + Room.DOOR_HEIGHT);
      }

    } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      pos.y -= 1;
      vector.y -= speed * delta;
      // Bottom door
      if (pos.x >= Room.DOOR_OFFSET.x - 3 && pos.x <= Room.DOOR_OFFSET.x + 4 && pos.y < Room.START_BORDER.y + Room.DOOR_HEIGHT + 1) {
        if (tryGoToNextRoom(2)) player.setY(Room.END_BORDER.y - Player.HEIGHT - Room.DOOR_HEIGHT - 1);
      }
    }
    
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      pos.x += 1;
      vector.x += speed * delta;
      // Right door
      if (pos.y >= Room.DOOR_OFFSET.y - 3 && pos.y <= Room.DOOR_OFFSET.y + 4 && pos.x > Room.END_BORDER.x - Player.WIDTH - Room.DOOR_HEIGHT - 1) {
        if (tryGoToNextRoom(1)) player.setX(Room.START_BORDER.x + Room.DOOR_HEIGHT);
      }
      
    } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      pos.x -= 1;
      vector.x -= speed * delta;
      // Left door
      if (pos.y >= Room.DOOR_OFFSET.y - 3 && pos.y <= Room.DOOR_OFFSET.y + 4 && pos.x < Room.START_BORDER.x + Room.DOOR_HEIGHT + 1) {
        if (tryGoToNextRoom(3)) player.setX(Room.END_BORDER.x - Player.WIDTH - Room.DOOR_HEIGHT - 1);
      }
    }

    player.translate(vector);
    
    // Use (pick up, put down, open door)
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (req_door_id != -1) {
        if (Point.distance(player.getCenterPos(), Room.GetDoorPosition(req_door_id)) < 7 && player.getItem() instanceof Key) {
          if (current_room.tryUnlockDoor(req_door_id, ((Key)player.getItem()).getKey())) {
            current_room.generateBackground();
            req_door_id = -1;
          }
        } else {
          req_door_id = -1;
        }
      } else {
        if (current_room.canGrabItem(player.getCenterPos())) {
          if (player.isDropAvailable()) {
            Item item = player.dropItem();

            item.setCenterPos(new Point(player.getCenterPos()));
            current_room.addItem(item);
          }
          player.pickupItem(current_room.grabItem());
          debug_info[DEBUG_LINE_INFO].setText("Grabbed!");
          
        } else {
          if (player.isDropAvailable()) {
          Item item = player.dropItem();

          item.setCenterPos(new Point(player.getCenterPos()));
          current_room.addItem(item);
          }
          debug_info[DEBUG_LINE_INFO].setText("Can't grab!");
        }
      }
    }

    timer += delta;
    // Attack
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      if (player.getItem() instanceof CloseRangeWeapon) {
        if (timer >= cooldown) {
          CloseRangeWeapon weapon = (CloseRangeWeapon)player.getItem();
          if (current_room.tryHitMob(player.getPos(), player.getDirection(), weapon.getDamage(), weapon.getDistance(), weapon.getAngle())) {
            debug_info[DEBUG_LINE_INFO].setText("Hitted");
            cooldown = weapon.getCooldown();
            timer = 0;
          } else {
            debug_info[DEBUG_LINE_INFO].setText("No hit");
          }
        } else {
          debug_info[DEBUG_LINE_INFO].setText("Time left: " + (cooldown - timer));
        }
      } else if (player.getItem() == null) { // aka hand
        if (timer >= cooldown) {
          if (current_room.tryHitMob(player.getPos(), player.getDirection(), Hand.getDamage(rand), Hand.DISTANCE, Hand.ANGLE)) {
            debug_info[DEBUG_LINE_INFO].setText("Hitted");
            cooldown = Hand.getCooldown(rand);
            timer = 0;
          } else {
            debug_info[DEBUG_LINE_INFO].setText("No hit");
          }
        } else {
          debug_info[DEBUG_LINE_INFO].setText("Time left: " + (cooldown - timer));
        }
      }
    }

    // [DEBUG] Spawn new mob
    if (Gdx.input.isKeyJustPressed(Input.Keys.T)) {
      current_room.addMob(new Skeleton(new Vector2d(player.getPos())));
    }

    // [DEBUG] Self damage
    if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
      player.damage(0.5);
    }
    
    // [DEBUG] Self treat
    if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
      player.treat(1);
    }

    // [DEBUG] Self hunger
    if (Gdx.input.isKeyJustPressed(Input.Keys.B)) {
      player.hunger(1);
    }
    
    // [DEBUG] Self saturation
    if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
      player.saturation(1);
    }

    // [DEBUG] Self thirst
    if (Gdx.input.isKeyJustPressed(Input.Keys.J)) {
      player.thirst(1);
    }
    
    // [DEBUG] Self drink
    if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
      player.drink(1);
    }
  }

  private void logic() {
    if (is_pause) return;
    
    double delta = Gdx.graphics.getDeltaTime();
    player.update(delta);
    current_room.update(delta);
    //debug_info[DEBUG_LINE_DISTANCE].setText("Dis to door: " + Point.distance(player.getCenterPos(), Room.GetDoorPosition(req_door_id)));
    //debug_info[DEBUG_LINE_DISTANCE].setText("Dis to item: " + current_room.distanceToNearestItem(player.getCenterPos()));
    debug_info[DEBUG_LINE_DISTANCE].setText("Dis to mob: " + current_room.distanceToNearestMob(player.getCenterPos()));
    debug_info[DEBUG_LINE_ANGLE].setText("Angle to mob: " + current_room.angleToNearestMob(player.getCenterPos(), player.getDirection()));
    Point pos = new Point(player.getPos());

    debug_info[DEBUG_LINE_PLAYER_POS].setText("Player X: " + pos.x + " Y: " + pos.y);

    if (current_room.distanceToNearestItem(player.getCenterPos()) > Room.PICK_UP_MAX_DISTANCE && show_item_info) {
      debug_info[DEBUG_LINE_ITEM_NAME].setVisible(false);
      show_item_info = false;
    } else if (current_room.distanceToNearestItem(player.getCenterPos()) <= Room.PICK_UP_MAX_DISTANCE && !show_item_info) {
      Item item = current_room.getNearestItem(player.getCenterPos());
      debug_info[DEBUG_LINE_ITEM_NAME].setVisible(true);
      debug_info[DEBUG_LINE_ITEM_NAME].setText(item.getName());
      show_item_info = true;
    }

    if (player.isDie()) {
      die_info.setVisible(true);
      die_tip.setVisible(true);
      is_pause = true;
    }
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
        debug_info[DEBUG_LINE_INFO].setText("Require key: " + req);
        req_door_id = door_id;
      }
      return false;
    }

    Point new_pos = Point.sum(current_room_pos, Room.GetRoomDeltaFromDoor(door_id));

    if (!rooms.containsKey(new_pos)) {

      current_room_pos = Point.sub(current_room_pos, Room.GetRoomDeltaFromDoor((door_id + 2) % 4));

      int[] must_doors = new int[4];
      for (int i = 0; i < 4; ++i) {
        Point cur_room = Point.sum(current_room_pos, Room.GetRoomDeltaFromDoor(i));
        if (rooms.containsKey(cur_room)) {
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

      if (generate_key_require != null) {
        if (rand.nextInt(100) < generate_key_chance) {
          new_room.addItem(generate_key_require);
          generate_key_require = null;
        } else {
          ++generate_key_chance;
        }
      }

      int count_new_mobs = rand.nextInt(10);

      for (int i = 0; i < count_new_mobs; ++i) {
        Mob mob = new Skeleton();
        if (rand.nextInt(10000) < 800) {
          mob.pickUpItem(new Knife());
        }
        new_room.addMob(mob);
      }

      rooms.put(current_room_pos, new_room);

    } else {
      current_room_pos = new_pos;
    }

    current_room = rooms.get(current_room_pos);

    debug_info[DEBUG_LINE_ROOM_POS].setText("Room X: " + current_room_pos.x + " Y: " + current_room_pos.y);
    return true;
  }
}