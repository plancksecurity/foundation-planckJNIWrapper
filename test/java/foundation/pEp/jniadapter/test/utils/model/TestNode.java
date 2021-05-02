package foundation.pEp.jniadapter.test.utils.model;

import java.util.HashMap;
import java.util.Map;

public class TestNode {
    private Node name = null;
    private Map<Role, TestIdentity> ownIdents = new HashMap<>();
    private String TransportAddress = null;

    public TestNode(Node name) {
        this.name = name;
    }

    public Node getName() {
        return name;
    }

    public Map<Role, TestIdentity> getOwnIdents() {
        return ownIdents;
    }

    public void addOwnIdent(TestIdentity ident) {
        this.ownIdents.put(ident.getRole(), ident);
    }

    public String getTransportAddress() {
        return TransportAddress;
    }

    public void setTransportAddress(String transportAddress) {
        TransportAddress = transportAddress;
    }
}
