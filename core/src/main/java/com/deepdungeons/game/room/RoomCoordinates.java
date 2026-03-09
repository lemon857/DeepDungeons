package com.deepdungeons.game.room;

import java.util.Objects;

public class RoomCoordinates {
    public int x;
    public int y;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        RoomCoordinates that = (RoomCoordinates)o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
