package net.whg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An extension of a standard Java ArrayList with the addition of a function
 * that returns a read-only interface for interacting with this list in a public
 * environment.
 */
public class SafeArrayList<T> extends ArrayList<T> {
    private transient final List<T> readonly;

    /**
     * Creates a new, empty, SafeArrayList.
     */
    public SafeArrayList() {
        super();
        readonly = Collections.unmodifiableList(this);
    }

    /**
     * Creates a new, empty, SafeArrayList.
     * 
     * @param alloc - How much space to preallocate to the underlying array.
     */
    public SafeArrayList(int alloc) {
        super(alloc);
        readonly = Collections.unmodifiableList(this);
    }

    /**
     * Gets a read-only interface for interacting with this array list. This
     * function does not allocate.
     * 
     * @return An unmodifiable list interface.
     */
    public List<T> asReadOnly() {
        return readonly;
    }
}