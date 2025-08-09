package com.deepdungeons.game;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class Player {
  public static final int WIDTH = 7;
  public static final int HEIGHT = 7;

  public static final int MAX_HP = 30;
  public static final int MAX_FP = 25;
  public static final int MAX_TP = 25;

  public static final int EYE_DOWN = (HEIGHT / 2) - 1;
  public static final int EYE_UP = HEIGHT - EYE_DOWN - 1;
  public static final int EYE_MIDDLE = EYE_DOWN + (EYE_UP - EYE_DOWN) / 2;

  public static final int EYE_LEFT = (WIDTH / 2) - 1;
  public static final int EYE_RIGHT = WIDTH - EYE_LEFT - 1;

  private static final Point START_BORDER = Point.sum(Room.START_BORDER, new Point(1, 1));
  private static final Point END_BORDER = Point.sub(Room.END_BORDER, new Point(WIDTH, HEIGHT));

  private static final Color COLOR = new Color(0.8f, 0.7f, 0.9f, 0.7f);
  private static final Color HEALTH_COLOR = new Color(1f, 0f, 0.2f, 1f);
  private static final Color DEAD_HEALTH_COLOR = new Color(0.8f, 0.5f, 0.65f, 1f);
  private static final Color EYE_COLOR = new Color(0, 0, 0, 1);

  private static final int WHITE_IN_HEART_MASK = -1;

  private static final double HUNGER_DAMAGE_COOLDOWN = 3;
  private static final double STRENGTH_HUNGER_COOLDOWN = 4;
  private static final double FAST_ATTACK_THIRSTY_COOLDOWN = 5;

  private double hunger_damage_timer;
  private double strength_hunger_timer;
  private double fast_attack_thirsty_timer;

  private final Random rand;

  private final Vector2d pos;
  private Texture image;

  private Texture inventory_texture;

  private Pixmap health_map;

  private final Pixmap hearts[];
  private Pixmap heart_mask;

  private final Pixmap full_food_point;
  private final Pixmap empty_food_point;

  private final Pixmap full_thirsty_point;
  private final Pixmap empty_thirsty_point;

  private Texture health_texture;

  private Direction dir;

  private boolean non_actual;

  private Item inventory;

  private double health_points;
  private int food_points;
  private int thirsty_points;

  private double move_speed;
  private double attack_speed;
  private double strength;
  private double luck;

  public Player(int x, int y) {
    this.rand = new Random();
    this.pos = new Vector2d(x, y);
    this.dir = Direction.Up;
    this.non_actual = false;
    this.health_points = MAX_HP;
    this.food_points = MAX_FP;
    this.thirsty_points = MAX_TP;

    this.move_speed = 40;
    this.attack_speed = 1;
    this.strength = 1;
    this.luck = 0;

    this.hunger_damage_timer = 0;
    this.strength_hunger_timer = 0;
    this.fast_attack_thirsty_timer = 0;

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

    image = new Texture(map);
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
    inventory = item;
    generateInventoryTexture();
  }

  public boolean isDropAvailable() {
    return inventory != null;
  }

  public Item dropItem() {
    Item res = inventory;
    inventory = null;
    generateInventoryTexture();
    return res;
  }
  public void translate(Vector2d vector) {
    translate(vector.x, vector.y);
  }
  public void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

    if (pos.x < START_BORDER.x) pos.x = START_BORDER.x;
    else if (pos.x > END_BORDER.x) pos.x = END_BORDER.x;

    if (pos.y < START_BORDER.y) pos.y = START_BORDER.y;
    else if (pos.y > END_BORDER.y) pos.y = END_BORDER.y;

    if (Utility.getTranslateDirection(x, y) != Direction.Undefined) dir = Utility.getTranslateDirection(x, y);
    
    non_actual = true;
  }

  public void setX(int x) {
    pos.x = x;
    if (pos.x < START_BORDER.x) pos.x = START_BORDER.x;
    else if (pos.x > END_BORDER.x) pos.x = END_BORDER.x;
  }
  public void setY(int y) {
    pos.y = y;
    if (pos.y < START_BORDER.y) pos.y = START_BORDER.y;
    else if (pos.y > END_BORDER.y) pos.y = END_BORDER.y;
  }

  public void update(double delta) {
    if (non_actual) {
      generatePlayerImage();
      non_actual = false;
    }

    if (food_points == 0) {
      hunger_damage_timer += delta;
      if (hunger_damage_timer >= HUNGER_DAMAGE_COOLDOWN) {
        damage(1);
        hunger_damage_timer = 0;
      }
    }

    if (strength > 1) {
      strength_hunger_timer += delta;
      if (strength_hunger_timer >= STRENGTH_HUNGER_COOLDOWN) {
        hunger(1);
        strength_hunger_timer = 0;
      }
    }

    if (attack_speed > 1) {
      fast_attack_thirsty_timer += delta;
      if (fast_attack_thirsty_timer >= FAST_ATTACK_THIRSTY_COOLDOWN) {
        thirst(1);
        fast_attack_thirsty_timer = 0;
      }
    }
  }

  public void draw(SpriteBatch batch) {
    // correct coords for Pixmap
    batch.draw(image, (float)pos.x, (float)pos.y + (float)HEIGHT,
    (float)WIDTH, -(float)HEIGHT);
    if (inventory != null) {
      if (inventory.isTExtureFromFile()) {
        batch.draw(inventory_texture, (float)pos.x + WIDTH * 0.7f, (float)pos.y + HEIGHT * 0.65f, 
        (float)inventory.getSize().x * 0.6f, (float)inventory.getSize().y * 0.6f);
      } else {
        batch.draw(inventory_texture, (float)pos.x + WIDTH * 0.7f, (float)pos.y + HEIGHT * 0.65f + (float)inventory.getSize().y * 0.6f, 
        (float)inventory.getSize().x * 0.6f, (float)inventory.getSize().y * -0.6f);
      }
    }
    batch.draw(health_texture, 1, Room.SCREEN_HEIGHT - 1, WIDTH * 4, -HEIGHT * 3);
  }
  
  public void setMoveSpeed(double move_speed) {
    this.move_speed = move_speed;
  }

  public void setAttackSpeed(double attack_speed) {
    this.attack_speed = attack_speed;
  }

  public void setStrength(double strength) {
    this.strength = strength;
  }

  public void setLuck(double luck) {
    this.luck = luck;
  }

  public double getMoveSpeed() {
    return move_speed;
  }

  public double getAttackSpeed() {
    return attack_speed;
  }

  public double getStrength() {
    return strength;
  }

  public double useLuck(double negativeMax, double positiveMax) {
    if (luck == 0) return 0;
    else if (luck > 0) return rand.nextDouble(luck * Math.abs(positiveMax)) * Math.signum(positiveMax);

    return rand.nextDouble(luck * Math.abs(negativeMax)) * Math.signum(negativeMax);
  }

  public Vector2d getPos() {
    return pos;
  }

  public Vector2d getCenterPos() {
    return Vector2d.sum(pos, new Vector2d(WIDTH / 2, HEIGHT / 2));
  }

  public Vector2d getDirection() {
    return Utility.getDirectionVector(dir);
  }

  public boolean isDie() {
    return health_points <= 0;
  }

  // True if it dead
  public boolean damage(double dmg) {
    if (health_points < 0) return true;

    int i;
    for (i = 0; i < Math.floor(dmg); ++i) {
      --health_points;
      simple_damage(1);
    }
    int before = (int)Math.ceil(health_points);
    health_points -= (dmg - i);
    int after = (int)Math.ceil(health_points);
    System.out.println("[damage] before: " + before + " after: " + after);
    if (before - after != 0) {
      simple_damage(dmg - i);
    }

    System.out.println("[damage] HP: " + health_points);
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
      System.out.println("[damage] Selected: x: " + x + " y: " + y + " color: " + Utility.getRGBAString(hearts[heart_num].getPixel(x, y)));

      if (counter >= 1000) {
        System.out.println("[damage] Count of tries is over");
      }
      hearts[heart_num].drawPixel(x, y);
    }
    health_map.drawPixmap(hearts[heart_num], (WIDTH + 1) * heart_num, HEIGHT * 2);
    health_texture = new Texture(health_map);
  }

  public void treat(double health) {
    if (health_points >= MAX_HP) return;
    int i;
    for (i = 0; i < Math.floor(health); ++i) {
      ++health_points;
      simple_treat(1);
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
  private void simple_treat(double delta) {
    if (health_points == 0) {

    } else if (health_points == 10 || (health_points - delta < 10 && health_points > 10)) {
      hearts[0] = generateHeart();
      health_map.drawPixmap(hearts[0], 0, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 0 && health_points < 10) {
      treat_heart(0);

    } else if (health_points == 20 || (health_points - delta < 20 && health_points > 20)) {
      hearts[1] = generateHeart();
      health_map.drawPixmap(hearts[1], WIDTH + 1, HEIGHT * 2);
      health_texture = new Texture(health_map);

    } else if (health_points > 10 && health_points < 20) {
      treat_heart(1);

    } else if (health_points == 30 || (health_points - delta < 30 && health_points > 30)) {
      hearts[2] = generateHeart();
      health_map.drawPixmap(hearts[2], WIDTH * 2 + 2, HEIGHT * 2);
      health_texture = new Texture(health_map);
    } else if (health_points > 20 && health_points < 30) {
      treat_heart(2);

    }
  }
  private void treat_heart(int heart_num) {
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
      System.out.println("[treat] Selected: x: " + x + " y: " + y + " color: " + Utility.getRGBAString(hearts[heart_num].getPixel(x, y)));

      if (counter >= 1000) {
        System.out.println("[treat] Count of tries is over");
      }
      hearts[heart_num].drawPixel(x, y);
    }
    health_map.drawPixmap(hearts[heart_num], (WIDTH + 1) * heart_num, HEIGHT * 2);
    health_texture = new Texture(health_map);
  }

  // Increase food points
  public void saturation(int points) {
    food_points += points;
    if (food_points > MAX_FP) food_points = MAX_FP;

    System.out.println("[saturation] FP: " + food_points + " int: " + (food_points / 5));

    for (int i = 0; i < (int)Math.ceil(food_points / 5.0); ++i) {
      health_map.drawPixmap(full_food_point, 2 + i * 4, HEIGHT + 2);
    }
    health_texture = new Texture(health_map);
  }

  // Decrease food points
  public void hunger(int points) {
    food_points -= points;
    if (food_points < 0) food_points = 0;

    System.out.println("[hunger] FP: " + food_points + " int: " + (food_points / 5));

    for (int i = (int)Math.ceil(food_points / 5.0); i < MAX_FP / 5; ++i) {
      health_map.drawPixmap(empty_food_point, 2 + i * 4, HEIGHT + 2);
    }
    health_texture = new Texture(health_map);
  }

  // Increase thirsty points
  public void drink(int points) {
    thirsty_points += points;
    if (thirsty_points > MAX_TP) thirsty_points = MAX_TP;

    System.out.println("[drink] TP: " + thirsty_points + " int: " + (thirsty_points / 5));

    for (int i = 0; i < (int)Math.ceil(thirsty_points / 5.0); ++i) {
      health_map.drawPixmap(full_thirsty_point, 2 + i * 4, 2);
    }
    health_texture = new Texture(health_map);
  }

  // Decrease thirsty points
  public void thirst(int points) {
    thirsty_points -= points;
    if (thirsty_points < 0) thirsty_points = 0;

    System.out.println("[thirst] TP: " + thirsty_points + " int: " + (thirsty_points / 5));

    for (int i = (int)Math.ceil(thirsty_points / 5.0); i < MAX_TP / 5; ++i) {
      health_map.drawPixmap(empty_thirsty_point, 2 + i * 4, 2);
    }
    health_texture = new Texture(health_map);
  }
}
