package net.whg.train;

import java.util.List;

import net.whg.graph.Graph;
import net.whg.solver.GraphResult;

/**
 * Used when training the neural network to provide heuristic results for graphs
 * and to send training data back to the neural network.
 */
public interface HeuristicProvider {
    /**
     * Sends a set of graphs to the neural network and returns a list of heuristics
     * for each corresponding graph.
     * 
     * @param problem  - The problem being solved.
     * @param solution - The current graph state.
     * @param next     - A list of potential next states.
     * @param output   - The output heuristics corresponding to each next state.
     */
    void getHeuristics(Graph problem, Graph solution, List<Graph> next, List<Float> output);

    /**
     * Sends a set of graphs to the neural network in order to train it to provide
     * the corresponding heuristics.
     * 
     * @param problem  - The problem being solved.
     * @param solution - The current graph state.
     * @param next     - A list of potential next states and their corresponding
     *                 heuristics.
     */
    void trainHeuristics(Graph problem, Graph solution, List<GraphResult> next);
}
