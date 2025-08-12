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
  }

  public Item nextItem() {
    int cur = Utility.getRandomWeightedNumber(rand, weights);

    System.out.println("[LootTable] Cur: " + cur);

    if (table[cur].length == 0) return null;

    int item = rand.nextInt(table[cur].length);
    
    System.out.println("[LootTable] Item: " + item);

    return Item.getStaticItem(table[cur][item]);
  }
}