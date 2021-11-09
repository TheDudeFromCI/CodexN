package net.whg.solver;

import net.whg.graph.Graph;

/**
 * A heuristic calculation factor that is taken into consideration when
 * searching for a matching graph solution.
 */
public interface Heuristic {
    /**
     * Calculates a heuristic estimate for the provided graph. The provided graph
     * may not be completed. This function is may be called from any thread.
     * 
     * @param graph - The graph to handle.
     * @return The heuristic estimate.
     */
    float getHeuristic(Graph graph);
}
