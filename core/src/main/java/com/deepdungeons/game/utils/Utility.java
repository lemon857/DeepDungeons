package com.deepdungeons.game.utils;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;

public class Utility {

  private static final double HALF_SQUARE_SUM = Math.sqrt(2)/2;
  
  public static Direction getTranslateDirection(double dx, double dy) {
    if (dx > 0) {
      if (dy > 0) {
        return Direction.UpRight;
      } else if (dy < 0) {
        return Direction.DownRight;
      } else {
        return Direction.Right;
      }
    } else if (dx < 0) {
      if (dy > 0) {
        return Direction.UpLeft;
      } else if (dy < 0) {
        return Direction.DownLeft;
      } else {
        return Direction.Left;
      }
    } else {
      if (dy > 0) {
        return Direction.Up;
      } else if (dy < 0) {
        return Direction.Down;
      }
    }
    return Direction.Undefined;
  }

  public static Direction getRandomDirection(Random rand) {
    int dir = rand.nextInt(8);
    switch (dir) {
    case 0: return Direction.Up;
    case 1: return Direction.UpRight;
    case 2: return Direction.Right;
    case 3: return Direction.DownRight;
    case 4: return Direction.Down;
    case 5: return Direction.DownLeft;
    case 6: return Direction.Left;
    case 7: return Direction.UpLeft;
    }
    return Direction.Undefined;
  }

  public static int getRandomWeightedNumber(Random rand, double[] weights) {
    double res = rand.nextDouble();
    double low = 0;
    double high = 0;
    for (int i = 0; i < weights.length; ++i) {
      low = high;
      high += weights[i];
      if (res >= low && res < high) {
        return i;
      }
    }
    return 0;
  }

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
  
  public static Pixmap replacePixelsExcludeColor(Pixmap map, Color exclude_color, Color new_color) {
    for (int i = 0; i < map.getWidth(); ++i) {
      for (int j = 0; j < map.getHeight(); ++j) {
        if (!isEqualColorsWithoutAlpha(map.getPixel(i, j), exclude_color)) {
          map.setColor(new_color);
          map.drawPixel(i, j);
        }
      }
    }
    return map;
  }

  public static Pixmap addPixelsColor(Pixmap map, Color src_color, float r, float g, float b) {
    for (int i = 0; i < map.getWidth(); ++i) {
      for (int j = 0; j < map.getHeight(); ++j) {
        if (isEqualColorsWithoutAlpha(map.getPixel(i, j), src_color)) {
          Color new_color = new Color(map.getPixel(i, j));
          new_color.r = Math.max(0, Math.min(1, new_color.r + r));
          new_color.g = Math.max(0, Math.min(1, new_color.g + g));
          new_color.b = Math.max(0, Math.min(1, new_color.b + b));
          map.setColor(new_color);
          map.drawPixel(i, j);
        }
      }
    }
    return map;
  }

  public static Pixmap addPixelsExcludeColor(Pixmap map, Color exclude_color, float r, float g, float b) {
    for (int i = 0; i < map.getWidth(); ++i) {
      for (int j = 0; j < map.getHeight(); ++j) {
        if (!isEqualColorsWithoutAlpha(map.getPixel(i, j), exclude_color)) {
          Color new_color = new Color(map.getPixel(i, j));
          new_color.r = Math.max(0, Math.min(1, new_color.r + r));
          new_color.g = Math.max(0, Math.min(1, new_color.g + g));
          new_color.b = Math.max(0, Math.min(1, new_color.b + b));
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

  public static double mapRange(double value, double start, double end, double start_res, double end_res) {
    return start_res + (((end_res - start_res) / (end - start)) * (value - start));
  }
}