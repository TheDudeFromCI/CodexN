package net.whg.solver;

import net.whg.graph.Graph;

/**
 * Contains a generates graph and it's calculated heuristic value.
 */
public record GraphResult(Graph graph, float heuristic) implements Comparable<GraphResult> {
    @Override
    public int compareTo(GraphResult o) {
        return Float.compare(heuristic, o.heuristic);
    }
}
