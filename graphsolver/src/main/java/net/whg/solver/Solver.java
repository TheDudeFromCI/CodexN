package net.whg.solver;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import net.whg.graph.Environment;
import net.whg.graph.Graph;

public class Solver {
    private final PriorityBlockingQueue<GraphResult> open = new PriorityBlockingQueue<>();
    private final PriorityBlockingQueue<GraphResult> solutions = new PriorityBlockingQueue<>();
    private final Environment env;
    private final Thread[] threads;
    private boolean running = true;

    public Solver(String graphName, Environment env, int workers) {
        this.env = env;

        var baseGraph = new Graph(graphName, env.getInputNodeType(), env.getOutputNodeType());
        open.add(new GraphResult(baseGraph, 0));

        threads = new Thread[workers];
        for (var i = 0; i < threads.length; i++) {
            threads[i] = new Thread(createWorker());
            threads[i].setDaemon(false);
            threads[i].start();
        }
    }

    private Runnable createWorker() {
        return () -> {
            var buffer = new ArrayList<Graph>();

            while (running) {
                GraphResult next;

                try {
                    next = open.take();
                } catch (InterruptedException e) {
                    return;
                }

                buffer.clear();
                next.graph().getChildGraphs(buffer, env);

                for (var graph : buffer) {
                    if (!env.isValid(graph))
                        continue;

                    var h = env.getHeuristic(graph);
                    var result = new GraphResult(graph, h);

                    if (graph.isComplete())
                        solutions.put(result);
                    else
                        open.put(result);
                }
            }
        };
    }

    public void stop() {
        running = false;

        for (var i = 0; i < threads.length; i++) {
            threads[i].interrupt();

            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Graph getSolution() throws InterruptedException {
        return solutions.take().graph();
    }
}
