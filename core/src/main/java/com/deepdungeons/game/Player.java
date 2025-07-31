package com.deepdungeons.game;

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

  private Texture inventory_image;

  private Direction dir;

  private boolean non_actual;

  private Item inventory;

  public Player(int x, int y) {
    this.pos = new DoublePoint(x, y);
    this.dir = Direction.Up;
    this.non_actual = true;
    Pixmap map = new Pixmap(18, 6, Pixmap.Format.RGBA8888);
    inventory_image = new Texture(map);
  }

  private void generatePlayerImage() {
    Pixmap map = new Pixmap(PLAYER_WIDTH, PLAYER_HEIGHT, Pixmap.Format.RGBA8888);
    map.setColor(0.7f, 0.7f, 0.7f, 0.7f);
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

  private void generateInventoryImage() {
    if (inventory != null) {
      inventory_image = new Texture(inventory.getImage());
    } else {
      Pixmap map = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
      inventory_image = new Texture(map);
    }
  }

  public Item getItem() {
    return inventory; 
  }

  public void grabItem(Item item) {
    inventory = item;
    non_actual = true;
  }

  public boolean isDropAvailable() {
    return inventory != null;
  }

  public Item dropItem() {
    Item res = inventory;
    inventory = null;
    non_actual = true;
    return res;
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
      generatePlayerImage();
      generateInventoryImage();
      non_actual = false;
    }
  }

  public void draw(SpriteBatch batch) {
    batch.draw(image, (float)Math.floor(pos.x), (float)Room.SCREEN_HEIGHT - (float)PLAYER_HEIGHT - (float)Math.floor(pos.y),
    (float)PLAYER_WIDTH, (float)PLAYER_HEIGHT);
    batch.draw(inventory_image, (float)Math.floor(pos.x) + 5.5f, (float)Room.SCREEN_HEIGHT - (float)PLAYER_HEIGHT - (float)Math.floor(pos.y) + 4.5f, 6, 3);
  }

  public DoublePoint getPos() {
    return pos;
  }

  public DoublePoint getCenterPos() {
    return DoublePoint.sum(pos, POS_OFFSET);
  }
}
