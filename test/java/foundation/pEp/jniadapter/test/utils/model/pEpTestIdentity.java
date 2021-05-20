package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class pEpTestIdentity extends TestIdentity {
    private List<pEpTestKeyPair> keys = new ArrayList<>();
    public Identity pEpIdent = null;
    private pEpTestKeyPair defaultKey = null;
    private pEpTestKeyPair defaultKeyPP = null;

    public pEpTestIdentity() {
        super();
    }

    @Override
    public void initialize(TestModel model, Role role) {
        super.initialize(model, role);
        pEpIdent = new Identity();
        pEpIdent.username = role.toString();
        pEpIdent.address = role + "@peptest.org";
    }

    public void addKey(pEpTestKeyPair kp, boolean isDefault) {
        keys.add(kp);
        if (isDefault) {
            if (kp.getType() == KeyType.NORMAL) {
                defaultKey = kp;
            } else {
                defaultKeyPP = kp;
            }
        }
    }

    public pEpTestKeyPair getDefaultKey(boolean passphrase) {
        if (!passphrase) {
            return defaultKey;
        } else {
            return defaultKeyPP;
        }
    }

    public List<pEpTestKeyPair> getAllKeys() {
        return keys;
    }

    public List<pEpTestKeyPair> getNormalKeys() {
        return keys.stream().filter(i -> {
            return i.getType().equals(KeyType.NORMAL);
        }).collect(Collectors.toList());
    }

    public List<pEpTestKeyPair> getPassphraseKeys() {
        return keys.stream().filter(i -> {
            return i.getType().equals(KeyType.PASSPHRASE);
        }).collect(Collectors.toList());
    }
}
