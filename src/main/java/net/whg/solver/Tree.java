package net.whg.solver;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import net.whg.graph.Graph;

/**
 * Mains a set of pending graphs and solutions that represent the current
 * position within the node graph search tree. Note that the environment should
 * not be changed at any time after the tree is created, otherwise the search
 * space might be contaminated.
 */
public class Tree {
    private final PriorityBlockingQueue<GraphResult> open = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<GraphResult> solutions = new PriorityBlockingQueue<>();
    private final Environment env;
    private final AtomicInteger numGraphsProcessed = new AtomicInteger(0);
    private final AtomicInteger numSolutionsFound = new AtomicInteger(0);

    /**
     * Creates and initializes a new node graph search tree object.
     * 
     * @param graphName - The name of the graph.
     * @param env       - The environment to create new graphs from.
     */
    public Tree(String graphName, Environment env) {
        this.env = env;

        var baseGraph = new Graph(graphName, env.getInputNodeType(), env.getOutputNodeType());
        open.add(new GraphResult(baseGraph, 0));
    }

    /**
     * Pulls the next open graph out of this tree. This method blocks until a graph
     * becomes available.
     * 
     * @return The next open graph.
     * @throws InterruptedException If the thread is interrupted while waiting for a
     *                              graph to become available.
     */
    public Graph nextGraph() throws InterruptedException {
        var graph = open.take().graph();
        numGraphsProcessed.incrementAndGet();
        return graph;
    }

    /**
     * Pulls the next discovered solution out of this tree. This method blocks until
     * a solution becomes available. If multiple solutions have been found, the
     * solution with the highest fitness is returned.
     * 
     * @return The next discovered solution.
     * @throws InterruptedException If the thread is interrupted while waiting for a
     *                              solution to become available.
     */
    public GraphResult nextSolution() throws InterruptedException {
        return solutions.take();
    }

    /**
     * Puts an open, valid graph node back into this tree to be processed later.
     * 
     * @param graph     - The graph to place.
     * @param heuristic - The heuristic value of the graph.
     */
    void putGraph(Graph graph, float heuristic) {
        open.put(new GraphResult(graph, heuristic));
    }

    /**
     * Puts a discovered solution back into this tree that can be processed later.
     * 
     * @param graph   - The solution graph.
     * @param fitness - The fitness score of the solution.
     */
    void putSolution(Graph graph, float fitness) {
        solutions.put(new GraphResult(graph, fitness));
        numSolutionsFound.incrementAndGet();
    }

    /**
     * Gets the environment this search tree is located within.
     * 
     * @return The environment.
     */
    public Environment getEnvironment() {
        return env;
    }

    /**
     * Gets the total number of processed graphs across all worker threads.
     * 
     * @return The number of processed graphs.
     */
    public int getNumGraphsProcessed() {
        return numGraphsProcessed.get();
    }

    /**
     * Gets the total number of solutions found across all worker threads.
     * 
     * @return The number of solutions found.
     */
    public int getNumSolutionsFound() {
        return numSolutionsFound.get();
    }

    /**
     * Gets the current number of open graphs within this tree.
     * 
     * @return The number of unprocessed graphs.
     */
    public int getNumOpenGraphs() {
        return open.size();
    }

    /**
     * Gets the best solution (as defined by having the highest fitness score)
     * currently in this tree, but does not remove it from this tree. This method is
     * non-blocking.
     * 
     * @return The solution with the highest fitness score, or null if there are no
     *         currently discovered solutions.
     */
    public GraphResult peekBestSolution() {
        return solutions.peek();
    }
}
