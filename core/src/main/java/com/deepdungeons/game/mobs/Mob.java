package com.deepdungeons.game.mobs;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.Character;
import com.deepdungeons.game.Main;
import com.deepdungeons.game.Room;
import com.deepdungeons.game.items.Item;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.LootTable;
import com.deepdungeons.game.utils.Point;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;
import com.deepdungeons.game.weapons.CloseRangeWeapon;
import com.deepdungeons.game.weapons.Hand;

public class Mob extends Character {
  private static HashMap<String, Mob> static_mobs;

  public static void initStaticMobs() {
    static_mobs = new HashMap<>();
  }

  public static void addStaticMob(String name, Mob mob) {
    static_mobs.put(name, mob);
  }

  public static Mob getStaticMob(String name) {
    Mob mob = static_mobs.get(name).clone();
    if (mob == null) {
      System.err.println("Mob " + name + " is not found");
      return null;
    }
    mob.generateRandomPos();
    return mob;
  }

  public static Mob getStaticMob(String name, Vector2d pos) {
    Mob mob = static_mobs.get(name).clone();
    if (mob == null) {
      System.err.println("Mob " + name + " is not found");
      return null;
    }
    mob.pos = pos;
    mob.updateSpritePos();
    return mob;
  }

  private static final Color DAMAGE_COLOR = new Color(1f, 0.7f, 0.7f, 0.7f);

  protected Pixmap image;

  protected Vector2d pos;
  protected Direction dir;

  private Sprite sprite;

  protected Item inventory;
  protected Texture inventory_texture;

  protected double attack_timer;
  protected double cooldown;

  protected final Random rand;

  protected Vector2d size;
  protected Point start_border;
  protected Point end_border;

  protected double attack_anim_timer;
  protected boolean attack_anim_play;
  protected static final double attack_anim_time = 0.2;

  protected double damage_anim_timer;
  protected boolean damage_anim_play;
  protected static final double damage_anim_time = 0.25;

  private final LootTable table;

  private final int id;

  private static int current_id = 1;

  private final Tier tier;

  public enum Tier {
    Humble, General, Wicked, Aggressive
  }

  public Mob(String path_to_texture, Tier tier, double move_speed, double attack_speed, double strength, LootTable table) {
    super(1, move_speed, attack_speed, strength);

    this.tier = tier;
    this.table = table;
    this.id = current_id;
    ++current_id;
    this.rand = new Random();
    this.cooldown = 0;
    this.attack_timer = 0;
    this.dir = Direction.Undefined;
    this.damage_anim_timer = damage_anim_time + 1;
    this.damage_anim_play = false;
    this.attack_anim_timer = attack_anim_time + 1;
    this.attack_anim_play = false;
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.sprite = new Sprite(new Texture(image));
  }

  public Mob(Pixmap map, Tier tier, double move_speed, double attack_speed, double strength, LootTable table) {
    super(1, move_speed, attack_speed, strength);

    this.tier = tier;
    this.table = table;
    this.id = current_id;
    ++current_id;
    this.rand = new Random();
    this.cooldown = 0;
    this.attack_timer = 0;
    this.dir = Direction.Undefined;
    this.damage_anim_timer = damage_anim_time + 1;
    this.damage_anim_play = false;
    this.attack_anim_timer = attack_anim_time + 1;
    this.attack_anim_play = false;
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.sprite = new Sprite(new Texture(image));
  }
  public void update(double delta) {
    updateTimers(delta);
  }

  public final void updateTimers(double delta) {
    attack_timer += delta;

    if (attack_anim_play) {
      attack_anim_timer += delta;
      if (attack_anim_timer >= attack_anim_time) {
        size.y = size.y / 0.9;
        updateSpriteSize();
        attack_anim_play = false;
      }
    }

    if (damage_anim_play) {
      damage_anim_timer += delta;
      if (damage_anim_timer >= damage_anim_time) {
        // change color of mob to default
        sprite.setColor(1, 1, 1, 1);
        damage_anim_play = false;
      }
    }
  }

  protected void updateSize(double koef) {
    setSize(this.image.getWidth() * koef, this.image.getHeight() * koef);
  }

  protected void updateSpritePos() {
    sprite.setPosition((float)pos.x, (float)pos.y);
  }

  protected void updateSpriteSize() {
    sprite.setSize((float)size.x, (float)size.y);
  }

  protected void generateImage() {
    image = new Pixmap(1, 1, Pixmap.Format.RGB888);

    sprite = new Sprite(new Texture(image));
  }

  public final int getId() {
    return id;
  }

  public final Tier getTier() {
    return tier;
  }

  public final double getHealthPoints() {
    return id;
  }

  public final Vector2d getCenterPos() {
    return Vector2d.sum(pos, Vector2d.div(size, 2));
  }

