package com.deepdungeons.game.room;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.deepdungeons.game.generator.Graph;
import com.deepdungeons.game.renderer.Drawable;

import java.util.ArrayList;
import java.util.List;

public class RoomsManager implements Drawable {
    private final List<Room> rooms = new ArrayList<>();
    private boolean isActive = true;

    public void createRoomsFromGraph(Graph graph, World world, String pathToWallTexture, String pathToDoorTexture, RoomConfiguration configuration) {
        Vector2 startPos = new Vector2(0, 0);
        Vector2 roomSize = configuration.getRoomSize();
        Vector2 pixelDoorSize = new Vector2(100, 20);
        float wallWidth = configuration.getWallWidth();

        for (int i = 0; i < graph.getVertexCount(); i++) {
            rooms.add(new Room(new SquareRoomWall(
                    world, pathToWallTexture,
                    new Vector2(startPos.x - roomSize.x / 2.f, startPos.y - roomSize.y / 2.f),
                    new Vector2(roomSize.x, wallWidth)
            )));
            startPos.x += roomSize.x + 3 * wallWidth;
        }

        for (Graph.Edge edge : graph.getEdges()) {
            Room a = rooms.get(edge.getFrom());
            Room b = rooms.get(edge.getTo());

            RoomConfiguration.MirrorDoorConfiguration doorConfig;

            if (a.getDoors().isEmpty()) {
               doorConfig = configuration.getMirrorDoorConfiguration().get(0);
            } else {
                doorConfig = configuration.getMirrorDoorConfiguration().get(1);
            }

            Vector2 roomALeftBottom = new Vector2(a.getWall().getLeftBottom());
            Vector2 doorAPosition = roomALeftBottom.add(doorConfig.getDoor1().getCenterPosition());

            Vector2 roomBLeftBottom = new Vector2(b.getWall().getLeftBottom());
            Vector2 doorBPosition = roomBLeftBottom.add(doorConfig.getDoor2().getCenterPosition());

            RoomDoor doorA = new RoomDoor(
                    world, pathToDoorTexture,
                    doorAPosition, pixelDoorSize,
                    doorConfig.getDoor1().isVertical(), doorConfig.getDoor1().isRight());

            RoomDoor doorB = new RoomDoor(
                    world, pathToDoorTexture,
                    doorBPosition, pixelDoorSize,
                    doorConfig.getDoor2().isVertical(), doorConfig.getDoor2().isRight());

            doorA.setDoorPair(doorB);
            doorB.setDoorPair(doorA);

            a.getDoors().add(doorA);
            b.getDoors().add(doorB);
        }
    }

    @Override
    public void draw(SpriteBatch batch) {
        for (Room room : rooms) {
            room.draw(batch);
        }
    }

    @Override
    public void setActive(boolean value) {
        for (Room room : rooms) {
            room.setActive(value);
        }
        isActive = value;
    }

    @Override
    public boolean getActive() {
        return isActive;
    }
}
