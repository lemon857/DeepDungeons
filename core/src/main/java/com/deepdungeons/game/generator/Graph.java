package com.deepdungeons.game.generator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class Graph {
    @Getter
    @RequiredArgsConstructor
    public class Edge {
        private final int from;
        private final int to;
    }

    private final int vertexCount;
    private final List<Edge> edges = new ArrayList<>();

    public void addEdge(int from, int to) {
        if (from >= vertexCount || to >= vertexCount) {
            throw new IllegalArgumentException("Vertex must be in graph");
        }
       edges.add(new Edge(from, to));
    }
}