  public final Vector2d getPos() {
    return pos;
  }

  public final Vector2d getSize() {
    return size;
  }

  public final Pixmap getImage() {
    return image;
  }

  public final void generateRandomPos() {
    pos.x = rand.nextDouble(size.x + Room.START_BORDER.x + 1, Room.END_BORDER.x - size.x - 1);
    pos.y = rand.nextDouble(Room.START_BORDER.y + size.y + 1, Room.END_BORDER.y - size.y - 1);
    updateSpritePos();
  }

  public void startAttackAnim() {
    attack_anim_timer = 0;
    attack_anim_play = true;
    size.y *= 0.9;
    updateSpriteSize();
  }

  public final void setSize(double width, double height) {
    start_border = Point.sum(Room.START_BORDER, new Point(1, 1));
    end_border = Point.sub(Room.END_BORDER, new Point((int)Math.ceil(width), (int)Math.ceil(height)));
    size = new Vector2d(width, height);
    updateSpriteSize();
  }
  public final void translate(Vector2d vector) {
    translate(vector.x, vector.y);
  }

  public final void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

    if (pos.x < start_border.x) pos.x = start_border.x;
    else if (pos.x > end_border.x) pos.x = end_border.x;

    if (pos.y < start_border.y) pos.y = start_border.y;
    else if (pos.y > end_border.y) pos.y = end_border.y;

    if (Utility.getTranslateDirection(x, y) != Direction.Undefined) dir = Utility.getTranslateDirection(x, y);
  }

  private void generateInventoryTexture() {
    if (inventory != null) {
      inventory_texture = new Texture(inventory.getImage());
    } else {
      Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      inventory_texture = new Texture(map);
    }
  }

  public void pickUpItem(Item item) {
    inventory = item;
    generateInventoryTexture();
  }

  public void pickUpItem(String static_name) {
    inventory = Item.getStaticItem(static_name);
    generateInventoryTexture();
  }

  public final void draw(SpriteBatch batch) {
    updateSpritePos();
    sprite.draw(batch);
    if (inventory != null) {
      if (inventory.isTextureFromFile()) {
        batch.draw(inventory_texture, (float)pos.x + (float)size.x * 0.7f, (float)pos.y + (float)size.y * 0.65f, 
        (float)inventory.getSize().x * 0.6f, (float)inventory.getSize().y * 0.6f);
      } else {
        batch.draw(inventory_texture, (float)pos.x + (float)size.x * 0.7f, (float)pos.y + (float)size.y * 0.65f + (float)inventory.getSize().y * 0.6f, 
        (float)inventory.getSize().x * 0.6f, (float)inventory.getSize().y * -0.6f);
      }
    }
  }

  public final Item getDrop() {
    if (table == null) return null;
    
    return table.nextItem();
  }

  protected boolean attack() {
    if (attack_timer < cooldown) return false;
    double max_distance = 0;
    double max_angle = 0;
    double damage = 0;
    boolean is_hand = true;
    if (inventory instanceof CloseRangeWeapon) {
      CloseRangeWeapon weapon = (CloseRangeWeapon)inventory;
      damage = weapon.getDamage();
      max_angle = weapon.getAngle();
      max_distance = weapon.getDistance();
      cooldown = weapon.getCooldown();
      is_hand = false;
    } else { // aka hand
      damage = Hand.getDamage(rand);
      max_distance = Hand.DISTANCE;
      max_angle = Hand.ANGLE;
      cooldown = Hand.getCooldown(rand);
    }
    Vector2d v2 = new Vector2d(pos, Main.player.getPos());

    if (v2.length() < max_distance && Vector2d.angle(Utility.getDirectionVector(dir), v2) < max_angle) {
      Main.player.damage(damage);
      if (is_hand) {
        Hand.playSound();
      } else {
        inventory.playSound();
      }
      attack_timer = 0;
      return true;
    }
    return false;
  }

  protected LootTable getTable() {
    return table;
  }

  @Override
  public boolean damage(double dmg) {
    // change color of mob to red
    sprite.setColor(DAMAGE_COLOR);
    damage_anim_play = true;
    damage_anim_timer = 0;
    health_points.v -= dmg;
    return health_points.v <= 0;
  }

  @Override
  public Mob clone() {
    Mob mob = new Mob(image, this.tier, getMoveSpeed(), getAttackSpeed(), getStrength(), this.table);

    mob.health_points.v = this.health_points.v;
    mob.cooldown = this.cooldown;
    mob.attack_timer = this.attack_timer;
    mob.dir = this.dir;

    return mob;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Mob)) return false;
    Mob other = (Mob) obj;
    return this.id == other.id;
  }
  @Override
  public int hashCode() {
    return id;
  }
}