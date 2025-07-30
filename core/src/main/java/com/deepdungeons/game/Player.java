package com.deepdungeons.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Player {

  private final DoublePoint pos;
  private Texture image;

  private Direction dir;

  private boolean non_actual;

  public Player(int x, int y) {
    this.pos = new DoublePoint(x, y);
    this.dir = Direction.Up;
    this.non_actual = true;
  }

  private void GenerateImage() {
    Pixmap map = new Pixmap(8, 8, Pixmap.Format.RGBA8888);
    map.setColor(1f, 1f, 1f, 1f);
    map.fillCircle(4, 4, 3);

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

  public void translate(double x, double y) {
    pos.x += x;
    pos.y += y;

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
  }
  public void setY(int y) {
    pos.y = y;
  }

  public void update() {
    if (non_actual) {
      GenerateImage();
      non_actual = false;
    }
  }

  public void draw(SpriteBatch batch) {
    batch.draw(image, (int)Math.floor(pos.x - 4), (int)Math.floor(50 - (pos.y - 4)), 8, 8);
  }

  public DoublePoint getPos() {
    return pos;
  }
}
