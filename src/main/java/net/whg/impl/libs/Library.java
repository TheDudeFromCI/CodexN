package net.whg.impl.libs;

import net.whg.graph.DataType;
import net.whg.graph.Environment;
import net.whg.graph.Executor;
import net.whg.graph.NodeType;

/**
 * A collection of existing node types, data types, and axioms that should be
 * loaded together within an environment.
 */
public abstract class Library {
    /**
     * A utility function for quickly creating an array of data types for input or
     * output arguments. This function is purely for syntax sugar.
     * 
     * @param args - The ordered set of arguments.
     * @return The data type array.
     */
    protected DataType[] d(DataType... args) {
        return args;
    }

    /**
     * A utility function for quickly creating a new node type. This function is
     * purely for syntax sugar.
     * 
     * @param name    - The name of the node type.
     * @param exec    - The executor function.
     * @param inputs  - The input argument types.
     * @param outputs - The output argument types.
     * @return The new node type.
     */
    protected NodeType n(String name, Executor exec, DataType[] inputs, DataType[] outputs) {
        return new NodeType(name, exec, inputs, outputs);
    }

    /**
     * Loads all contents of this library into the provided environment.
     * 
     * @param env - The environment to load everything into.
     */
    public abstract void register(Environment env);
}
