package com.deepdungeons.game.items;

import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.Room;
import com.deepdungeons.game.utils.Vector2d;

public class Item {
  private static HashMap<String, Item> static_items;

  public static void initStaticItems() {
    static_items = new HashMap<>();
  }

  public static void addStaticItem(String name, Item item) {
    static_items.put(name, item);
  }

  public static Item getStaticItem(String name) {
    Item item = static_items.get(name);
    if (item == null) {
      System.err.println("[Error] Item " + name + " is not found");
      return null;
    }
    Item res = item.clone();
    res.generateRandomPos();
    return res;
  }

  public static Item getStaticItem(String name, Vector2d pos) {
    Item item = static_items.get(name);
    if (item == null) {
      System.err.println("[Error] Item " + name + " is not found");
      return null;
    }
    Item res = item.clone();
    res.pos = pos;
    return res;
  }

  protected Pixmap image;

  protected Texture texture;

  protected Vector2d pos;
  protected Vector2d size;

  protected boolean is_texture_from_file = false;
  
  protected Sound sound;

  protected final Random rand;

  private final int id;

  private static int current_id = 1;

  private final Type type;

  private final Tier tier;

  private final String name;

  public enum Type {
    None, Key, Weapon, ForCraft, Food, Drink, Coin
  }

  public enum Tier {
    Common, Uncommon, Startling, Incredible
  }

  protected Item(Type type, Tier tier, String name, String path_to_texture) {
    this.type = type;
    this.tier = tier;
    this.name = name;
    this.id = current_id;
    ++current_id;
    this.rand = new Random();
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(this.image);
    this.is_texture_from_file = true;
    this.sound = null;
  }

  protected Item(Type type, Tier tier, String name, Pixmap map) {
    this.type = type;
    this.tier = tier;
    this.name = name;
    this.id = current_id;
    ++current_id;
    this.rand = new Random();
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.texture = new Texture(this.image);
    this.sound = null;
  }

  protected void updateSize(double koef) {
    this.size = new Vector2d(this.image.getWidth() * koef, this.image.getHeight() * koef);
  }

  public void update(double delta) {}

  @Override
  public Item clone() {
    Item item = new Item(type, tier, name, image);
    return item;
  }

  public final void draw(SpriteBatch batch) {
    if (is_texture_from_file) {
      batch.draw(texture, (float)pos.x, (float)pos.y, (float)size.x, (float)size.y);
    } else {
      batch.draw(texture, (float)pos.x, (float)pos.y + (float)size.y, (float)size.x, -(float)size.y);
    }
  }

  public final void generateRandomPos() {
    pos.x = rand.nextDouble(size.x + Room.START_BORDER.x + 1, Room.END_BORDER.x - size.x - 1);
    pos.y = rand.nextDouble(Room.START_BORDER.y + size.y + 1, Room.END_BORDER.y - size.y - 1);
  }

  public final Pixmap getImage() {
    return image;
  }

  public final int getId() {
    return id;
  }

  public final Type getType() {
    return type;
  }

  public final Tier getTier() {
    return tier;
  }

  public final String getName() {
    return name;
  }

  public final Vector2d getPos() {
    return pos;
  }

  public final boolean isTextureFromFile() {
    return is_texture_from_file;
  }

  public final Vector2d getCenterPos() {
    return Vector2d.sum(pos, Vector2d.div(size, 2));
  }

  public final Vector2d getSize() {
    return size;
  }

  public final void setPos(Vector2d new_pos) {
    pos = new_pos;
    if (pos.x + size.x > Room.END_BORDER.x) {
      pos.x = Room.END_BORDER.x - size.x - 1;
    }
    if (pos.x < Room.START_BORDER.x) {
      pos.x = Room.START_BORDER.x + 1;
    }
    if (pos.y + size.y > Room.END_BORDER.y) {
      pos.y = Room.END_BORDER.y - size.y - 1;
    }
    if (pos.y < Room.START_BORDER.y) {
      pos.y = Room.START_BORDER.y + 1;
    }
  }

  public final void setCenterPos(Vector2d new_pos) {
    pos = Vector2d.sub(new_pos, Vector2d.div(size, 2));
    if (pos.x + size.x > Room.END_BORDER.x) {
      pos.x = Room.END_BORDER.x - size.x - 1;
    }
    if (pos.x < Room.START_BORDER.x + 1) {
      pos.x = Room.START_BORDER.x + 1;
    }
    if (pos.y + size.y > Room.END_BORDER.y) {
      pos.y = Room.END_BORDER.y - size.y - 1;
    }
    if (pos.y < Room.START_BORDER.y + 1) {
      pos.y = Room.START_BORDER.y + 1;
    }
  }

  public final void playSound() {
    if (sound == null) {
      System.err.println("[Item] Sound is null!");
      return;
    }
    sound.play();
  }

  public void dispose() {
    if (sound != null) sound.dispose();
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!(obj instanceof Item)) return false;
    Item other = (Item) obj;
    return this.id == other.id;
  }
  @Override
  public int hashCode() {
    return id;
  }
}
