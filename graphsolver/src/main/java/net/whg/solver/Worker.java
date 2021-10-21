package net.whg.solver;

import java.util.ArrayList;
import java.util.List;

import net.whg.graph.Graph;

/**
 * A worker runnable that will pull the next available graph out of a tree,
 * process all children, and put those children back into the tree each
 * iteration. When running, this action is repeated endlessly until stopped.
 */
public class Worker implements Runnable {
    private final List<Graph> buffer = new ArrayList<>();
    private final Tree tree;
    private boolean running;

    public Worker(Tree tree) {
        this.tree = tree;
    }

    /**
     * Pulls the next graph out of the tree and processes all child graphs. This
     * method blocks until a graph becomes available within the tree.
     * 
     * @throws InterruptedException If the thread is interrupted while waiting for
     *                              the next graph to become available.
     */
    public void step() throws InterruptedException {
        var env = tree.getEnvironment();

        var next = tree.nextGraph();
        next.getChildGraphs(buffer, env);

        for (var graph : buffer) {
            if (!env.isValid(graph))
                continue;

            var h = env.getHeuristic(graph);

            if (graph.isComplete())
                tree.putSolution(graph, h);
            else
                tree.putGraph(graph, h);
        }

        buffer.clear();
    }

    /**
     * Will begin running the {@link #step()} function repeatedly until this object
     * is stopped.
     */
    @Override
    public void run() {
        running = true;
        while (running) {
            try {
                step();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Will cause the thread to stop looping after it finishes it's next graph. This
     * function does not interrupt the thread.
     */
    public void stop() {
        running = false;
    }
}
