package com.deepdungeons.game.room;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.renderer.Drawable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class Room implements Drawable {
    @Getter
    private final RoomWall wall;
    @Getter
    private final List<RoomDoor> doors = new ArrayList<>();

    @Override
    public void draw(SpriteBatch batch) {
        wall.draw(batch);
    }

    @Override
    public void setActive(boolean value) {
       wall.setActive(value);
    }

    @Override
    public boolean getActive() {
        return wall.getActive();
    }
}
