package foundation.pEp.jniadapter.test.utils.model;

import foundation.pEp.jniadapter.Identity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TestIdentity {
    private Role role = null;
    public Identity pEpIdent = null;
    private List<TestKeyPair> keys = new ArrayList<>();
    private TestKeyPair defaultKey = null;
    private TestKeyPair defaultKeyPP = null;

    public TestIdentity(Role role) {
        this.role = role;
        pEpIdent = new Identity();
        pEpIdent.username = role.toString();
        pEpIdent.address = role + "@peptest.org";
    }

    public Role getRole() {
        return role;
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
}
