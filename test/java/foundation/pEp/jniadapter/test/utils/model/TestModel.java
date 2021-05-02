package foundation.pEp.jniadapter.test.utils.model;

import java.util.HashMap;
import java.util.Map;

public class TestModel {
    private Map<Role, TestIdentity> idents = new HashMap<>();
    private Map<Node, TestNode> nodes = new HashMap<>();
    private TestNode myNode = null;

    public TestModel() {
    }

    public TestNode getMyNode() {
        return myNode;
    }

    public void add(TestIdentity ident) {
        idents.put(ident.getRole(), ident);
    }

    public TestIdentity get(Role name) {
        return idents.get(name);
    }

    public void add(TestNode node) {
        nodes.put(node.getName(), node);
    }

//    public TestNode get(Node name) {
//        return nodes.get(name);
//    }

}
