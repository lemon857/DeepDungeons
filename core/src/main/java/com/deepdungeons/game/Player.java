package com.deepdungeons.game;

import java.util.HashSet;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {
  public static final int PLAYER_WIDTH = 8;
  public static final int PLAYER_HEIGHT = 8;

  private static final int START_BORDER = Room.START_BORDER;
  private static final int END_BORDER = Room.END_BORDER - PLAYER_WIDTH;

  private  static final Point POS_OFFSET = new Point(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2);

  private final DoublePoint pos;
  private Texture image;

  private Direction dir;

  private boolean non_actual;

  private final HashSet<Item> items;

  public Player(int x, int y) {
    this.pos = new DoublePoint(x, y);
    this.dir = Direction.Up;
    this.non_actual = true;
    this.items = new HashSet<>();
  }

  private void generateImage() {
    Pixmap map = new Pixmap(PLAYER_WIDTH, PLAYER_HEIGHT, Pixmap.Format.RGBA8888);
    map.setColor(1f, 1f, 1f, 1f);
    map.fillCircle(PLAYER_WIDTH / 2, PLAYER_HEIGHT / 2, (PLAYER_WIDTH / 2) - 1);

    map.setColor(0f, 0f, 0f, 1f);
    switch (dir) {
    case Up:
    default:   
      map.drawPixel(3, 3);
      map.drawPixel(5, 3);   
      break;
    case Down:  
        map.drawPixel(3, 5);
        map.drawPixel(5, 5);   
        break;
    case Right:  
      map.drawPixel(5, 3);
      map.drawPixel(5, 5);   
      break;
    case Left:  
        map.drawPixel(3, 3);
        map.drawPixel(3, 5);   
        break;
    }

    image = new Texture(map);
  }

  public boolean grabItem(Item item) {
    if (items.contains(item)) return false;
    items.add(item);
    return true;
  }

  public HashSet<Key> getKeys() {
    HashSet<Key> keys = new HashSet<>();

    for (Item item : items) {
      if (item.getType() == ItemType.Key) {
        keys.add((Key)item);
      }
    }

    return keys;
  }

  public void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

    if (pos.x < START_BORDER) pos.x = START_BORDER;
    else if (pos.x > END_BORDER) pos.x = END_BORDER;

    if (pos.y < START_BORDER) pos.y = START_BORDER;
    else if (pos.y > END_BORDER) pos.y = END_BORDER;

    if (x > 0) {
      dir = Direction.Right;
    } else if (x < 0) {
      dir = Direction.Left;
    }

    if (y > 0) {
      dir = Direction.Down;
    } else if (y < 0) {
      dir = Direction.Up;
    }
    non_actual = true;
  }

  public void setX(int x) {
    pos.x = x;
    if (pos.x < START_BORDER) pos.x = START_BORDER;
    else if (pos.x > END_BORDER) pos.x = END_BORDER;
  }
  public void setY(int y) {
    pos.y = y;
    if (pos.y < START_BORDER) pos.y = START_BORDER;
    else if (pos.y > END_BORDER) pos.y = END_BORDER;
  }

  public void update() {
    if (non_actual) {
      generateImage();
      non_actual = false;
    }
  }

  public void draw(SpriteBatch batch) {
    batch.draw(image, (float)Math.floor(pos.x), (float)Room.SCREEN_HEIGHT - (float)PLAYER_HEIGHT - (float)Math.floor(pos.y), (float)PLAYER_WIDTH, (float)PLAYER_HEIGHT);
  }

  public DoublePoint getPos() {
    return pos;
  }

  public DoublePoint getCenterPos() {
    return DoublePoint.sum(pos, POS_OFFSET);
  }
}
