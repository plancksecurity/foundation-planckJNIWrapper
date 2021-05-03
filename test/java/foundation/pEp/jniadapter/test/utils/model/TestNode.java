package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestNode {
    private NodeName name = null;
    private TestModel model = null;
    // TODO: Just one role per node for now
    //    private Set<Role> ownRoles = new HashSet();
    private Role defaultRole = null;
    private String homeDir = null;
    private String transportDir = null;

    public TestNode(TestModel model, NodeName name) {
        this.model = model;
        this.name = name;
        homeDir = model.nodesDir + name.toString() + "/";
        transportDir = homeDir + "inboxes/";
        this.model.addNode(this);
    }

    public NodeName getName() {
        return name;
    }

//    public TestModel getModel() {
//        return model;
//    }

    public void setRole(Role role) {
        this.defaultRole = role;
        TestIdentity ident = model.getIdent(role);
        if(!ident.hasNode(getName())) {
            ident.addNode(this);
        }
    }

    public TestIdentity getIdent() {
        return model.getIdent(defaultRole);
    }

//    public void addRole(Role role) {
//        this.ownRoles.add(role);
//        TestIdentity ident = model.getIdent(role);
//        if(!ident.hasNode(getName())) {
//            ident.addNode(this);
//        }
//    }

//    public boolean hasRole(Role role) {
//        return ownRoles.contains(role);
//    }

//    public Set<TestIdentity> getIdents() {
//        Set<TestIdentity> ret = new HashSet();
//        for(Role r : ownRoles) {
//            ret.add(model.getIdent(r));
//        }
//        return ret;
//    }

    public String getHomeDir() {
        return homeDir;
    }

    public String getTransportDir() {
        return transportDir;
    }
}
