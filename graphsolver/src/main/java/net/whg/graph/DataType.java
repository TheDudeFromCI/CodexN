package net.whg.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a data type that can be used within the node type.
 */
public final class DataType {
    private final String name;
    private final List<DataType> parents = new ArrayList<>();

    /**
     * Creates a new DataType instance.
     * 
     * @param name - The name of this data type.
     */
    public DataType(String name) {
        this.name = name;
    }

    /**
     * Adds a new parent data type that this data type should extend from.
     * 
     * @param parent - The new parent data type.
     * @throws IllegalArgumentException If adding the requested data type as a
     *                                  parent would cause a circular dependency to
     *                                  occur.
     */
    public void addParentType(DataType parent) {
        verifyNoCircularDependencies(parent);
        this.parents.add(parent);
    }

    /**
     * A recursive function that checks if the given data type would cause a
     * circular dependency if added as a parent to this node.
     * 
     * @param parent - The data type to check.
     * @throws IllegalArgumentException If adding the requested data type as a
     *                                  parent would cause a circular dependency to
     *                                  occur.
     */
    private void verifyNoCircularDependencies(DataType parent) {
        if (parent == this)
            throw new IllegalArgumentException("Cannot create circular dependencies!");

        for (var p : parent.parents) {
            verifyNoCircularDependencies(p);
        }
    }

    /**
     * Gets the name of this data type.
     * 
     * @return The name.
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks whether or not this data type is a subclass of another data type.
     * 
     * @param other - The data type to check against.
     * @return True if this data type is a subclass of the "other" data type. False
     *         otherwise.
     */
    public boolean isInstanceOf(DataType other) {
        if (this == other)
            return true;

        for (var parent : parents) {
            if (parent.isInstanceOf(other))
                return true;
        }

        return false;
    }
}
