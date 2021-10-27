package net.whg.impl.heuristics;

import net.whg.graph.Graph;
import net.whg.solver.Heuristic;

/**
 * A very simple heuristic that returns a heuristic based off the number of
 * connections within a graph multiplied by a provided weight per connection.
 */
public class ConnectionCountHeuristic implements Heuristic {
    private final float weight;

    /**
     * Creates a new ConnectionWeightHeuristic.
     * 
     * @param weight - The weight to multiply each connection by. Positive values
     *               will bias towards deeper search trees while negative values
     *               will bias towards wider search trees.
     */
    public ConnectionCountHeuristic(float weight) {
        this.weight = weight;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getHeuristic(Graph graph) {
        return graph.getAllConnections().size() * weight;
    }
}
