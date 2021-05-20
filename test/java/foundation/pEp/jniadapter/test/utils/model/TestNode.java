package foundation.pEp.jniadapter.test.utils.model;

public class TestNode<IdentityType extends TestIdentity> {
    private NodeName name = null;
    private TestModel<IdentityType,TestNode<IdentityType>> model = null;
    // TODO: Just one role per node for now
    //    private Set<Role> ownRoles = new HashSet();
    private Role defaultRole = null;
    private String homeDir = null;
    private String transportDir = null;
    private boolean isInitialized = false;

    public TestNode() {
    }

    // this method has to be called before you can do ANYTHING with this object
    public void initialize(TestModel model, NodeName name) {
        this.name = name;
        this.model = model;
        this.homeDir = model.nodesDir + name.toString() + "/";
        this.transportDir = homeDir + "inboxes/";
        this.isInitialized = true;
    }

    public NodeName getName() {
        ensureInitialized();
        return name;
    }

    public void setDefaultRole(Role role) {
        ensureInitialized();
        this.defaultRole = role;
        IdentityType ident = model.getIdent(role);
        if (!ident.hasNode(getName())) {
            ident.addNode(this);
        }
    }

    public IdentityType getIdent() {
        ensureInitialized();
        return model.getIdent(defaultRole);
    }

    public String getHomeDir() {
        ensureInitialized();
        return homeDir;
    }

    public String getTransportDir() {
        ensureInitialized();
        return transportDir;
    }

    private void ensureInitialized() {
        if (!isInitialized) {
            throw new IllegalStateException("Not initialized");
        }
    }
}
