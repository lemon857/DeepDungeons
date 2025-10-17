package com.deepdungeons.game;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.deepdungeons.game.effects.CycleEffect;
import com.deepdungeons.game.effects.Effect;
import com.deepdungeons.game.effects.EffectsPanel;
import com.deepdungeons.game.effects.SimpleEffect;
import com.deepdungeons.game.items.Coin;
import com.deepdungeons.game.items.Edible;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.items.ItemForCraft;
import com.deepdungeons.game.items.Key;
import com.deepdungeons.game.mobs.DefaultEnemy;
import com.deepdungeons.game.mobs.Mob;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.DebugRenderer;
import com.deepdungeons.game.utils.Generator;
import com.deepdungeons.game.utils.LootTable;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Vector2d;
import com.deepdungeons.game.weapons.CloseRangeWeapon;
import com.deepdungeons.game.weapons.Hand;

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
  private BaseRenderer renderer;

  private Stage stage;

  private HashMap<Point, Room> rooms;

  private Point current_room_pos;
  private Room current_room;

  public static Player player;
  private boolean is_pause;
  private double timer;
  private double cooldown;

  private int req_door_id;

  private Label pause_info_label;
  private Label pause_tip_label;

  private Label coins_label;

  private static final String DIE_MESSAGE = "YOU DIED";
  private static final String DIE_TIP = "Press G to restart";

  private static final String PAUSE_MESSAGE = "PAUSE";
  private static final String PAUSE_TIP = "Press ESC to resume\nPress G to restart";

  private static final Point START_ROOM = new Point(0, 0);
  private Generator generator;

  private LootTable items_room_lt;

  private Random rand;

  public static World world;

  private boolean show_map;
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

  private RoomWall wall;

  private PhysicsContactListener contactListener;

  @Override
  public void create() {   
    generator = new Generator(START_ROOM);
    rand = new Random();

    world = new World(new Vector2(0f, 0f), true);
    world.setContactListener(contactListener);

    renderer = new DebugRenderer(new SpriteBatch(), Room.SCREEN_WIDTH, Room.SCREEN_HEIGHT, world);
    
    wall = new RoomWall("textures/wall.png");

    renderer.addDrawable(wall);

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

    initStaticItems();

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

    coins_label = new Label("0", skin);
    coins_label.setPosition(1460, 970);
    coins_label.setColor(1, 1, 0.2f, 1);
    coins_label.setFontScale(2);
    stage.addActor(coins_label);

    debug_info[DEBUG_LINE_ITEM_NAME].setPosition(DEBUG_START.x, 100);
    
    debug_info[DEBUG_LINE_ITEM_NAME].setColor(1, 0, 1, 1);
    debug_info[DEBUG_LINE_ITEM_NAME].setFontScale(3);

    start();
  }

  private void start() {
    generator.generateSkeleton();

    pause_info_label.setVisible(false);
    pause_tip_label.setVisible(false);

    player = new Player();
    renderer.addDrawable(player);

    rooms = new HashMap<>();

    Generator.RoomMark mark = generator.getRoomMark(START_ROOM);

    Room new_room = new Room(new Point(0, 0), mark.doors, mark.lock_doors, mark.lock_doors_color);
    renderer.addDrawable(new_room);

    if (!mark.require_keys.isEmpty()) {
      for (int i = 0; i < mark.require_keys.size(); ++i) {
        Key key = (Key)Item.getStaticItem("special/key");
        key.setParams(mark.require_keys.get(i), mark.require_keys_color.get(i));
        new_room.addItem(key);
      }
    }

    new_room.addItem("weapons/bone_baton");
    new_room.addItem("weapons/knife");

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
    show_map = false;
  }

  @Override
  public void render() {
    input();
    logic();
    draw();
  }

  private void input() {
    if (is_pause) {
      if (Gdx.input.isKeyJustPressed(Input.Keys.G)) {
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
      pause_info_label.setText(PAUSE_MESSAGE);
      pause_tip_label.setText(PAUSE_TIP);

      pause_info_label.setVisible(true);
      pause_tip_label.setVisible(true);
      is_pause = true;
    }
    float speed = (float)player.getMoveSpeed();
    double delta = Gdx.graphics.getDeltaTime();

    Point pos = new Point(player.getPos());
    Vector2 vector = new Vector2();

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      pos.y += 1;
      vector.y += speed;
      // Top door
      // if (pos.x >= Room.DOOR_OFFSET.x - 3 && pos.x <= Room.DOOR_OFFSET.x + 4 && pos.y > Room.END_BORDER.y - Player.HEIGHT - Room.DOOR_HEIGHT) {
      //   if (tryGoToNextRoom(0)) player.setY(Room.START_BORDER.y + Room.DOOR_HEIGHT);
      // }

    } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      pos.y -= 1;
      vector.y -= speed;
      // Bottom door
      // if (pos.x >= Room.DOOR_OFFSET.x - 3 && pos.x <= Room.DOOR_OFFSET.x + 4 && pos.y < Room.START_BORDER.y + Room.DOOR_HEIGHT + 1) {
      //   if (tryGoToNextRoom(2)) player.setY(Room.END_BORDER.y - Player.HEIGHT - Room.DOOR_HEIGHT - 1);
      // }
    }
    
    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      pos.x += 1;
      vector.x += speed;
      // Right door
      // if (pos.y >= Room.DOOR_OFFSET.y - 3 && pos.y <= Room.DOOR_OFFSET.y + 4 && pos.x > Room.END_BORDER.x - Player.WIDTH - Room.DOOR_HEIGHT - 1) {
      //   if (tryGoToNextRoom(1)) player.setX(Room.START_BORDER.x + Room.DOOR_HEIGHT);
      // }
      
    } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      pos.x -= 1;
      vector.x -= speed;
      // Left door
      // if (pos.y >= Room.DOOR_OFFSET.y - 3 && pos.y <= Room.DOOR_OFFSET.y + 4 && pos.x < Room.START_BORDER.x + Room.DOOR_HEIGHT + 1) {
      //   if (tryGoToNextRoom(3)) player.setX(Room.END_BORDER.x - Player.WIDTH - Room.DOOR_HEIGHT - 1);
      // }
    }

    player.setVelocity(vector);
    // player.translate(vector);
    
    // Use (pick up, put down, open door)
    if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
      if (req_door_id != -1 && player.getItem() != null) {
        if (Point.distance(player.getCenterPos(), Room.getDoorPosition(req_door_id)) < 7 && player.getItem().getType() == Item.Type.Key) {
          if (current_room.tryUnlockDoor(req_door_id, ((Key)player.getItem()).getKey())) {
            current_room.generateBackground();
            req_door_id = -1;
            Item item = player.dropItem(); // Remove key
            item.playSound();
          }
        } else {
          req_door_id = -1;
        }
      } else {
        if (current_room.canGrabItem(player.getCenterPos())) {
          Item new_item = current_room.grabItem();

          if (player.isDropAvailable() && new_item.getType() != Item.Type.Coin) {
            Item item = player.dropItem();

            item.setCenterPos(player.getCenterPos());
            current_room.addItem(item);
          }
          player.pickupItem(new_item);
          coins_label.setText(player.getCoinsCount());
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
          if (current_room.tryHitMob(player.getCenterPos(), player.getDirection(), 
            Hand.getDamage(rand) * player.getStrength(), Hand.DISTANCE, Hand.ANGLE, player.getSizeOffset(), false)) {
            player.startAttackAnim();
            debug_info[DEBUG_LINE_INFO].setText("Hand Hitted");
            cooldown = Hand.getCooldown(rand) / player.getAttackSpeed();
            Hand.playSound();
            timer = 0;
          } else {
            debug_info[DEBUG_LINE_INFO].setText("No hand hit");
          }
        } else {
          debug_info[DEBUG_LINE_INFO].setText("Time left: " + (cooldown - timer));
        }
      } else if (player.getItem().getType() == Item.Type.Weapon) {
        if (timer >= cooldown) {
          CloseRangeWeapon weapon = (CloseRangeWeapon)player.getItem();
          if (current_room.tryHitMob(player.getCenterPos(), player.getDirection(), 
            weapon.getDamage() * player.getStrength(), weapon.getDistance(), weapon.getAngle(), player.getSizeOffset(), weapon.isAllowSplash())) {
            player.startAttackAnim();
            debug_info[DEBUG_LINE_INFO].setText("Weapon Hitted");
            cooldown = weapon.getCooldown() / player.getAttackSpeed();
            timer = 0;
          } else {
            debug_info[DEBUG_LINE_INFO].setText("No weapon hit");
          }
        } else {
          debug_info[DEBUG_LINE_INFO].setText("Time left: " + (cooldown - timer));
        }
      }
    }

    // [DEBUG] Show map
    if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
      show_map = !show_map;
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
      player.heal(1);
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

    // [DEBUG] Add positive effect
    if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
      player.addCycleEffect("effects/heal", 4, 100, 1, 1);
      // player.addCycleEffect("effects/hunger", 4, 10, 1, 1);
    }

    // [DEBUG] Add negative effect
    if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
      player.addEffect("effects/luck", -4, -1);
    }
  }

  private void logic() {
    if (is_pause) return;

		world.step(Gdx.graphics.getDeltaTime(), 1, 1);
    
    double delta = Gdx.graphics.getDeltaTime();
    player.update(delta);
    current_room.update(delta);
    //debug_info[DEBUG_LINE_DISTANCE].setText("Dis to door: " + Point.distance(player.getCenterPos(), Room.GetDoorPosition(req_door_id)));
    //debug_info[DEBUG_LINE_DISTANCE].setText("Dis to item: " + current_room.distanceToNearestItem(player.getCenterPos()));
    debug_info[DEBUG_LINE_DISTANCE].setText("Dis to mob: " + (current_room.distanceToNearestMob(player.getCenterPos(), player.getSizeOffset())));
    debug_info[DEBUG_LINE_ANGLE].setText("Angle to mob: " + current_room.angleToNearestMob(player.getCenterPos(), player.getDirection(), player.getSizeOffset()));
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

      pause_info_label.setText(DIE_MESSAGE);
      pause_tip_label.setText(DIE_TIP);

      pause_info_label.setVisible(true);
      pause_tip_label.setVisible(true);
    }
  }

  private void draw() {
    renderer.render();
    
    // if (show_map) {
    //   batch.draw(generator.getTexture(), (float)Room.SHIFT_X, 100, 100, -100);
    // }

    stage.act(Gdx.graphics.getDeltaTime());
	  stage.draw();
  }

  @Override
  public void dispose() {
    renderer.dispose();
    world.dispose();
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  private boolean tryGoToNextRoom(int door_id) {
    if (!rooms.get(current_room_pos).canGoNextRoom(door_id)) {
      int req = rooms.get(current_room_pos).getLockedDoorKey(door_id);
      if (req != 0) {
        debug_info[DEBUG_LINE_INFO].setText("Require key: " + req);
        req_door_id = door_id;
      }
      return false;
    }

    Point new_pos = Point.sum(current_room_pos, Generator.getRoomDeltaFromDoor(door_id));

    if (!rooms.containsKey(new_pos)) {

      current_room_pos = Point.sub(current_room_pos, Generator.getRoomDeltaFromDoor((door_id + 2) % 4));

      Generator.RoomMark mark = generator.getRoomMark(current_room_pos);
      Room new_room = new Room(current_room_pos, mark.doors, mark.lock_doors, mark.lock_doors_color);
      renderer.addDrawable(new_room);

      if (!mark.require_keys.isEmpty()) {
        for (int i = 0; i < mark.require_keys.size(); ++i) {
          Key key = (Key)Item.getStaticItem("special/key");
          key.setParams(mark.require_keys.get(i), mark.require_keys_color.get(i));
          new_room.addItem(key);
        }
      }

      if (mark.type == Generator.RoomType.Monsters) {
        int count_new_mobs = rand.nextInt(10 + (int)Math.floor(player.useLuck(5, -5)));

        for (int i = 0; i < count_new_mobs; ++i) {
          Mob mob;
          if (rand.nextInt(10000) < 5000) {   
            mob = Mob.getStaticMob("mobs/skeleton");
          } else {
            mob = Mob.getStaticMob("mobs/zombie");
          }
          if (rand.nextInt(10000) < 800 + player.useLuck(100, -100)) {
            mob.pickUpItem("textures/weapons/knife");
          }
          new_room.addMob(mob);
        }
      } else if (mark.type == Generator.RoomType.Items) {
        int count_new_items = rand.nextInt(2, 6 + (int)Math.ceil(player.useLuck(1, -1)));
        for (int i = 0; i < count_new_items; ++i) {
          new_room.addItem(items_room_lt.nextItem());
        }
      }

      rooms.put(current_room_pos, new_room);

    } else {
      current_room_pos = new_pos;
    }

    current_room.setActive(false);
    current_room = rooms.get(current_room_pos);
    current_room.setActive(true);

    debug_info[DEBUG_LINE_ROOM_POS].setText("Room X: " + current_room_pos.x + " Y: " + current_room_pos.y);
    return true;
  }

  private void initStaticItems() {
    Hand.initSound("sounds/hand.mp3");
    Edible.setSounds("sounds/eating.mp3", "sounds/drinking.mp3");

    Item.initStaticItems();
    Item.addStaticItem("special/key", new Key("textures/items/key.png", "sounds/use_key.mp3"));
    Item.addStaticItem("special/coin", new Coin("textures/items/coin.png", "sounds/coin.mp3"));

    Item.addStaticItem("forcraft/bone", new ItemForCraft("textures/items/bone.png", Item.Tier.Common, "bone"));
    Item.addStaticItem("forcraft/rope", new ItemForCraft("textures/items/rope.png", Item.Tier.Common, "rope"));
    Item.addStaticItem("forcraft/leather", new ItemForCraft("textures/items/leather.png", Item.Tier.Common, "leather"));
    Item.addStaticItem("forcraft/stone", new ItemForCraft("textures/items/stone.png", Item.Tier.Common, "stone"));
    Item.addStaticItem("forcraft/stick", new ItemForCraft("textures/items/stick.png", Item.Tier.Common, "stick"));
    Item.addStaticItem("forcraft/feather", new ItemForCraft("textures/items/feather.png", Item.Tier.Common, "feather"));

    Item.addStaticItem("foods/candy", new Edible("textures/items/candy.png", Item.Tier.Common, "candy", false, 2));
    Item.addStaticItem("foods/meat", new Edible("textures/items/meat.png", Item.Tier.Uncommon, "meat", false, 5));

    Item.addStaticItem("drinks/bottle_of_water", new Edible("textures/items/bottle_of_water.png", Item.Tier.Common, "bottle of water", true, 4));
    Item.addStaticItem("drinks/cup_of_tea", new Edible("textures/items/cup_of_tea.png", Item.Tier.Uncommon, "cup of tea", true, 7));

    Item.addStaticItem("weapons/knife", new CloseRangeWeapon("textures/weapons/knife.png", "sounds/knife.mp3", "knife",
    0.8, 0.4, Math.PI / 2, 2, 0.5, false));

    Item.addStaticItem("weapons/bone_baton", new CloseRangeWeapon("textures/weapons/bone_baton.png", "sounds/baton.mp3", "bone baton",
     1.2, 2, Math.PI / 3, 1.5, 1, true));

    Mob.initStaticMobs();
    Mob.addStaticMob("mobs/skeleton", new DefaultEnemy("textures/mobs/skeleton.png", 
    25, 1.1, 0.8, 1, 3,
     new LootTable(new String[][]{{}, {"forcraft/bone"}, {"forcraft/rope"}}, new double[]{0.8, 0.15, 0.05})));

     Mob.addStaticMob("mobs/zombie", new DefaultEnemy("textures/mobs/zombie.png", 
     20, 0.9, 1.2, 2, 3,
      new LootTable(new String[][]{{}, {"forcraft/leather"}, {"forcraft/rope"}}, new double[]{0.8, 0.15, 0.05})));

    Effect.initStaticEffects();
    Effect.addStaticEffect("effects/speed", 
      new SimpleEffect("textures/effects/speed.png", "textures/effects/slowness.png", "speed"))
      .setCharacterFunc(Character::setMoveSpeedModifier);

    Effect.addStaticEffect("effects/haste", 
      new SimpleEffect("textures/effects/attack_speed.png", "textures/effects/attack_slowness.png", "haste"))
      .setCharacterFunc(Character::setAttackSpeedModifier);

    Effect.addStaticEffect("effects/strength", 
      new SimpleEffect("textures/effects/strength.png", "textures/effects/weakness.png", "strength"))
      .setCharacterFunc(Character::setStrengthModifier);

    Effect.addStaticEffect("effects/damage", 
      new CycleEffect("textures/effects/damage.png", "damage", -1))
      .setCharacterFunc(Character::damage);

    Effect.addStaticEffect("effects/heal", 
      new CycleEffect("textures/effects/heal.png", "heal", 1))
      .setCharacterFunc(Character::heal);

    Effect.addStaticEffect("effects/hunger", 
      new CycleEffect("textures/effects/heal.png", "hunger", -1))
      .setPlayerFunc(Player::hunger);

    Effect.addStaticEffect("effects/saturation", 
      new CycleEffect("textures/effects/heal.png", "saturation", -1))
      .setPlayerFunc(Player::saturation);

    Effect.addStaticEffect("effects/luck", 
      new SimpleEffect("textures/effects/luck.png",  "textures/effects/unluck.png", "luck"))
      .setPlayerFunc(Player::setLuck);

    EffectsPanel.initLevelImages();
    for (int i = -4; i < 5; ++i) {
      if (i == 0) {
        EffectsPanel.addLevelTexture(i, "textures/ui/neutral.png");
      } else if (i > 0) {
        EffectsPanel.addLevelTexture(i, "textures/ui/green" + i + ".png");
      } else {
        EffectsPanel.addLevelTexture(i, "textures/ui/red" + (-i) + ".png");
      }
    }
    
    // for_craft eat drink special
    items_room_lt = new LootTable(new String[][]{
      {"forcraft/stone", "forcraft/stick", "forcraft/feather"},
      {"foods/candy", "foods/meat"},
      {"drinks/bottle_of_water", "drinks/cup_of_tea"},
      {"special/coin"}
    }, new double[]{
      0.3,
      0.25,
      0.25,
      0.2
    });
  }
}