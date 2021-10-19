package net.whg.graph;

import net.whg.util.SafeArrayList;

public class GraphRenderer {
    private record Variable(DataType dataType, String name) {
    }

    private class FunctionCall {
        private final Node function;
        private final Variable[] inputs;
        private final Variable[] outputs;

        FunctionCall(Node function, Variable[] inputs, Variable[] outputs) {
            this.function = function;
            this.inputs = inputs;
            this.outputs = outputs;
        }

        @Override
        public String toString() {
            var sb = new StringBuilder();

            if (function.type().isInputType())
                showInputNode(sb);
            else if (function.type().isOutputType())
                showOutputNode(sb);
            else
                showHiddenNode(sb);

            return sb.toString();
        }

        private void showInputNode(StringBuilder sb) {
            var output = getOutputFunctionCall();
            listVariables(sb, output.inputs, true, false);

            sb.append(' ');
            sb.append(graph.getName()).append('(');
            listVariables(sb, outputs, true, true);
            sb.append("): ");
        }

        private void showHiddenNode(StringBuilder sb) {
            sb.append("  ");
            listVariables(sb, outputs, false, true);
            sb.append(" = ");
            sb.append(function.type().getName()).append('(');
            listVariables(sb, inputs, false, true);
            sb.append(')');
        }

        private void showOutputNode(StringBuilder sb) {
            sb.append("  return ");
            listVariables(sb, inputs, false, true);
        }

        private void listVariables(StringBuilder sb, Variable[] variables, boolean showTypes, boolean showNames) {
            for (var i = 0; i < variables.length; i++) {
                if (showNames)
                    sb.append(variables[i].name());

                if (showNames && showTypes)
                    sb.append(": ");

                if (showTypes)
                    sb.append(variables[i].dataType());

                if (i < variables.length - 1)
                    sb.append(", ");
            }
        }
    }

    private static final String VARIABLE_CONSTANTS = "abcdefghijklmnopqrstuvwxyz";

    private final SafeArrayList<FunctionCall> functionCalls = new SafeArrayList<>();
    private final Graph graph;
    private int variableIndex;

    public GraphRenderer(Graph graph) {
        this.graph = graph;

        analyze(graph.getInputNode());
        analyze(graph.getOutputNode());
    }

    private FunctionCall getOutputFunctionCall() {
        for (var func : functionCalls) {
            if (func.function == graph.getOutputNode())
                return func;
        }

        return null;
    }

    private FunctionCall analyze(Node node) {
        var inputs = new Variable[node.type().getInputCount()];
        for (var i = 0; i < inputs.length; i++)
            inputs[i] = getVariable(node, i);

        var outputs = new Variable[node.type().getOutputCount()];
        for (var i = 0; i < outputs.length; i++) {
            var type = node.type().getOutput(i);
            var name = genVariableName();
            outputs[i] = new Variable(type, name);
        }

        var func = new FunctionCall(node, inputs, outputs);
        functionCalls.add(func);

        return func;
    }

    private String genVariableName() {
        var sb = new StringBuilder();

        if (variableIndex > 0) {
            var temp = variableIndex;
            while (temp > 0) {
                sb.insert(0, VARIABLE_CONSTANTS.charAt(temp % 26));
                temp /= 26;
            }
        } else
            sb.append("a");

        variableIndex++;
        return sb.toString();
    }

    private Variable getVariable(Node node, int argumentIndex) {
        var connection = getConnection(node, argumentIndex);
        if (connection == null)
            return new Variable(node.type().getInput(argumentIndex), genVariableName());

        for (var func : functionCalls) {
            if (func.function == connection.nodeA())
                return func.outputs[connection.aIndex()];
        }

        var func = analyze(connection.nodeA());
        return func.outputs[connection.aIndex()];
    }

    private Connection getConnection(Node nodeB, int bIndex) {
        for (var connection : graph.getAllConnections()) {
            if (connection.nodeB() == nodeB && connection.bIndex() == bIndex)
                return connection;
        }

        return null;
    }

    public String asFunction() {
        var sb = new StringBuilder();

        for (var functionCall : functionCalls)
            sb.append(functionCall).append("\n");

        return sb.toString();
    }

    public String asConnectionList() {
        var sb = new StringBuilder();

        var nodes = graph.getAllNodes();
        var connections = graph.getAllConnections();

        for (var node : nodes) {
            sb.append(nodes.indexOf(node)).append(" = ");
            sb.append(node.type()).append("\n");
        }

        for (var connection : connections) {
            sb.append(nodes.indexOf(connection.nodeA())).append(": ").append(connection.aIndex());
            sb.append(" --> ");
            sb.append(nodes.indexOf(connection.nodeB())).append(": ").append(connection.bIndex());
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return asConnectionList();
    }
}
