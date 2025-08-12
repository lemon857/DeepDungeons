package com.deepdungeons.game.mobs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.deepdungeons.game.utils.Direction;
import com.deepdungeons.game.utils.LootTable;
import com.deepdungeons.game.utils.Utility;
import com.deepdungeons.game.utils.Vector2d;

public class DefaultMob extends Mob {

  private Vector2d current_move_dir;

  public DefaultMob(String path_to_texture, double speed, double min_hp, double max_hp, LootTable table) {
    super(Mob.Tier.Humble, speed, table);
    this.health_points = rand.nextDouble(min_hp, max_hp + 1);
    this.pos = new Vector2d();
    this.image = new Pixmap(Gdx.files.internal(path_to_texture));
    this.texture = new Texture(image);
    setSize(image.getWidth() / 3, image.getHeight() / 3);
    generateRandomPos();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }

  public DefaultMob(Pixmap map, double speed, double min_hp, double max_hp, LootTable table) {
    super(Mob.Tier.Humble, speed, table);
    this.health_points = rand.nextDouble(min_hp, max_hp + 1);
    this.pos = new Vector2d();
    this.image = new Pixmap(map.getWidth(), map.getHeight(), map.getFormat());
    this.image.drawPixmap(map, 0, 0);
    this.texture = new Texture(image);
    setSize(image.getWidth() / 3, image.getHeight() / 3);
    generateRandomPos();
    current_move_dir = Utility.getRandomDirectionVector(rand);
  }

  @Override
  public final void update(double delta) {
    attack_timer += delta;

    if (attack_anim_timer >= attack_anim_time && attack_anim_play) {
      size.y /= 0.9;
      attack_anim_play = false;
    } else if (attack_anim_play) {
      attack_anim_timer += delta;
    }

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
      if (attack()) {
        startAttackAnim();
      }
    }
  }

  @Override
  public Mob clone() {
    DefaultMob mob = new DefaultMob(image, speed, health_points, health_points, getTable());
    
    mob.health_points = this.health_points;
    mob.cooldown = this.cooldown;
    mob.attack_timer = this.attack_timer;
    mob.dir = this.dir;

    return mob;
  }
}