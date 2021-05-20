package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestIdentity {
    private TestModel model = null; // Belongs to a TestModel (Tree struture)
    private Role role = null;       // Assumes a Role
    private Role defaultPartner = null;
    private Map<NodeName, FsMQIdentity> transportIdents = new HashMap<>();
    private boolean isInitialized = false;

    public TestIdentity() {
    }

    // this method has to be called before you can do ANYTHING with this object
    public void initialize( TestModel model, Role role) {
        this.role = role;
        this.model = model;
        isInitialized = true;
    }

    public Role getRole() {
        ensureInitialized();
        return role;
    }

    public TestIdentity getDefaultPartner() {
        ensureInitialized();
        return model.getIdent(defaultPartner);
    }

    public void setDefaultPartner(Role defaultPartner) {
        this.defaultPartner = defaultPartner;
    }

    public boolean hasNode(NodeName nodeName) {
        return transportIdents.keySet().contains(nodeName);
    }

    public void addNode(TestNode node) {
        ensureInitialized();
        FsMQIdentity tmp = createTransportIdentity(node);
        transportIdents.put(node.getName(), tmp);
        if (node.getIdent().getRole() != getRole()) {
            node.setDefaultRole(getRole());
        }
    }

    public List<FsMQIdentity> getAllTransportIdents() {
        return new ArrayList<>(transportIdents.values());
    }

    public FsMQIdentity getTransportIdent(NodeName nodeName) {
        return transportIdents.get(nodeName);
    }

    private FsMQIdentity createTransportIdentity(TestNode node) {
        ensureInitialized();
        String transportAddress = node.getName().toString() + getRole().toString();
        String transportDir = node.getTransportDir() + getRole().toString();
        return new FsMQIdentity(transportAddress, transportDir);
    }

    private void ensureInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("not initialized");
        }
    }
}
