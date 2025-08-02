package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.items.Item;

public class Player {
  public static final int WIDTH = 7;
  public static final int HEIGHT = 7;

  private static final int START_BORDER = Room.START_BORDER + 1;
  private static final int END_BORDER = Room.END_BORDER - WIDTH;

  private  static final Point POS_OFFSET = new Point(WIDTH / 2, HEIGHT / 2);

  private static final Color COLOR = new Color(0.8f, 0.7f, 0.9f, 0.7f);
  private static final Color EYE_COLOR = new Color(0, 0, 0, 1);

  private final Vector2d pos;
  private Texture image;

  private Texture inventory_image;

  private Direction dir;

  private boolean non_actual;

  private Item inventory;

  public enum Direction {
    Up, Down, Right, Left,
    UpRight, UpLeft, DownRight, DownLeft
  }

  public static Vector2d GetDirectionVector(Direction dir) {
    Vector2d delta = new Vector2d();
    switch (dir) {
      case Up: delta.y = -1; break;
      case Right: delta.x = 1; break;
      case Down: delta.y = 1; break;
      case Left: delta.x = -1; break;
      default: break;
    }
    return delta;
  }

  public Player(int x, int y) {
    this.pos = new Vector2d(x, y);
    this.dir = Direction.Up;
    this.non_actual = true;
    Pixmap map = new Pixmap(18, 6, Pixmap.Format.RGBA8888);
    inventory_image = new Texture(map);
  }

  private void generatePlayerImage() {
    Pixmap map = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    map.setColor(COLOR);
    map.fillCircle(WIDTH / 2, HEIGHT / 2, (WIDTH / 2));

    map.setColor(EYE_COLOR);
    switch (dir) {
    case Up:
    default:
      map.drawPixel(2, 2);
      map.drawPixel(4, 2);   
      break;
    case Down:
        map.drawPixel(2, 4);
        map.drawPixel(4, 4);   
        break;
    case Right:
      map.drawPixel(3, 3);
      map.drawPixel(5, 3);   
      break;
    case Left:
      map.drawPixel(1, 3);
      map.drawPixel(3, 3); 
      break;
    case UpRight:
      map.drawPixel(3, 2);
      map.drawPixel(5, 2);   
      break;
    case UpLeft:
      map.drawPixel(1, 2);
      map.drawPixel(3, 2);   
      break;
    case DownRight:
        map.drawPixel(3, 4);
        map.drawPixel(5, 4);   
        break;
    case DownLeft:
        map.drawPixel(1, 4);
        map.drawPixel(3, 4);   
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

  public void pickupItem(Item item) {
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
  public void translate(Vector2d vector) {
    translate(vector.x, vector.y);
  }
  public void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

    if (pos.x < START_BORDER) pos.x = START_BORDER;
    else if (pos.x > END_BORDER) pos.x = END_BORDER;

    if (pos.y < START_BORDER) pos.y = START_BORDER;
    else if (pos.y > END_BORDER) pos.y = END_BORDER;

    if (x > 0) {
      if (y > 0) {
        dir = Direction.UpRight;
      } else if (y < 0) {
        dir = Direction.DownRight;
      } else {
        dir = Direction.Right;
      }
    } else if (x < 0) {
      if (y > 0) {
        dir = Direction.UpLeft;
      } else if (y < 0) {
        dir = Direction.DownLeft;
      } else {
        dir = Direction.Left;
      }
    } else {
      if (y > 0) {
        dir = Direction.Up;
      } else if (y < 0) {
        dir = Direction.Down;
      }
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
    batch.draw(image, (float)pos.x, (float)pos.y,
    (float)WIDTH, (float)HEIGHT);
    if (inventory != null) {
      batch.draw(inventory_image, (float)pos.x + 5.5f, (float)pos.y + 4.5f, 
      inventory.getSize().x * 0.6f, inventory.getSize().y * 0.6f);
    }
  }

  public Vector2d getPos() {
    return pos;
  }

  public Vector2d getCenterPos() {
    return Vector2d.sum(pos, POS_OFFSET);
  }
}
