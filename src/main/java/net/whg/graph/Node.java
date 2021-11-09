package net.whg.graph;

import java.util.UUID;

/**
 * Represents a node instance that exists on a graph. It is meant to show how
 * data should be processed as it moves forward through a graph.
 */
public class Node {
    private final NodeType type;
    private final UUID uuid;

    /**
     * Creates a new Node instance.
     * 
     * @param type - The type of node.
     */
    public Node(NodeType type) {
        this.type = type;
        this.uuid = UUID.randomUUID();
    }

    /**
     * Gets the type of function this node represents.
     * 
     * @return The node type.
     */
    public NodeType type() {
        return type;
    }

    /**
     * Gets the UUID of this node instance.
     * 
     * @return The UUID.
     */
    public UUID uuid() {
        return uuid;
    }
}
