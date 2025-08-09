package com.deepdungeons.game.mobs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class Skeleton extends Mob {

  public static final int WIDTH = 7;
  public static final int HEIGHT = 7;

  public static final double MAX_HP_LOW = 1;
  public static final double MAX_HP_HIGH = 3;

  private static final Color COLOR = new Color(0.7f, 0.7f, 0.7f, 0.7f);
  private static final double SPEED = 25f; // TODO: use random speed

  private Vector2d current_move_dir;

  public Skeleton() {
    super(Mob.Tier.Humble, SPEED);
    this.health_points = rand.nextDouble(MAX_HP_LOW, MAX_HP_HIGH + 1);
    this.pos = new Vector2d();
    setSize(WIDTH, HEIGHT);
    generateRandomPos();
    generateImage();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }
  
  public Skeleton(Vector2d pos) {
    super(Mob.Tier.Humble, SPEED);
    this.pos = pos;
    this.health_points = rand.nextDouble(MAX_HP_LOW, MAX_HP_HIGH + 1);
    setSize(WIDTH, HEIGHT);
    generateImage();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }

  @Override
  public final void update(double delta) {
    attack_timer += delta;
    if (rand.nextInt(10000) < 2000) {
      if (rand.nextInt(10000) < 3000) {
        Vector2d del = Utility.getRandomDirectionVector(rand);
        current_move_dir = Vector2d.sum(current_move_dir,
        Vector2d.mul(del, 0.25));
        current_move_dir.normalize();
      }

      Vector2d move = Vector2d.mul(current_move_dir, delta * speed);
       
      pos.x += move.x;
      pos.y += move.y;

      if (pos.x < start_border.x) {
        pos.x = start_border.x;
        if (rand.nextInt(10000) < 3000) current_move_dir.x = -current_move_dir.x * rand.nextDouble(speed * delta);
        current_move_dir.normalize();
      }
      else if (pos.x > end_border.x) {
        pos.x = end_border.x;
        if (rand.nextInt(10000) < 3000) current_move_dir.x = -current_move_dir.x * rand.nextDouble(speed * delta);
        current_move_dir.normalize();
      }

      if (pos.y < start_border.y) {
        pos.y = start_border.y;
        if (rand.nextInt(10000) < 3000) current_move_dir.y = -current_move_dir.y * rand.nextDouble(speed * delta);
        current_move_dir.normalize();
      }
      else if (pos.y > end_border.y) {
        pos.y = end_border.y;
        if (rand.nextInt(10000) < 3000) current_move_dir.y = -current_move_dir.y * rand.nextDouble(speed * delta);
        current_move_dir.normalize();
      }

      if (Utility.getTranslateDirection(move.x, move.y) != Direction.Undefined) dir = Utility.getTranslateDirection(move.x, move.y);
    }

    if (rand.nextInt(10000) < 1000) {
      attack();
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