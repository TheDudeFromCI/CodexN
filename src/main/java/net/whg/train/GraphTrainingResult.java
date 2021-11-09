package net.whg.train;

import net.whg.graph.Graph;

/**
 * Contains a generates graph and it's calculated heuristic value.
 */
public record GraphTrainingResult(Graph graph, float estimatedHeuristic, float randomHeuristic, int parentIndex)
        implements Comparable<GraphTrainingResult> {
    @Override
    public int compareTo(GraphTrainingResult o) {
        return Float.compare(o.estimatedHeuristic, estimatedHeuristic + randomHeuristic);
    }
}
