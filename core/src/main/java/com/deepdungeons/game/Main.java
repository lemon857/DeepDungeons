package com.deepdungeons.game;

import com.badlogic.gdx.ApplicationAdapter;

// ! WARNING !
// IDK why, start coords on Pixmap located at Up-Left corner,
// but start coords when draw texture located at Down-Left corner
// for minimize misunderstanding I'll correct all of Pixmap drawing
// with mirroring it
// here always 
// X from left to right
// Y from down to up
// But if load Pixmap from file all is correct,
// I fix it with crutch - flag for check loaded from file

public class Main extends ApplicationAdapter {

  private final double width_to_height_koef;

  private Game game;

  public Main(double width_to_height_koef) {
    this.width_to_height_koef = width_to_height_koef;
  }

  @Override
  public void create() {
    int width = 1400;
    game = new Game(width, (int)(width * width_to_height_koef));
  }

  @Override
  public void render() {
    game.input();
    game.logic();
    game.draw();
  }

  @Override
  public void dispose() {
    game.dispose();
  }

  @Override
  public void resize(int width, int height) {
    game.resize(width, height);
  }
}