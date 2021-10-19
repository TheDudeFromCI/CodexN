package net.whg.solver;

import net.whg.graph.Graph;

/**
 * A rule that must be followed in order for a graph to be considered valid. Not
 * that if an uncompleted graph is invalid, all possible child graphs are also
 * considered invalid.
 */
public interface Axiom {
    /**
     * Verifies whether or not a graph follows this axiom. The provided graph may
     * not be completed. This function is may be called from any thread.
     * 
     * @param graph - The graph to verify.
     * @return True if the graph is valid, false otherwise.
     */
    boolean verifyGraph(Graph graph);
}
