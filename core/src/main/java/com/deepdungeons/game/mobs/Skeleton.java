package com.deepdungeons.game.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.Point;
import com.deepdungeons.game.Room;
import com.deepdungeons.game.Vector2d;

public class Skeleton extends Mob {

  public static final int WIDTH = 7;
  public static final int HEIGHT = 7;

  public static final double MAX_HP_LOW = 1;
  public static final double MAX_HP_HIGH = 3;

  private static final Point START_BORDER = Point.sum(Room.START_BORDER, new Point(1, 1));
  private static final Point END_BORDER = Point.sub(Room.END_BORDER, new Point(WIDTH, HEIGHT));

  private static final Color COLOR = new Color(0.7f, 0.7f, 0.7f, 0.7f);
  private static final double SPEED = 50f; // TODO: use random speed

  public Skeleton(Vector2d pos) {
    super(Mob.Tier.Humble);
    this.pos = pos;
    this.size = new Vector2d(WIDTH, HEIGHT);
    this.health_points = rand.nextDouble(MAX_HP_LOW, MAX_HP_HIGH + 1);
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
      if (pos.x < START_BORDER.x) pos.x = START_BORDER.x;
      else if (pos.x > END_BORDER.x) pos.x = END_BORDER.x;

      if (pos.y < START_BORDER.y) pos.y = START_BORDER.y;
      else if (pos.y > END_BORDER.y) pos.y = END_BORDER.y;
    }
  }
  
  @Override
  protected final void generateImage() {
    image = new Pixmap(WIDTH, HEIGHT, Pixmap.Format.RGBA8888);
    image.setColor(COLOR);
    image.fillCircle(WIDTH / 2, HEIGHT / 2, WIDTH / 2);

    texture = new Texture(image);
  }

  @Override
  public boolean damage(double dmg) {
    health_points -= dmg;
    for (int i = 0; i < 2; ++i) {
      int x = rand.nextInt(0, WIDTH);
      int y = rand.nextInt(0, HEIGHT);
      while (image.getPixel(x, y) == 0) {
        x = rand.nextInt(0, WIDTH);
        y = rand.nextInt(0, HEIGHT);
      }
      image.setColor(0, 0, 0, 1);
      image.drawPixel(x, y);
    }
    texture = new Texture(image);
    return health_points <= 0;
  }
}