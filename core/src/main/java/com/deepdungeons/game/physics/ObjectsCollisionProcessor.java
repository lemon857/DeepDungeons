package com.deepdungeons.game.physics;

@FunctionalInterface
public interface ObjectsCollisionProcessor {
    void process(Object a, Object b);
}
