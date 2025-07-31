package com.deepdungeons;

import com.badlogic.gdx.graphics.Color;

public class Key {
  private int key;
  private Color color;

  public Key(int key, Color color) {
    this.key = key;
    this.color = color;
  }

  public int getKey() {
    return key;
  }
}
