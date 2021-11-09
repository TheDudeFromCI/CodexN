package net.whg.solver;

/**
 * Creates and maintains a list of worker threads for navigating the search
 * tree.
 */
public class Solver {
    private final Thread[] threads;
    private final Worker[] workers;

    /**
     * Creates and starts a list of new worker daemon threads.
     * 
     * @param tree     - The search tree to navigate.
     * @param nWorkers - The number of workers to create.
     */
    public Solver(Tree tree, int nWorkers) {
        threads = new Thread[nWorkers];
        workers = new Worker[nWorkers];
        for (var i = 0; i < nWorkers; i++) {
            workers[i] = new Worker(tree);
            threads[i] = new Thread(workers[i]);
            threads[i].setDaemon(true);
            threads[i].start();
        }
    }

    /**
     * Triggers all worker threads to stop and waits for them to finish executing.
     * 
     * @throws InterruptedException If the thread is interrupted while waiting for
     *                              the threads to stop.
     */
    public void stop() throws InterruptedException {
        for (var i = 0; i < workers.length; i++) {
            workers[i].stop();
            threads[i].interrupt();
        }

        for (var i = 0; i < threads.length; i++) {
            threads[i].join();
        }
    }
}
