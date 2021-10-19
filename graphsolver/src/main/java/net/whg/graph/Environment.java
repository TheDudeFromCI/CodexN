package net.whg.graph;

import java.util.List;

import net.whg.solver.Axiom;
import net.whg.solver.Heuristic;
import net.whg.util.SafeArrayList;

/**
 * Contains a set of available node types and axioms that can be passed to a
 * graph when looking for child graphs.
 */
public class Environment {
    private final SafeArrayList<NodeType> nodeTypes = new SafeArrayList<>();
    private final SafeArrayList<Axiom> axioms = new SafeArrayList<>();
    private final SafeArrayList<Heuristic> heuristics = new SafeArrayList<>();
    private final NodeType inputNodeType;
    private final NodeType outputNodeType;

    /**
     * Creates a new Environment instance.
     * 
     * @param inputNodeType  - The type of node to use as a graph input node.
     * @param outputNodeType - The type of node to use as a graph output node.
     */
    public Environment(NodeType inputNodeType, NodeType outputNodeType) {
        this.inputNodeType = inputNodeType;
        this.outputNodeType = outputNodeType;
    }

    /**
     * Adds a new node type to this environment.
     * 
     * @param nodeType - The node type to add.
     */
    public void addNodeType(NodeType nodeType) {
        if (!nodeTypes.contains(nodeType))
            nodeTypes.add(nodeType);
    }

    /**
     * Gets a read-only list of all currently available node types.
     * 
     * @return The list of all node types.
     */
    public List<NodeType> getNodeTypes() {
        return nodeTypes.asReadOnly();
    }

    /**
     * Gets the type of node to use as a graph input node.
     * 
     * @return The input node type.
     */
    public NodeType getInputNodeType() {
        return inputNodeType;
    }

    /**
     * Gets the type of node to use as a graph output node.
     * 
     * @return The output node type.
     */
    public NodeType getOutputNodeType() {
        return outputNodeType;
    }

    /**
     * Adds a new axiom to this environment.
     * 
     * @param axiom - The axiom to add.
     */
    public void addAxiom(Axiom axiom) {
        axioms.add(axiom);
    }

    /**
     * Gets a read-only list of all axioms in this environment.
     * 
     * @return A list of all axioms.
     */
    public List<Axiom> getAxioms() {
        return axioms.asReadOnly();
    }

    /**
     * Adds a new heuristic to this environment.
     * 
     * @param heuristic - heuristic axiom to add.
     */
    public void addHeuristic(Heuristic heuristic) {
        heuristics.add(heuristic);
    }

    /**
     * Gets a read-only list of all heuristics in this environment.
     * 
     * @return A list of all heuristics.
     */
    public List<Heuristic> getHeuristics() {
        return heuristics.asReadOnly();
    }

    public boolean isValid(Graph graph) {
        for (var axiom : axioms) {
            if (!axiom.verifyGraph(graph))
                return false;
        }

        return true;
    }

    public float getHeuristic(Graph graph) {
        float h = 0;

        for (var heuristic : heuristics) {
            h += heuristic.getHeuristic(graph);
        }

        return h;
    }
}
