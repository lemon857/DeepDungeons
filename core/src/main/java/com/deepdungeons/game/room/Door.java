package com.deepdungeons.game.room;

import com.deepdungeons.game.characters.Player;

public interface Door {
    void comeIn(Player player);
    void comeOut(Player player);
    void setDoorPair(Door other);
}
