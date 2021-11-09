package net.whg.graph;

/**
 * A function that takes in a set of inputs, preforms a calculation based on
 * that information, and produces an output.
 */
public interface Executor {
    /**
     * A function that takes in a set of inputs, preforms a calculation based on
     * that information, and produces an output. This function should always product
     * the same output for the same inputs, and should be thread safe as this can be
     * called from any thread. All objects in input and outputs should be immutable.
     * 
     * @param inputs  - The set of input arguments for this execution.
     * @param outputs - The set of output arguments for this execution.
     */
    void execute(Object[] inputs, Object[] outputs);
}
