package com.deepdungeons.game.room;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DoorConfiguration {
    private Vector2 centerPosition;
    private boolean isVertical;
    private boolean isRight;
}
