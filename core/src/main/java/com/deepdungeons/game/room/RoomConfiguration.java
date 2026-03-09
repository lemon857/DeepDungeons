package com.deepdungeons.game.room;

import com.badlogic.gdx.math.Vector2;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class RoomConfiguration {
    @Getter
    @AllArgsConstructor
    public class MirrorDoorConfiguration {
        private final DoorConfiguration door1;
        private final DoorConfiguration door2;
    }

    private final Vector2 roomSize;
    private final float wallWidth;
    private final List<MirrorDoorConfiguration> mirrorDoorConfiguration = new ArrayList<>();

    public void addMirrorDoorConfiguration(DoorConfiguration configuration1, DoorConfiguration configuration2) {
        mirrorDoorConfiguration.add(new MirrorDoorConfiguration(configuration1, configuration2));
    }
}
