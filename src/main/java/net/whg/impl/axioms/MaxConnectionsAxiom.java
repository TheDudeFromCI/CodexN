package net.whg.impl.axioms;

import net.whg.graph.Graph;
import net.whg.solver.Axiom;

/**
 * A simple axiom that prevents more than an assigned number of connections to
 * exist within a graph. This useful for preventing graphs from growing too
 * large.
 */
public class MaxConnectionsAxiom implements Axiom {
    private final int maxConnections;

    /**
     * Creates a new MaxConnectionsAxiom.
     * 
     * @param maxConnections - The maximum number of allowed connections within a
     *                       graph.
     */
    public MaxConnectionsAxiom(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    @Override
    public boolean verifyGraph(Graph graph) {
        var minConnections = 0;

        for (var node : graph.getAllNodes())
            minConnections += node.type().getInputCount();

        return minConnections <= maxConnections;
    }
}
