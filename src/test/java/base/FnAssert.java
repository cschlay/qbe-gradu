package base;

import org.neo4j.graphdb.Transaction;

@FunctionalInterface
public interface FnAssert<T> {
    void accept(Transaction tx, T element) throws Exception;
}
