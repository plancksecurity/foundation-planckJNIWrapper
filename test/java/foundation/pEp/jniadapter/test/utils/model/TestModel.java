package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestModel {
    private Map<Role, TestIdentity> idents = new HashMap<>();
    private Map<NodeName, TestNode> nodes = new HashMap<>();

    public String dataDir = "../resources/";;
    public String nodesDir = dataDir + "nodes/";

    public TestModel() {
        // Creating all Roles
        for (Role r : Role.values()) {
            new TestIdentity(this, r);
        }
        // Creating all Nodes
        for (NodeName n : NodeName.values()) {
            new TestNode(this, n);
        }
    }

    public void addIdent(TestIdentity ident) {
        idents.put(ident.getRole(), ident);
    }

    public TestIdentity getIdent(Role name) {
        return idents.get(name);
    }

    public List<TestIdentity> getAllIdents() {
        return new ArrayList<TestIdentity>(idents.values());
    }

    public void addNode(TestNode node) {
        nodes.put(node.getName(), node);
    }

    public TestNode getNode(NodeName name) {
        TestNode re = nodes.get(name);
        return re;
    }

    public List<TestNode> getAllNodes() {
        return new ArrayList<TestNode>(nodes.values());
    }
}
