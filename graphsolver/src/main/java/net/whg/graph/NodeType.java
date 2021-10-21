package net.whg.graph;

import java.util.Arrays;

/**
 * Represents an executable node type that can be added to a graph. Node types
 * may have a variable number of inputs or outputs.
 */
public final class NodeType {
    private final String name;
    private final DataType[] inputs;
    private final DataType[] outputs;
    private final Executor executor;

    /**
     * Creates a new NodeType instance.
     * 
     * @param name     - The name of this node type.
     * @param executor - The executor for this node type.
     * @param inputs   - The array of inputs argument data types, in order.
     * @param outputs  - The array of output argument data types, in order.
     * @throws IllegalArgumentException If the number of inputs and the number of
     *                                  outputs are both equal to 0.
     */
    public NodeType(String name, Executor executor, DataType[] inputs, DataType[] outputs) {
        if (inputs.length == 0 && outputs.length == 0)
            throw new IllegalArgumentException("NodeType has not inputs or outputs!");

        this.name = name;
        this.executor = executor;
        this.inputs = Arrays.copyOf(inputs, inputs.length);
        this.outputs = Arrays.copyOf(outputs, outputs.length);
    }

    /**
     * Checks whether or not this node type is an input node type.
     * 
     * @return True if this node type has no input arguments, only outputs.
     */
    public boolean isInputType() {
        return inputs.length == 0;
    }

    /**
     * Checks whether or not this node type is an output node type.
     * 
     * @return True if this node type has no outputs arguments, only inputs.
     */
    public boolean isOutputType() {
        return outputs.length == 0;
    }

    /**
     * Gets the name of this node type.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the data type of the specified input index.
     * 
     * @param index - The input index.
     * @return The corresponding data type.
     */
    public DataType getInput(int index) {
        return inputs[index];
    }

    /**
     * Gets the data type of the specified output index.
     * 
     * @param index - The output index.
     * @return The corresponding data type.
     */
    public DataType getOutput(int index) {
        return outputs[index];
    }

    /**
     * Gets the number of input arguments for this node type.
     * 
     * @return The number of inputs.
     */
    public int getInputCount() {
        return inputs.length;
    }

    /**
     * Gets the number of output arguments for this node type.
     * 
     * @return The number of outputs.
     */
    public int getOutputCount() {
        return outputs.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override

    public String toString() {
        var sb = new StringBuilder();

        if (outputs.length > 0) {
            for (var i = 0; i < outputs.length; i++) {
                sb.append(outputs[i].getName());

                if (i == outputs.length - 1)
                    sb.append(" ");
                else
                    sb.append(", ");
            }
        } else {
            sb.append("void ");
        }

        sb.append(getName());
        sb.append("(");

        for (var i = 0; i < inputs.length; i++) {
            sb.append(inputs[i].getName());

            if (i < inputs.length - 1)
                sb.append(", ");
        }

        sb.append(")");

        return sb.toString();
    }

    /**
     * Gets the executor for this node type.
     * 
     * @return The executor.
     */
    public Executor getExecutor() {
        return executor;
    }
}
