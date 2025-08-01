package com.deepdungeons.game.mobs;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.Vector2d;
import com.deepdungeons.game.Room;

public class Skeleton extends Mob {

  public static final int WIDTH = 8;
  public static final int HEIGHT = 8;

  private static final int START_BORDER = Room.START_BORDER;
  private static final int END_BORDER = Room.END_BORDER - WIDTH;

  private static final Color COLOR = new Color(0.7f, 0.7f, 0.7f, 0.7f);
  private static final double SPEED = 50f;

  private final Random rand;

  public Skeleton(Vector2d pos) {
    super();
    this.pos = pos;
    this.rand = new Random(System.currentTimeMillis());
    this.size = new Vector2d(WIDTH, HEIGHT);
    generateImage();
  }

  @Override
  public final void update(double delta) {
    if (rand.nextInt(10000) < 700) {
      int dir = rand.nextInt(4);
      switch(dir) {
      case 0: pos.y -= delta * SPEED; break;
      case 1: pos.x += delta * SPEED; break;
      case 2: pos.y += delta * SPEED; break;
      case 3: pos.x -= delta * SPEED; break;
      }
      if (pos.x < START_BORDER) pos.x = START_BORDER;
      else if (pos.x > END_BORDER) pos.x = END_BORDER;

      if (pos.y < START_BORDER) pos.y = START_BORDER;
      else if (pos.y > END_BORDER) pos.y = END_BORDER;
    }
  }
  
  @Override
  protected final void generateImage() {
    image = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    image.setColor(COLOR);
    image.fillCircle(WIDTH / 2, HEIGHT / 2, (WIDTH / 2) - 1);

    texture = new Texture(image);
  }
}