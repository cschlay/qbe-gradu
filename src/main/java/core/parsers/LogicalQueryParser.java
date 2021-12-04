package core.parsers;

import java.util.List;

public class LogicalQueryParser {

    public TreeNode parse(String query) {
        TreeNode node = null;
        StringBuilder s = new StringBuilder();

        for (int i = 0; i < query.length(); i++) {
            char c = query.charAt(i);

            if (c == '(') {
                s.setLength(0);
            } else if (c == ',') {
                var n = new TreeNode();
                n.value = s.toString().trim();

                System.out.printf("%s add child %s%n", query, n.value);
                node.children.add(n);

                s.setLength(0);
            } else if (c == ')') {
                var n = new TreeNode();
                n.value = s.toString().trim();
                node.children.add(n);
                node.length = i + l(node.children);

                return node;
            }

            else {
                s.append(c);

                String token = s.toString().trim();
                //stem.out.printf("%s %s %n", query, token);

                if ("AND".equals(token) || "OR".equals(token) || "NOT".equals(token)) {
                    if (node == null) {
                        node = new TreeNode();
                        node.value = token;
                        s.setLength(0);
                    } else {
                        //stem.out.printf("START RECURSION AT %s%n", i);
                        var childNode = parse(query.substring(i-token.length()+1));
                        node.children.add(childNode);
                        System.out.printf("%s add child %s %n", query, childNode.value);
                        i = i + childNode.length;
                        s.setLength(0);
                    }
                }
            }

        }
        return node;
    }

    private int l(List<TreeNode> ll) {
        int c = 0;
        for (var n : ll) {
            c += n.length;
        }
        return c;
    }
}
