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
import com.deepdungeons.game.items.ItemForCraft;
import com.deepdungeons.game.items.Edible;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.items.Key;
import com.deepdungeons.game.mobs.DefaultMob;
import com.deepdungeons.game.mobs.Mob;
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

  private Label pause_info_label;
  private Label pause_tip_label;

  private static final String die_message = "YOU DIED";
  private static final String die_tip = "Press SPACE to restart";

  private static final String pause_message = "PAUSE";
  private static final String pause_tip = "Press ESC to resume\nPress SPACE to restart";

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

    Item.initStaticItems();
    Item.addStaticItem("forcraft/bone", new ItemForCraft(Item.Tier.Common, "items/bone.png", "bone"));
    Item.addStaticItem("forcraft/rope", new ItemForCraft(Item.Tier.Common, "items/rope.png", "rope"));
    Item.addStaticItem("forcraft/leather", new ItemForCraft(Item.Tier.Common, "items/leather.png", "leather"));

    Item.addStaticItem("foods/candy", new Edible(Item.Tier.Common, "items/candy.png", "candy", false, 2));
    Item.addStaticItem("foods/meat", new Edible(Item.Tier.Uncommon, "items/meat.png", "meat", false, 5));

    Item.addStaticItem("drinks/bottle_of_water", new Edible(Item.Tier.Common, "items/bottle_of_water.png", "bottle of water", true, 4));
    Item.addStaticItem("drinks/cup_of_tea", new Edible(Item.Tier.Uncommon, "items/cup_of_tea.png", "cup of tea", true, 7));

    Item.addStaticItem("weapons/knife", new Knife("weapons/knife.png"));

    Mob.initStaticMobs();
    Mob.addStaticMob("mobs/skeleton", new DefaultMob("mobs/skeleton.png", 25, 1, 3));

    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    debug_info = new Label[DEBUG_LINES];
    int current_y = DEBUG_START.y;
    for (int i = 0; i < DEBUG_LINES; ++i) {
      debug_info[i] = new Label("---", skin);
      debug_info[i].setPosition(DEBUG_START.x, current_y);
      stage.addActor(debug_info[i]);
      current_y += DEBUG_Y_STEP;
    }

    pause_info_label = new Label("", skin);
    pause_info_label.setPosition(1500 / 2 - 360, 1000 / 2);
    pause_info_label.setColor(1, 0, 0, 1);
    pause_info_label.setFontScale(15);
    stage.addActor(pause_info_label);

    pause_tip_label = new Label("", skin);
    pause_tip_label.setPosition(1500 / 2 - 200, 1000 / 2 - 200);
    pause_tip_label.setColor(1, 1, 1, 1);
    pause_tip_label.setFontScale(3);
    stage.addActor(pause_tip_label);

    debug_info[DEBUG_LINE_ITEM_NAME].setPosition(DEBUG_START.x, 100);
    
    debug_info[DEBUG_LINE_ITEM_NAME].setColor(1, 0, 1, 1);
    debug_info[DEBUG_LINE_ITEM_NAME].setFontScale(3);

    start();
  }

  private void start() {
    pause_info_label.setVisible(false);
    pause_tip_label.setVisible(false);

    player = new Player();

    rooms = new HashMap<>();

    Room new_room = new Room(new Point(0, 0), new int[]{1, 1, 1, 1});
    Key key = new_room.lockDoor(0);
    generate_key_require = new_room.lockDoor(2);
    generate_key_chance = 10;
    new_room.addItem(key);

    int count_new_mobs = rand.nextInt(10);

    for (int i = 0; i < count_new_mobs; ++i) {
      Mob mob = Mob.getStaticMob("mobs/skeleton");
      if (rand.nextInt(10000) < 800) {
        mob.pickUpItem("weapons/knife");
      }
      new_room.addMob(mob);
    }

    new_room.addItem("weapons/knife");

    new_room.addItem("forcraft/bone");
    new_room.addItem("forcraft/rope");
    new_room.addItem("forcraft/leather");

    new_room.addItem("foods/candy");
    new_room.addItem("foods/meat");

    new_room.addItem("drinks/bottle_of_water");
    new_room.addItem("drinks/cup_of_tea");

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
      
        pause_info_label.setVisible(false);
        pause_tip_label.setVisible(false);
      }

      return;
    }

    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
      pause_info_label.setText(pause_message);
      pause_tip_label.setText(pause_tip);

      pause_info_label.setVisible(true);
      pause_tip_label.setVisible(true);
      is_pause = true;
    }
    double speed = player.getMoveSpeed();
    double delta = Gdx.graphics.getDeltaTime();

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
        if (Point.distance(player.getCenterPos(), Room.GetDoorPosition(req_door_id)) < 7 && player.getItem().getType() == Item.Type.Key) {
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

            item.setCenterPos(player.getCenterPos());
            current_room.addItem(item);
          }
          player.pickupItem(current_room.grabItem());
          debug_info[DEBUG_LINE_INFO].setText("Grabbed!");
          
        } else {
          if (player.isDropAvailable()) {
          Item item = player.dropItem();

          item.setCenterPos(player.getCenterPos());
          current_room.addItem(item);
          }
          debug_info[DEBUG_LINE_INFO].setText("Can't grab!");
        }
      }
    }

    // Eat and drink
    if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
      player.tryUseEdibleItem();
    }

    timer += delta;
    // Attack
    if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
      if (player.getItem() == null) { // aka hand
        if (timer >= cooldown) {
          if (current_room.tryHitMob(player.getPos(), player.getDirection(), 
            Hand.getDamage(rand) * player.getStrength(), Hand.DISTANCE, Hand.ANGLE)) {
            player.startAttackAnim();
            debug_info[DEBUG_LINE_INFO].setText("Hitted");
            cooldown = Hand.getCooldown(rand) / player.getAttackSpeed();
            timer = 0;
          } else {
            debug_info[DEBUG_LINE_INFO].setText("No hit");
          }
        } else {
          debug_info[DEBUG_LINE_INFO].setText("Time left: " + (cooldown - timer));
        }
      } else if (player.getItem().getType() == Item.Type.Weapon) {
        if (timer >= cooldown) {
          CloseRangeWeapon weapon = (CloseRangeWeapon)player.getItem();
          if (current_room.tryHitMob(player.getPos(), player.getDirection(), 
            weapon.getDamage() * player.getStrength(), weapon.getDistance(), weapon.getAngle())) {
            player.startAttackAnim();
            debug_info[DEBUG_LINE_INFO].setText("Hitted");
            cooldown = weapon.getCooldown() / player.getAttackSpeed();
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
      current_room.addMob(Mob.getStaticMob("mobs/skeleton", new Vector2d(player.getPos())));
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
      is_pause = true;

      pause_info_label.setText(die_message);
      pause_tip_label.setText(die_tip);

      pause_info_label.setVisible(true);
      pause_tip_label.setVisible(true);
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

      int type_room = rand.nextInt(10); // 0-1 - empty room, 2-5 - room with enemies, 6-9 - room with items

      if (type_room > 1 && type_room < 6) {
        int count_new_mobs = rand.nextInt(10 + (int)Math.floor(player.useLuck(5, -5)));

        for (int i = 0; i < count_new_mobs; ++i) {
          Mob mob = Mob.getStaticMob("mobs/skeleton");
          if (rand.nextInt(10000) < 800 + (int)Math.floor(player.useLuck(100, -100))) {
            mob.pickUpItem("weapons/knife");
          }
          new_room.addMob(mob);
        }
      } else if (type_room > 6) {
        if (rand.nextInt(10000) < 7500 + (4 - new_room.getDoorsCount()) * 250) { // have items
          int count_new_items = rand.nextInt(3 + (int)Math.ceil(player.useLuck(1, -1)));
          for (int i = 0; i < count_new_items; ++i) {
            switch(rand.nextInt(3)) {
            case 0: new_room.addItem("forcraft/leater");
            case 1: new_room.addItem("forcraft/rope");
            default: new_room.addItem("forcraft/bone");
            }
          }
        }
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