package net.whg.impl.heuristics;

import net.whg.graph.Graph;
import net.whg.solver.Heuristic;

public class MinConnectionsHeuristic implements Heuristic {
    @Override
    public float getHeuristic(Graph graph) {
        return -graph.getAllConnections().size();
    }
}
