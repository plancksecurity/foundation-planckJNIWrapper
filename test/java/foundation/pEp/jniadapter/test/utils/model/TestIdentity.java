package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;

import java.util.*;
import java.util.stream.Collectors;

public class TestIdentity {
    private TestModel model = null;
    private Role role = null;
    private Role defaultPartner = null;
    public Identity pEpIdent = null;
    private List<TestKeyPair> keys = new ArrayList<>();
    private TestKeyPair defaultKey = null;
    private TestKeyPair defaultKeyPP = null;
    private Map<NodeName,FsMQIdentity> transportIdents = new HashMap<>();

    public TestIdentity(TestModel model, Role role) {
        this.model = model;
        this.role = role;
        pEpIdent = new Identity();
        pEpIdent.username = role.toString();
        pEpIdent.address = role + "@peptest.org";
        this.model.addIdent(this);
    }

//    public TestModel getModel() {
//        return model;
//    }

    public Role getRole() {
        return role;
    }

    public TestIdentity getDefaultPartner() {
        return model.getIdent(defaultPartner);
    }

    public void setDefaultPartner(Role defaultPartner) {
        this.defaultPartner = defaultPartner;
    }

    public boolean hasNode(NodeName nodeName) {
        return transportIdents.keySet().contains(nodeName);
    }

    public void addNode(TestNode node) {
        FsMQIdentity tmp = createTransportIdentity(node);
        transportIdents.put(node.getName(),tmp);
        if (node.getIdent().getRole() != getRole()) {
            node.setRole(getRole());
        }
    }

    public List<FsMQIdentity> getAllTransportIdents() {
        return new ArrayList<>(transportIdents.values());
    }

    public FsMQIdentity getTransportIdent(NodeName nodeName) {
        return transportIdents.get(nodeName);
    }

    public void addKey(TestKeyPair kp, boolean isDefault) {
        keys.add(kp);
        if (isDefault) {
            if (kp.getType() == KeyType.NORMAL) {
                defaultKey = kp;
            } else {
                defaultKeyPP = kp;
            }
        }
    }


    public TestKeyPair getDefaultKey(boolean passphrase) {
        if (!passphrase) {
            return defaultKey;
        } else {
            return defaultKeyPP;
        }
    }

    public List<TestKeyPair> getAllKeys() {
        return keys;
    }

    public List<TestKeyPair> getNormalKeys() {
        return keys.stream().filter(i -> {
            return i.getType().equals(KeyType.NORMAL);
        }).collect(Collectors.toList());
    }

    public List<TestKeyPair> getPassphraseKeys() {
        return keys.stream().filter(i -> {
            return i.getType().equals(KeyType.PASSPHRASE);
        }).collect(Collectors.toList());
    }

    private FsMQIdentity createTransportIdentity(TestNode node) {
        String transportAddress = node.getName().toString() + getRole().toString();
        String transportDir = node.getTransportDir() + getRole().toString();
        return new FsMQIdentity(transportAddress, transportDir);
    }
}
