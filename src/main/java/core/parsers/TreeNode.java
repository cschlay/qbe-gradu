package core.parsers;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    public String value;
    public List<TreeNode> children;
    public int length;

    public TreeNode() {
        value = "";
        children = new ArrayList<TreeNode>();
    }

}
