package com.deepdungeons.game.generator;

import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
public class MstGraphGenerator implements GraphGenerator{
    private final int vertexCount;

    public Graph generateGraph() {
        Graph graph = new Graph(vertexCount);

        Set<Integer> vertices = new HashSet<>();

        Random random = new Random();

        int vertexX = random.nextInt(vertexCount);
        vertices.add(vertexX);
        int vertexY = 0;
        for (int i = 0; i < vertexCount - 1; i++) {
            do {
                vertexY = random.nextInt(0, vertexCount);
            } while (vertices.contains(vertexY));
            vertices.add(vertexY);

            graph.addEdge(vertexX, vertexY);
            vertexX = vertexY;
        }

        return graph;
    }
}
