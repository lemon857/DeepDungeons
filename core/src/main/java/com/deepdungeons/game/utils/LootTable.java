package com.deepdungeons.game.utils;

import java.util.Random;

import com.deepdungeons.game.items.Item;

public class LootTable {
  private final String[][] table;
  private final double[] weights;
  private final Random rand;

  public LootTable(String[][] table, double[] weights) {
    this.rand = new Random();
    this.table = table;
    this.weights = weights;

    double res = 0;
    for (double i : weights) {
      res += i;
    }

    if (res != 1) {
      System.err.println("[LootTable] Invalid weights");
    }
  }

  public Item nextItem() {
    int cur = Utility.getRandomWeightedNumber(rand, weights);

    if (table[cur].length == 0) {
      System.out.println("[LootTable] Null");
      return null; 
    }

    int item = rand.nextInt(table[cur].length);
    
    System.out.printf("[LootTable] Cur: %d Item: %d Name: %s\n", cur, item, table[cur][item]);

    return Item.getStaticItem(table[cur][item]);
  }
}