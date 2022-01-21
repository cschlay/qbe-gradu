package base;

import org.neo4j.graphdb.Transaction;

@FunctionalInterface
public interface FnTransaction {
    void accept(Transaction t) throws Exception;
}
