package com.deepdungeons.game.physics;

import com.badlogic.gdx.math.Vector2;

@FunctionalInterface
public interface ObjectsCollisionProcessor {
    public void process(Object a, Object b);
}
