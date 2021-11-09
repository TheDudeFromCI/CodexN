package net.whg.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.whg.util.SafeArrayList;

public class Graph {
    private final String name;
    private final SafeArrayList<Connection> connections;
    private final SafeArrayList<Node> nodes;
    private boolean complete;

    public Graph(String name, NodeType inputNodeType, NodeType outputNodeType) {
        if (!outputNodeType.isOutputType())
            throw new IllegalArgumentException("The provided node type is not an output node!");

        this.name = name;
        connections = new SafeArrayList<Connection>(0);
        nodes = new SafeArrayList<Node>(2);

        nodes.add(new Node(outputNodeType));
        nodes.add(new Node(inputNodeType));
    }

    public Graph(Graph parent) {
        this.name = parent.name;
        connections = new SafeArrayList<Connection>(parent.connections.size() + 1);
        nodes = new SafeArrayList<Node>(parent.nodes.size() + 1);

        for (var node : parent.nodes)
            nodes.add(new Node(node.type()));

        for (var connection : parent.connections) {
            var nodeA = nodes.get(parent.nodes.indexOf(connection.nodeA()));
            var nodeB = nodes.get(parent.nodes.indexOf(connection.nodeB()));
            var aIndex = connection.aIndex();
            var bIndex = connection.bIndex();

            connections.add(new Connection(nodeA, aIndex, nodeB, bIndex));
        }
    }

    public Node getOutputNode() {
        return nodes.get(0);
    }

    public Node getInputNode() {
        return nodes.get(1);
    }

    public List<Node> getAllNodes() {
        return nodes.asReadOnly();
    }

    public List<Connection> getAllConnections() {
        return connections.asReadOnly();
    }

    public void getChildGraphs(List<Graph> graphs, Environment env) {
        var node = nextOpenNode();
        var index = nextOpenIndex(node);

        if (node == null)
            return;

        addConnectionsToExistingNodes(node, index, graphs);
        addConnectionsToNewNodes(node, index, graphs, env);
    }

    private Node nextOpenNode() {
        for (var i = nodes.size() - 1; i >= 0; i--) {
            var node = nodes.get(i);
            if (nextOpenIndex(node) > -1)
                return node;
        }

        return null;
    }

    private int nextOpenIndex(Node node) {
        if (node == null)
            return -1;

        if (node.type().isInputType())
            return -1;

        for (var i = connections.size() - 1; i >= 0; i--) {
            var connection = connections.get(i);
            if (connection.nodeB() != node)
                continue;

            var index = connection.bIndex() + 1;
            if (index < node.type().getInputCount())
                return index;
            else
                return -1;
        }

        return 0;
    }

    private boolean isChildOf(Node a, Node b, List<Node> buffer) {
        buffer.clear();
        buffer.add(a);

        for (var connection : connections) {
            if (buffer.contains(connection.nodeB()) && !buffer.contains(connection.nodeA()))
                buffer.add(connection.nodeA());
        }

        return buffer.contains(b);
    }

    private void addConnectionsToExistingNodes(Node node, int index, List<Graph> graphs) {
        var buffer = new ArrayList<Node>();

        for (var other : nodes) {
            if (isChildOf(other, node, buffer))
                continue;

            addConnectionsToNode(node, index, other, graphs);
        }
    }

    private void addConnectionsToNode(Node node, int index, Node parent, List<Graph> graphs) {
        var inputType = node.type().getInput(index);

        var outputCount = parent.type().getOutputCount();
        for (var outputIndex = 0; outputIndex < outputCount; outputIndex++) {
            var outputType = parent.type().getOutput(outputIndex);

            if (!outputType.isInstanceOf(inputType))
                continue;

            var newGraph = new Graph(this);
            var oldNode = newGraph.nodes.get(nodes.indexOf(node));
            var oldParent = newGraph.nodes.get(nodes.indexOf(parent));

            var connection = new Connection(oldParent, outputIndex, oldNode, index);
            newGraph.connections.add(connection);
            newGraph.complete = newGraph.nextOpenNode() == null;

            graphs.add(newGraph);
        }
    }

    private void addConnectionsToNewNodes(Node node, int index, List<Graph> graphs, Environment env) {
        var inputType = node.type().getInput(index);

        for (var nodeType : env.getNodeTypes()) {
            if (nodeType.isInputType() || nodeType.isOutputType())
                continue;

            var outputCount = nodeType.getOutputCount();
            for (var outputIndex = 0; outputIndex < outputCount; outputIndex++) {
                var outputType = nodeType.getOutput(outputIndex);

                if (!outputType.isInstanceOf(inputType))
                    continue;

                var newGraph = new Graph(this);
                var oldNode = newGraph.nodes.get(nodes.indexOf(node));

                var newNode = new Node(nodeType);
                var connection = new Connection(newNode, outputIndex, oldNode, index);

                newGraph.nodes.add(newNode);
                newGraph.connections.add(connection);
                newGraph.complete = newGraph.nextOpenNode() == null;

                graphs.add(newGraph);
            }
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new GraphRenderer(this).toString();
    }

    public boolean isComplete() {
        return complete;
    }

    public void execute(Object[] inputs, Object[] outputs) {
        if (!complete)
            throw new IllegalStateException("Graph is not complete!");

        if (inputs.length != getInputNode().type().getOutputCount())
            throw new IllegalStateException("Unexpected number of inputs!");

        if (outputs.length != getOutputNode().type().getInputCount())
            throw new IllegalStateException("Unexpected number of outputs!");

        var cache = new HashMap<Node, Object[]>();
        cache.put(getInputNode(), inputs);

        var out = getExecutionOutput(getOutputNode(), cache);
        System.arraycopy(out, 0, outputs, 0, out.length);
    }

    private Object[] getExecutionOutput(Node node, Map<Node, Object[]> cache) {
        if (cache.containsKey(node))
            return cache.get(node);

        var inputs = new Object[node.type().getInputCount()];

        for (var connection : connections) {
            if (connection.nodeB() != node)
                continue;

            var incoming = getExecutionOutput(connection.nodeA(), cache);
            inputs[connection.bIndex()] = incoming[connection.aIndex()];
        }

        if (node.type().isOutputType()) {
            cache.put(node, inputs);
            return inputs;
        } else {
            var outputs = new Object[node.type().getOutputCount()];
            node.type().getExecutor().execute(inputs, outputs);
            cache.put(node, outputs);
            return outputs;
        }
    }
}
