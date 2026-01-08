package com.deepdungeons.game.room;

import com.deepdungeons.game.characters.Player;
import com.deepdungeons.game.physics.ObjectsCollisionProcessor;

public class PlayerDoorCollisionProcessor implements ObjectsCollisionProcessor {
    @Override
    public void process(Object a, Object b) {
        if (a instanceof Player && b instanceof Door) {
            ((Door)b).comeIn((Player)a);
        } else if (b instanceof Player && a instanceof Door) {
            ((Door)a).comeIn((Player)b);
        }
    }
}
