package com.deepdungeons.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.deepdungeons.game.renderer.BaseRenderer;
import com.deepdungeons.game.renderer.Renderer;

public class Game {
  
  private final BaseRenderer renderer;

  public Game(int width, int height) {
    renderer = new Renderer(new SpriteBatch(), width, height);
  }

  public void input() {
    
  }

  public void logic() {

  }

  public void draw() {
    renderer.render();
  }

  public void resize(int width, int height) {
    renderer.resize(width, height);
  }

  public void dispose() {
    renderer.dispose();
  }
}
