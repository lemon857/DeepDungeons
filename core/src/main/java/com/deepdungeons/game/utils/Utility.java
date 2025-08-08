package com.deepdungeons.game.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Utility {

  public static Pixmap replacePixelsColor(Pixmap map, Color src_color, Color new_color) {
    for (int i = 0; i < map.getWidth(); ++i) {
      for (int j = 0; j < map.getHeight(); ++j) {
        if (isEqualColorsWithoutAlpha(map.getPixel(i, j), src_color)) {
          map.setColor(new_color);
          map.drawPixel(i, j);
        }
      }
    }
    return map;
  }

  public static boolean isEqualColorsWithoutAlpha(int color1, Color color2) {
    Color color = new Color(color1);
    return color.r == color2.r && color.g == color2.g && color.b == color2.b;
  }
  public static String getRGBAString(int color1) {
    Color color = new Color(color1);
    return "R: " + color.r + " G: " + color.g + " B: " + color.b + " A: " + color.a;
  }
}