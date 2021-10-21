package net.whg.graph;

import java.util.List;

import net.whg.impl.libs.Library;
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
    private final SafeArrayList<DataType> dataTypes = new SafeArrayList<>();
    private final SafeArrayList<Library> libraries = new SafeArrayList<>();

    /**
     * Gets the node type in this environment that is marked as an input type. If
     * there are several loaded input node types, only the first one is returned.
     * 
     * @return The input node type, or null if there is no input node type.
     */
    public NodeType getInputNodeType() {
        for (var nodeType : nodeTypes)
            if (nodeType.isInputType())
                return nodeType;

        return null;
    }

    /**
     * Gets the node type in this environment that is marked as an output type. If
     * there are several loaded output node types, only the first one is returned.
     * 
     * @return The output node type, or null if there is no output node type.
     */
    public NodeType getOutputNodeType() {
        for (var nodeType : nodeTypes)
            if (nodeType.isOutputType())
                return nodeType;

        return null;
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

    /**
     * Checks if the provided graph passes all axioms in this environment.
     * 
     * @param graph - The graph to test.
     * @return True if the graph passes all axioms. False otherwise.
     */
    public boolean isValid(Graph graph) {
        for (var axiom : axioms) {
            if (!axiom.verifyGraph(graph))
                return false;
        }

        return true;
    }

    /**
     * Gets the sum of all heuristic values calculated for this graph.
     * 
     * @param graph - The graph to get the heuristic of.
     * @return The heuristic value.
     */
    public float getHeuristic(Graph graph) {
        float h = 0;

        for (var heuristic : heuristics) {
            h += heuristic.getHeuristic(graph);
        }

        return h;
    }

    /**
     * Gets or creates a data type in this environment with the given name.
     * 
     * @param name - The name of the data type.
     * @return The corresponding data type.
     */
    public DataType getDataType(String name) {
        for (var dataType : dataTypes)
            if (dataType.getName().equals(name))
                return dataType;

        var dataType = new DataType(name);
        dataTypes.add(dataType);

        return dataType;
    }

    /**
     * Loads a library into this environment and all corresponding library
     * dependencies.
     * 
     * @param lib - The library to load.
     */
    public void loadLibrary(Library lib) {
        lib.register(this);
        libraries.add(lib);
    }

    /**
     * Gets a read-only list of all libraries in this environment.
     * 
     * @return A list of all libraries.
     */
    public List<Library> getLoadedLibraries() {
        return libraries.asReadOnly();
    }
}
