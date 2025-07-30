package com.deepdungeons.game;

import java.util.Dictionary;
import java.util.Hashtable;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter implements InputProcessor {
  private SpriteBatch batch;
  private FitViewport viewport;
  private Texture image;

  private Dictionary<Integer, Room> rooms;

  private int current_room_id;

  @Override
  public void create() {
    batch = new SpriteBatch();
    viewport = new FitViewport(50, 50);

    Gdx.input.setInputProcessor(this);

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
  }

  @Override
  public boolean mouseMoved (int screenX, int screenY) {
		return false;
	}

	@Override
  public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
  public boolean touchDragged (int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
  public boolean touchUp (int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
  public boolean keyDown (int keycode) {
		return false;
	}

	@Override
  public boolean keyUp (int keycode) {
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

	@Override
  public boolean keyTyped (char character) {
		return false;
	}

	@Override
  public boolean scrolled (float x, float y) {
		return false;
	}

  @Override
  public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
    return false;
  }
}