package foundation.pEp.jniadapter.test.utils.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;


public class TestModel<IdentityType extends TestIdentity, NodeType extends TestNode> {
    private Map<Role, IdentityType> idents = new HashMap<>();
    private Map<NodeName, NodeType> nodes = new HashMap<>();

    public String dataDir = "../resources/";
    public String nodesDir = dataDir + "nodes/";

    public TestModel(Supplier<IdentityType> identityTypeSupplier, Supplier<NodeType> nodeTypeSupplier) {
        // Creating all Roles
        for (Role r : Role.values()) {
            IdentityType tmp = identityTypeSupplier.get();
            tmp.initialize(this,r);
            addIdent(tmp);
        }
        // Creating all Nodes
        for (NodeName n : NodeName.values()) {
            NodeType tmp = nodeTypeSupplier.get();
            tmp.initialize(this, n);
            addNode(tmp);
        }
    }

    public void addIdent(IdentityType ident) {
        idents.put(ident.getRole(), ident);
    }

    public IdentityType getIdent(Role name) {
        return idents.get(name);
    }

    public List<IdentityType> getAllIdents() {
        return new ArrayList<>(idents.values());
    }

    public void addNode(NodeType node) {
        nodes.put(node.getName(), node);
    }

    public NodeType getNode(NodeName name) {
        return nodes.get(name);
    }

    public List<NodeType> getAllNodes() {
        return new ArrayList<>(nodes.values());
    }
}
