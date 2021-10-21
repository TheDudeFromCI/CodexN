package net.whg.solver;

import net.whg.graph.Graph;

/**
 * A fitness evaluation factor that is used to judge node graph solutions.
 */
public interface FitnessEval {
    /**
     * Calculates the fitness score for the provided node graph solution. This
     * function is may be called from any thread.
     * 
     * @param graph - The graph to handle.
     * @return The fitness score.
     */
    float getFitness(Graph graph);
}
