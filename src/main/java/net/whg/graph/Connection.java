package net.whg.graph;

import java.util.UUID;

/**
 * Represents a connection showing how data should move from one node to another
 * node within a graph. The connection is represented as moving from an output
 * argument of node A to an input argument of node B.
 */
public class Connection {
    private final Node nodeA;
    private final Node nodeB;
    private final int aIndex;
    private final int bIndex;
    private final UUID uuid;

    /**
     * Creates a new Connection instance between two nodes and their respective
     * input and output argument indices.
     * 
     * @param nodeA  - The node that the connection is moving out of.
     * @param aIndex - The output argument index of node A.
     * @param nodeB  - The node that the connection is moving in to.
     * @param bIndex - The input argument index of node B.
     */
    public Connection(Node nodeA, int aIndex, Node nodeB, int bIndex) {
        this.nodeA = nodeA;
        this.nodeB = nodeB;
        this.aIndex = aIndex;
        this.bIndex = bIndex;

        uuid = UUID.randomUUID();
    }

    /**
     * Gets the node this connection is coming out of.
     * 
     * @return The node.
     */
    public Node nodeA() {
        return nodeA;
    }

    /**
     * Gets the node this connection is going in to.
     * 
     * @return The node.
     */
    public Node nodeB() {
        return nodeB;
    }

    /**
     * Gets the output argument index of the node this connection is coming out of.
     * 
     * @return The argument index.
     */
    public int aIndex() {
        return aIndex;
    }

    /**
     * Gets the input argument index of the node this connection is going in to.
     * 
     * @return The argument index.
     */
    public int bIndex() {
        return bIndex;
    }

    /**
     * Gets the UUID of this connection instance.
     * 
     * @return The UUID.
     */
    public UUID uuid() {
        return uuid;
    }
}
