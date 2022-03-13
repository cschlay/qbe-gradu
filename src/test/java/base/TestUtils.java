package base;

import graphs.QbeEdge;
import graphs.QbeNode;

public class TestUtils {
    public static QbeEdge createTestEdge(String name, String tail, String head) {
        var queryEdge = new QbeEdge(name);
        queryEdge.tailNode = new QbeNode(tail);
        queryEdge.headNode = new QbeNode(head);
        return queryEdge;
    }
    public static QbeEdge createTestEdge(String name, QbeNode tail, QbeNode head) {
        var queryEdge = new QbeEdge(name);
        queryEdge.tailNode = tail;
        queryEdge.headNode = head;
        return queryEdge;
    }
}
