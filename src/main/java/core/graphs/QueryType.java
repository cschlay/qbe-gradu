package core.graphs;

/**
 * Marks the operation to perform with the node.
 */
public enum QueryType {
    QUERY,
    // Aggregation
    COUNT,
    SUM,
    // Data manipulation
    DELETE,
    INSERT,
    UPDATE,
}
