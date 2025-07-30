package com.deepdungeons.game;

import java.util.Dictionary;
import java.util.Hashtable;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {
  private SpriteBatch batch;
  private FitViewport viewport;
  private Texture image;

  private Stage stage;
  private Label room_id_label;

  private Dictionary<Integer, Room> rooms;

  private int current_room_id;

  @Override
  public void create() {
    batch = new SpriteBatch();
    viewport = new FitViewport(50, 50);
    stage = new Stage();
    Gdx.input.setInputProcessor(stage);

    stage.addListener(new InputListener() {
      @Override
      public boolean keyUp(InputEvent event, int keycode) {
        switch (keycode) {
          case Input.Keys.UP:
            GoToNextRoom(0);  
            break;
          case Input.Keys.RIGHT:
            GoToNextRoom(1);
            break;
          case Input.Keys.DOWN:
            GoToNextRoom(2);
            break;
          case Input.Keys.LEFT:
            GoToNextRoom(3);
            break;
    
          default: return false;
        }
        return true;
      }
    });

    Skin skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

    room_id_label = new Label("1", skin);
    room_id_label.setPosition(10, 10);
    stage.addActor(room_id_label);

    rooms = new Hashtable<>();

    Room new_room = new Room(2);

    current_room_id = new_room.GetID();
    rooms.put(current_room_id, new_room);

    image = new Texture(rooms.get(current_room_id).GenerateImage());
  }

  @Override
  public void render() {
    ScreenUtils.clear(0f, 0f, 0f, 1f);
    viewport.apply();
    batch.setProjectionMatrix(viewport.getCamera().combined);
    batch.begin();
    batch.draw(image, 0, 0, 50, 50);
    batch.end();

    stage.act(Gdx.graphics.getDeltaTime());
	  stage.draw();
  }

  @Override
  public void dispose() {
    batch.dispose();
    image.dispose();
  }

  @Override
  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  private void GoToNextRoom(int door_id) {
    int next_id = rooms.get(current_room_id).GetNextRoomID(door_id);
    if (next_id == 0) {
      System.out.printf("Current id: %d\n", current_room_id);
      Room new_room = new Room(-1, (door_id + 2) % 4, current_room_id);

      rooms.get(current_room_id).SetNextRoomID(door_id, new_room.GetID());

      rooms.put(new_room.GetID(), new_room);

      current_room_id = new_room.GetID();
      image = new Texture(rooms.get(current_room_id).GenerateImage());

      System.out.printf("New Current id: %d\n", current_room_id);

    } else if (next_id != -1) {
      current_room_id = next_id;
      image = new Texture(rooms.get(current_room_id).GenerateImage());
    }
    room_id_label.setText(current_room_id);
  }
}