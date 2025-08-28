package com.deepdungeons.game.mobs;

import com.badlogic.gdx.graphics.Pixmap;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.LootTable;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class DefaultEnemy extends Mob {

  private Vector2d current_move_dir;

  private static final double SIZE_KOEF = 0.4;

  public DefaultEnemy(String path_to_texture, double move_speed, double attack_speed, double strength, double min_hp, double max_hp, LootTable table) {
    super(path_to_texture, Mob.Tier.Humble, move_speed, attack_speed, strength, table);
    this.health_points.v = rand.nextDouble(min_hp, max_hp + 1);
    this.pos = new Vector2d();

    updateSize(SIZE_KOEF);
    generateRandomPos();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }

  public DefaultEnemy(Pixmap map, double move_speed, double attack_speed, double strength, double min_hp, double max_hp, LootTable table) {
    super(map, Mob.Tier.Humble, move_speed, attack_speed, strength, table);
    this.health_points.v = rand.nextDouble(min_hp, max_hp + 1);
    this.pos = new Vector2d();

    updateSize(SIZE_KOEF);
    generateRandomPos();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }

  @Override
  public final void update(double delta) {
    updateTimers(delta);

    if (rand.nextInt(10000) < 2000) {
      if (rand.nextInt(10000) < 3000) {
        Vector2d del = Utility.getRandomDirectionVector(rand);
        current_move_dir = Vector2d.sum(current_move_dir,
        Vector2d.mul(del, 0.25));
        current_move_dir.normalize();
      }

      Vector2d move = Vector2d.mul(current_move_dir, delta * getMoveSpeed());
       
      pos.x += move.x;
      pos.y += move.y;

      if (pos.x < start_border.x) {
        pos.x = start_border.x;
        if (rand.nextInt(10000) < 3000) current_move_dir.x = -current_move_dir.x * rand.nextDouble(getMoveSpeed() * delta);
        current_move_dir.normalize();
      }
      else if (pos.x > end_border.x) {
        pos.x = end_border.x;
        if (rand.nextInt(10000) < 3000) current_move_dir.x = -current_move_dir.x * rand.nextDouble(getMoveSpeed() * delta);
        current_move_dir.normalize();
      }

      if (pos.y < start_border.y) {
        pos.y = start_border.y;
        if (rand.nextInt(10000) < 3000) current_move_dir.y = -current_move_dir.y * rand.nextDouble(getMoveSpeed() * delta);
        current_move_dir.normalize();
      }
      else if (pos.y > end_border.y) {
        pos.y = end_border.y;
        if (rand.nextInt(10000) < 3000) current_move_dir.y = -current_move_dir.y * rand.nextDouble(getMoveSpeed() * delta);
        current_move_dir.normalize();
      }

      if (Utility.getTranslateDirection(move.x, move.y) != Direction.Undefined) {
        updateSpritePos();
        dir = Utility.getTranslateDirection(move.x, move.y);
      }
    }

    if (rand.nextInt(10000) < 1000) {
      if (attack()) {
        startAttackAnim();
      }
    }
  }

  @Override
  public Mob clone() {
    DefaultEnemy mob = new DefaultEnemy(image, getMoveSpeed(), getAttackSpeed(), getStrength(), health_points.v, health_points.v, getTable());

    return mob;
  }
}