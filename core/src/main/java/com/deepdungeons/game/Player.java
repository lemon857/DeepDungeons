package com.deepdungeons.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.deepdungeons.game.effects.EffectsPanel;
import com.deepdungeons.game.items.Edible;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public final class Player extends Character {
  public static final int WIDTH = 7;
  public static final int HEIGHT = 7;

  public static final double MAX_HP = 30;
  public static final int MAX_FP = 25;
  public static final int MAX_TP = 25;
  // private static final int MIN_TP = 0;

  private static final int MOVE_SPEED = 40;
  private static final int ATTCAK_SPEED = 1;
  private static final int STRENGTH = 1;

  public static final int EYE_UP = (HEIGHT / 2) - 1;
  public static final int EYE_DOWN = HEIGHT - EYE_UP - 1;
  public static final int EYE_MIDDLE = EYE_DOWN + (EYE_UP - EYE_DOWN) / 2;

  public static final int EYE_LEFT = (WIDTH / 2) - 1;
  public static final int EYE_RIGHT = WIDTH - EYE_LEFT - 1;

  private static final Point START_BORDER = Point.sum(Room.START_BORDER, new Point(1, 1));
  private static final Point END_BORDER = Point.sub(Room.END_BORDER, new Point(WIDTH, HEIGHT));

  private static final Color DAMAGE_COLOR = new Color(1f, 0.7f, 0.7f, 0.7f);

  private static final Color COLOR = new Color(0.8f, 0.7f, 0.9f, 0.7f);
  private static final Color HEALTH_COLOR = new Color(1f, 0f, 0.2f, 1f);
  private static final Color DEAD_HEALTH_COLOR = new Color(0.8f, 0.5f, 0.65f, 1f);
  private static final Color EYE_COLOR = new Color(0, 0, 0, 1);

  private static final Vector2d EFFECT_SIZE = new Vector2d(5, 5);
  private static final Vector2d EFFECT_PANEL_POS = new Vector2d(Room.SCREEN_WIDTH - EFFECT_SIZE.x - 1, Room.SCREEN_HEIGHT - EFFECT_SIZE.y);
  private static final double EFFECT_Y_SHIFT = 2;

  private static final int WHITE_IN_HEART_MASK = -1;

  private static final double MAX_THIRSTY_DISTANCE = Room.WIDTH * 10;
  private static final double MIN_THIRSTY_DISTANCED = Room.WIDTH * 6;

  private double next_thirsty_distance;
  private double current_walked_distance;

  private final Random rand;

  private double attack_anim_timer;
  private boolean attack_anim_play;
  private static final double ATTACK_ANIM_TIME = 0.2;

  protected double damage_anim_timer;
  protected boolean damage_anim_play;
  protected static final double DAMAGE_ANIM_TIME = 0.25;

  private Sprite sprite;

  private Texture inventory_texture;

  private Pixmap health_map;

  private final Pixmap hearts[];
  private Pixmap heart_mask;

  private final EffectsPanel effects_panel;

  private final Pixmap full_food_point;
  private final Pixmap empty_food_point;

  private final Pixmap full_thirsty_point;
  private final Pixmap empty_thirsty_point;

  private Texture health_texture;

  private Direction dir;

  private boolean non_actual;

  private Item inventory;

  private int money;

  private int food_points;
  private int thirsty_points;

  private double luck;

  public Player() {
    super(MAX_HP, MOVE_SPEED, ATTCAK_SPEED, STRENGTH);

    CircleShape shape = new CircleShape();
    shape.setRadius(WIDTH);
    setShape(shape, 4f, 0.5f);

    this.effects_panel = new EffectsPanel(effects, EFFECT_PANEL_POS, EFFECT_SIZE, EFFECT_Y_SHIFT);

    this.rand = new Random();
    this.damage_anim_timer = DAMAGE_ANIM_TIME + 1;
    this.damage_anim_play = false;
    this.attack_anim_timer = ATTACK_ANIM_TIME + 1;
    this.attack_anim_play = false;
    this.dir = Direction.Up;
    this.non_actual = false;
    this.food_points = MAX_FP;
    this.thirsty_points = MAX_TP;

    this.money = 0;

    this.luck = 0;

    this.next_thirsty_distance = rand.nextDouble(MIN_THIRSTY_DISTANCED, MAX_THIRSTY_DISTANCE) + useLuck(-2.0 * Room.WIDTH, 2.0 * Room.WIDTH);
    this.current_walked_distance = 0;

    Pixmap map = new Pixmap(18, 6, Pixmap.Format.RGBA8888);
    inventory_texture = new Texture(map);

    this.full_food_point = generateFullFoodPoint();
    this.empty_food_point = generateEmptyFoodPoint();

    this.full_thirsty_point = generateFullThirstyPoint();
    this.empty_thirsty_point = generateEmptyThirstyPoint();

    generateHeartMask();

    this.hearts = new Pixmap[]{ generateHeart(), generateHeart(), generateHeart() };

    generatePlayerImage();
    generateInventoryTexture();
    generateHealthTexture();

    generateRandomPos();
    updateSpritePos();
    this.sprite.setSize(WIDTH, HEIGHT);
  }
  public final void generateRandomPos() {
    Vector2 pos = new Vector2();
    pos.x = rand.nextFloat(WIDTH + Room.START_BORDER.x + 1, Room.END_BORDER.x - WIDTH - 1);
    pos.y = rand.nextFloat(Room.START_BORDER.y + HEIGHT + 1, Room.END_BORDER.y - HEIGHT - 1);
    body.setTransform(pos, 0);
    updateSpritePos();
  }

  private void updateSpritePos() {
    sprite.setPosition(body.getPosition().x, body.getPosition().y);
  }

  protected void updateSpriteSize() {
    sprite.setSize((float)size.x, (float)size.y);
  }

  private void generatePlayerImage() {
    Pixmap map = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    map.setColor(COLOR);
    map.fillCircle(WIDTH / 2, HEIGHT / 2, (WIDTH / 2));

    map.setColor(EYE_COLOR);
    switch (dir) {
    case Up:
    default:
      map.drawPixel(EYE_LEFT, EYE_UP);
      map.drawPixel(EYE_RIGHT, EYE_UP);   
      break;
    case Down:
        map.drawPixel(EYE_LEFT, EYE_DOWN);
        map.drawPixel(EYE_RIGHT, EYE_DOWN);
        break;
    case Right:
      map.drawPixel(EYE_LEFT + 1, EYE_MIDDLE);
      map.drawPixel(EYE_RIGHT + 1, EYE_MIDDLE);   
      break;
    case Left:
      map.drawPixel(EYE_LEFT - 1, EYE_MIDDLE);
      map.drawPixel(EYE_RIGHT - 1, EYE_MIDDLE); 
      break;
    case UpRight:
      map.drawPixel(EYE_LEFT + 1, EYE_UP);
      map.drawPixel(EYE_RIGHT + 1, EYE_UP);   
      break;
    case UpLeft:
      map.drawPixel(EYE_LEFT - 1, EYE_UP);
      map.drawPixel(EYE_RIGHT - 1, EYE_UP);   
      break;
    case DownRight:
        map.drawPixel(EYE_LEFT + 1, EYE_DOWN);
        map.drawPixel(EYE_RIGHT + 1, EYE_DOWN);   
        break;
    case DownLeft:
        map.drawPixel(EYE_LEFT - 1, EYE_DOWN);
        map.drawPixel(EYE_RIGHT - 1, EYE_DOWN);   
        break;
    }

    if (sprite == null) sprite = new Sprite(new Texture(map));
    sprite.getTexture().draw(map, 0, 0);

    if (damage_anim_play) sprite.setColor(DAMAGE_COLOR);
  }

  private void generateInventoryTexture() {
    if (inventory != null) {
      inventory_texture = new Texture(inventory.getImage());
    } else {
      Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      inventory_texture = new Texture(map);
    }
  }

  private void generateHeartMask() {
    int width = 7;
    int height = 7;
    heart_mask = new Pixmap(width, height, Pixmap.Format.RGBA4444);

    heart_mask.setColor(1, 1, 1, 1);
    heart_mask.fillCircle(width / 2, height / 2, width / 2);

    heart_mask.setColor(0, 0, 0, 1);
    heart_mask.drawLine(width / 2, height / 2 + 1, width / 2, height - 1);
    heart_mask.drawLine(width / 2 - 2, height - 1, width / 2 + 2, height - 1);
    heart_mask.drawLine(width / 2 - 1, height - 2, width / 2 + 1, height - 2);

    // for (int i = 0; i < heart_mask.getWidth(); ++i) {
    //   for (int j = 0; j < heart_mask.getHeight(); ++j) {
    //     // if (heart_mask.getPixel(i, j) != BLACK_IN_HEART_MASK) {
    //     //   ++count_heart_pixels;
    //     // }
    //     System.out.print(heart_mask.getPixel(i, j) + " ");
    //   }
    //   System.out.print("\n");
    // }

    //System.out.println("0: " + heart_mask.getPixel(0, 0) + " 1: " + heart_mask.getPixel(WIDTH / 2, HEIGHT / 2));
  }

  private Pixmap generateHeart() {
    Pixmap map = new Pixmap(heart_mask.getWidth(), heart_mask.getHeight(), Pixmap.Format.RGB888);

    for (int i = 0; i < heart_mask.getWidth(); ++i) {
      for (int j = 0; j < heart_mask.getHeight(); ++j) {
        if (heart_mask.getPixel(i, j) == WHITE_IN_HEART_MASK) {
          map.setColor(HEALTH_COLOR);
          map.drawPixel(i, j);
        }
      }
    }

    return map;
  }

  private Pixmap generateFullFoodPoint() {
    Pixmap map = new Pixmap(3, 3, Pixmap.Format.RGB888);

    map.setColor(1f, 0.3f, 0.5f, 1f);
    map.drawRectangle(1, 1, 2, 2);
    map.setColor(1f, 0.6f, 0.4f, 1f);
    map.drawPixel(1, 1);
    map.setColor(1f, 1f, 1f, 1f);
    map.drawPixel(0, 0);

    return map;
  }

  private Pixmap generateEmptyFoodPoint() {
    Pixmap map = new Pixmap(3, 3, Pixmap.Format.RGB888);

    map.setColor(0.7f, 0.4f, 0.35f, 1f);
    map.drawRectangle(1, 1, 2, 2);
    map.setColor(0.6f, 0.3f, 0.4f, 1f);
    map.drawPixel(1, 1);
    map.setColor(0.4f, 0.4f, 0.4f, 1f);
    map.drawPixel(0, 0);

    return map;
  }

  private Pixmap generateFullThirstyPoint() {
    Pixmap map = new Pixmap(3, 5, Pixmap.Format.RGB888);

    map.setColor(0.2f, 0.3f, 1f, 1f);
    map.fillRectangle(0, 0, 3, 3);
    map.setColor(1f, 0.6f, 0.4f, 1f);
    map.drawPixel(1, 3);
    map.setColor(1f, 1f, 1f, 1f);
    map.drawLine(0, 4, 2, 4);

    return map;
  }

  private Pixmap generateEmptyThirstyPoint() {
    Pixmap map = new Pixmap(3, 5, Pixmap.Format.RGB888);

    map.setColor(0.2f, 0.2f, 0.2f, 1f);
    map.fillRectangle(0, 0, 3, 3);
    map.setColor(1f, 0.6f, 0.4f, 1f);
    map.drawPixel(1, 3);
    map.setColor(1f, 1f, 1f, 1f);
    map.drawLine(0, 4, 2, 4);

    return map;
  }

  private void generateHealthTexture() {
    health_map = new Pixmap(WIDTH * 4, HEIGHT * 3, Pixmap.Format.RGBA8888);

    for (int i = 0; i < 3; ++i) {
      health_map.drawPixmap(hearts[i], i * (WIDTH + 1), HEIGHT * 2);
    }

    for (int i = 0; i < 5; ++i) {
      health_map.drawPixmap(full_food_point, 2 + i * 4, HEIGHT + 2);
    }

    for (int i = 0; i < 5; ++i) {
      health_map.drawPixmap(full_thirsty_point, 2 + i * 4, 2);
    }

    health_texture = new Texture(health_map);
  }
 
  public Item getItem() {
    return inventory; 
  }

  public void pickupItem(Item item) {
    if (item.getType() == Item.Type.Coin) {
      ++money;
      item.playSound();
      return;
    }
    inventory = item;
    generateInventoryTexture();
  }

  public boolean isDropAvailable() {
    return inventory != null;
  }

  public boolean tryUseEdibleItem() {
    if (inventory == null) return false;
    if (inventory.getType() == Item.Type.Food) {
      saturation(((Edible)inventory).getPoints());
      inventory.playSound();
      inventory = null;
      return true;

    } else if (inventory.getType() == Item.Type.Drink) {
      drink(((Edible)inventory).getPoints());
      inventory.playSound();
      inventory = null;
      return true;
    }
    return false;
  }

  public void startAttackAnim() {
    attack_anim_timer = 0;
    attack_anim_play = true;
    size.y *= 0.9;
    updateSpriteSize();
    if (inventory != null) inventory.playSound();
  }

  public Item dropItem() {
    Item res = inventory;
    inventory = null;
    generateInventoryTexture();
    return res;
  }
  // public void translate(Vector2d vector) {
  //   translate(vector.x, vector.y);
  // }
  // public void translate(double x, double y) {
  //   Vector2d old_pos = new Vector2d(pos);

  //   pos.x += x;
  //   pos.y += y;

  //   if (pos.x < START_BORDER.x) pos.x = START_BORDER.x;
  //   else if (pos.x > END_BORDER.x) pos.x = END_BORDER.x;

  //   if (pos.y < START_BORDER.y) pos.y = START_BORDER.y;
  //   else if (pos.y > END_BORDER.y) pos.y = END_BORDER.y;

  //   current_walked_distance += Vector2d.distance(old_pos, pos);
  //   if (current_walked_distance >= next_thirsty_distance) {
  //     thirst(rand.nextInt(2, 5));
  //     next_thirsty_distance = rand.nextDouble(MIN_THIRSTY_DISTANCED, MAX_THIRSTY_DISTANCE) + useLuck(-2.0 * Room.WIDTH, 2.0 * Room.WIDTH);
  //     current_walked_distance = 0;
  //   }

  //   if (Utility.getTranslateDirection(x, y) != Direction.Undefined) {
  //     dir = Utility.getTranslateDirection(x, y);
  //     updateSpritePos();
  //   }
    
  //   non_actual = true;
  // }

  public void setX(int x) {
    Vector2 pos = body.getPosition();
    pos.x = x;
    if (pos.x < START_BORDER.x) pos.x = START_BORDER.x;
    else if (pos.x > END_BORDER.x) pos.x = END_BORDER.x;

    body.setTransform(pos, 0);
    updateSpritePos();
  }
  public void setY(int y) {
    Vector2 pos = body.getPosition();
    pos.y = y;
    if (pos.y < START_BORDER.y) pos.y = START_BORDER.y;
    else if (pos.y > END_BORDER.y) pos.y = END_BORDER.y;

    body.setTransform(pos, 0);
    updateSpritePos();
  }

  // true if success
  public boolean trySpendMoney(int count) {
    if (count > money) return false;

    money -= count;
    return true;
  }

  public int getCoinsCount() {
    return money;
  }

  public void update(double delta) {
    updateEffects(delta);

    if (non_actual) {
      generatePlayerImage();
      non_actual = false;
    }

    if (attack_anim_timer >= ATTACK_ANIM_TIME && attack_anim_play) {
      size.y = HEIGHT;
      updateSpriteSize();
      attack_anim_play = false;
    } else if (attack_anim_play) {
      attack_anim_timer += delta;
    }

    if (damage_anim_play) {
      damage_anim_timer += delta;
      if (damage_anim_timer >= DAMAGE_ANIM_TIME) {
        // change color of mob to default
        sprite.setColor(1, 1, 1, 1);
        damage_anim_play = false;
      }
    }
  }

  public void draw(SpriteBatch batch) {
    Vector2 pos = body.getPosition();
    // correct coords for Pixmap
    updateSpritePos();
    sprite.draw(batch);
    effects_panel.draw(batch);
    //batch.draw(image, (float)pos.x, (float)pos.y + (float)size.y,
    //(float)size.x, -(float)size.y);
    if (inventory != null) {
      if (inventory.isTextureFromFile()) {
        batch.draw(inventory_texture, (float)pos.x + WIDTH * 0.7f, (float)pos.y + HEIGHT * 0.65f, 
        (float)inventory.getSize().x * 0.8f, (float)inventory.getSize().y * 0.8f);
      } else {
        batch.draw(inventory_texture, (float)pos.x + WIDTH * 0.7f, (float)pos.y + HEIGHT * 0.65f + (float)inventory.getSize().y * 0.8f, 
        (float)inventory.getSize().x * 0.8f, (float)inventory.getSize().y * -0.8f);
      }
    }
    batch.draw(health_texture, 1, Room.SCREEN_HEIGHT - 1, WIDTH * 4, -HEIGHT * 3);
  }

  public void setLuck(double luck) {
    this.luck = luck;
  }

  public double useLuck(double negativeMax, double positiveMax) {
    if (luck == 0) return 0;
    else if (luck > 0) return rand.nextDouble(luck * Math.abs(positiveMax)) * Math.signum(positiveMax);

    return rand.nextDouble(luck * Math.abs(negativeMax)) * Math.signum(negativeMax);
  }

  public int useLuck(int negativeMax, int positiveMax) {
    if (luck == 0) return 0;
    else if (luck > 0) return rand.nextInt((int)Math.floor(luck * Math.abs(positiveMax))) * (int)Math.signum(positiveMax);

    return rand.nextInt((int)Math.floor(luck * Math.abs(negativeMax))) * (int)Math.signum(negativeMax);
  }

  public Vector2d getPos() {
    Vector2 pos = body.getPosition();
    return new Vector2d(pos.x, pos.y);
  }

  public Vector2d getCenterPos() {
    return Vector2d.sum(getPos(), new Vector2d(WIDTH / 2, HEIGHT / 2));
  }

  public Vector2d getDirection() {
    return Utility.getDirectionVector(dir);
  }

  public double getSizeOffset() {
    return 0;
  }

  public boolean isDie() {
    return health_points <= 0;
  }

  @Override
  public boolean damage(double dmg) {
    if (health_points < 0) return true;

    sprite.setColor(DAMAGE_COLOR);
    damage_anim_play = true;
    damage_anim_timer = 0;

    int i;
    for (i = 0; i < Math.floor(dmg); ++i) {
      --health_points;
      simple_damage(1);
    }
    int before = (int)Math.ceil(health_points);
    health_points -= (dmg - i);
    int after = (int)Math.ceil(health_points);
    // System.out.println("[damage] before: " + before + " after: " + after);
    if (before - after != 0) {
      simple_damage(dmg - i);
    }

    System.out.printf("[damage] HP: %f, dmg: %f\n", health_points, dmg);

    if (rand.nextInt(10000) < 1000 + useLuck(200, -200)) {
      hunger(rand.nextInt(2, 5));
    }

    return health_points <= 0;
  }
  private void simple_damage(double delta) {
    if (health_points <= 0) {
      hearts[0] = Utility.replacePixelsColor(hearts[0], HEALTH_COLOR, DEAD_HEALTH_COLOR);
      health_map.drawPixmap(hearts[0], 0, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points == 10 || (health_points + delta > 10 && health_points < 10)) {
      hearts[1] = Utility.replacePixelsColor(hearts[1], HEALTH_COLOR, DEAD_HEALTH_COLOR);
      health_map.drawPixmap(hearts[1], WIDTH + 1, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 0 && health_points < 10) {
      damage_heart(0);

    } else if (health_points == 20 || (health_points + delta > 20 && health_points < 20)) {
      hearts[2] = Utility.replacePixelsColor(hearts[2], HEALTH_COLOR, DEAD_HEALTH_COLOR);
      health_map.drawPixmap(hearts[2], WIDTH * 2 + 2, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 10 && health_points < 20) {
      damage_heart(1);

    } else if (health_points > 20 && health_points < 30) {
      damage_heart(2);
    }
  }
  private void damage_heart(int heart_num) {
    hearts[heart_num].setColor(Color.BLACK);
    for (int i = 0; i < 2; ++i) {
      int x, y;
      int counter = 0;
      do {
        x = rand.nextInt(0, WIDTH);
        y = rand.nextInt(0, HEIGHT);
        ++counter;
      } while((Utility.isEqualColorsWithoutAlpha(hearts[heart_num].getPixel(x, y), Color.BLACK)
        || heart_mask.getPixel(x, y) != WHITE_IN_HEART_MASK) && counter < 1000);
      // System.out.println("[damage] Selected: x: " + x + " y: " + y + " color: " + Utility.getRGBAString(hearts[heart_num].getPixel(x, y)));

      if (counter >= 1000) {
        System.out.println("[damage] Count of tries is over");
      }
      hearts[heart_num].drawPixel(x, y);
    }
    health_map.drawPixmap(hearts[heart_num], (WIDTH + 1) * heart_num, HEIGHT * 2);
    health_texture = new Texture(health_map);
  }

  @Override
  public void heal(double health) {
    if (health_points >= MAX_HP) return;
    int i;
    for (i = 0; i < Math.floor(health); ++i) {
      ++health_points;
      simple_heal(1);
    }
    
    if (health_points >= MAX_HP) health_points = MAX_HP;
    int before = (int)Math.ceil(health_points);
    health_points += (health - i);
    int after = (int)Math.ceil(health_points);
    System.out.println("[treat] before: " + before + " after: " + after);
    if (before - after != 0) {
      simple_damage(health - i);
    }
    System.out.println("[treat] HP: " + health_points);
  }
  private void simple_heal(double delta) {
    if (health_points == 0) {

    } else if (health_points == 10 || (health_points - delta < 10 && health_points > 10)) {
      hearts[0] = generateHeart();
      health_map.drawPixmap(hearts[0], 0, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 0 && health_points < 10) {
      heal_heart(0);

    } else if (health_points == 20 || (health_points - delta < 20 && health_points > 20)) {
      hearts[1] = generateHeart();
      health_map.drawPixmap(hearts[1], WIDTH + 1, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 10 && health_points < 20) {
      heal_heart(1);

    } else if (health_points == 30 || (health_points - delta < 30 && health_points > 30)) {
      hearts[2] = generateHeart();
      health_map.drawPixmap(hearts[2], WIDTH * 2 + 2, HEIGHT * 2);
      health_texture = new Texture(health_map);
    } else if (health_points > 20 && health_points < 30) {
      heal_heart(2);

    }
  }
  private void heal_heart(int heart_num) {
    hearts[heart_num].setColor(HEALTH_COLOR);
    for (int i = 0; i < 2; ++i) {
      int x, y;
      int counter = 0;
      do {
        x = rand.nextInt(0, WIDTH);
        y = rand.nextInt(0, HEIGHT);
        ++counter;
      } while((!Utility.isEqualColorsWithoutAlpha(hearts[heart_num].getPixel(x, y), Color.BLACK)
        || heart_mask.getPixel(x, y) != WHITE_IN_HEART_MASK) && counter < 1000);
      // System.out.println("[heal] Selected: x: " + x + " y: " + y + " color: " + Utility.getRGBAString(hearts[heart_num].getPixel(x, y)));

      if (counter >= 1000) {
        System.out.println("[heal] Count of tries is over");
      }
      hearts[heart_num].drawPixel(x, y);
    }
    health_map.drawPixmap(hearts[heart_num], (WIDTH + 1) * heart_num, HEIGHT * 2);
    health_texture = new Texture(health_map);
  }

  // Increase food points
  public void saturation(double points) {
    points = Math.floor(points);
    if (food_points <= 0 && points > 0) {
      removeInfEffect("effects/damage", -1);
      removeInfEffect("effects/strength", -1);
    }
    food_points += points;
    if (food_points > MAX_FP) food_points = MAX_FP;

    System.out.println("[saturation] FP: " + food_points + " int: " + (food_points / 5));

    for (int i = 0; i < (int)Math.ceil(food_points / 5.0); ++i) {
      health_map.drawPixmap(full_food_point, 2 + i * 4, HEIGHT + 2);
    }
    health_texture = new Texture(health_map);

    if (rand.nextInt(10000) < 1000 + useLuck(200, -200)) {
      thirst(rand.nextInt(2, 5));
    }
  }

  // Decrease food points
  public void hunger(double points) {
    points = Math.floor(points);
    if (food_points > 0 && food_points - points <= 0) {
      addCycleEffect("effects/damage", 1, -1, 3, 1);
      addEffect("effects/strength", -1, -1);
    }
    food_points -= points;
    if (food_points < 0) {
      food_points = 0;
    }

    System.out.println("[hunger] FP: " + food_points + " int: " + (food_points / 5));

    for (int i = (int)Math.ceil(food_points / 5.0); i < MAX_FP / 5; ++i) {
      health_map.drawPixmap(empty_food_point, 2 + i * 4, HEIGHT + 2);
    }
    health_texture = new Texture(health_map);
  }

  // Increase thirsty points
  public void drink(double points) {
    points = Math.floor(points);
    if (thirsty_points <= 0 && thirsty_points + points > 0) {
      removeInfEffect("effects/speed", -3);

    } else if (thirsty_points <= 10 && thirsty_points + points > 10) {
      removeInfEffect("effects/speed", -2);

    } else if (thirsty_points <= 15 && thirsty_points + points > 15) {
      removeInfEffect("effects/speed", -1);
    }

    thirsty_points += points;
    if (thirsty_points > MAX_TP) thirsty_points = MAX_TP;

    System.out.println("[drink] TP: " + thirsty_points + " int: " + (thirsty_points / 5));

    for (int i = 0; i < (int)Math.ceil(thirsty_points / 5.0); ++i) {
      health_map.drawPixmap(full_thirsty_point, 2 + i * 4, 2);
    }
    health_texture = new Texture(health_map);
  }

  // Decrease thirsty points
  public void thirst(double points) {
    points = Math.floor(points);
    if (thirsty_points > 0 && thirsty_points - points <= 0) {
      addEffect("effects/speed", -3, -1);

    } else if (thirsty_points > 10 && thirsty_points - points <= 10) {
      addEffect("effects/speed", -2, -1);

    } else if (thirsty_points > 15 && thirsty_points - points <= 15) {
      addEffect("effects/speed", -1, -1);
    }

    thirsty_points -= points;
    if (thirsty_points < 0) thirsty_points = 0;

    System.out.println("[thirst] TP: " + thirsty_points + " int: " + (thirsty_points / 5));

    for (int i = (int)Math.ceil(thirsty_points / 5.0); i < MAX_TP / 5; ++i) {
      health_map.drawPixmap(empty_thirsty_point, 2 + i * 4, 2);
    }
    health_texture = new Texture(health_map);
  }
}
