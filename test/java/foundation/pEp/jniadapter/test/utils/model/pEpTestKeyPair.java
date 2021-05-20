package foundation.pEp.jniadapter.test.utils.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class pEpTestKeyPair {
    private KeyType type = null;
    private pEpTestIdentity owner = null;
    private String pathPub = null;
    private String pathSec = null;
    private byte[] keyPub = null;
    private byte[] keySec = null;
    private String passphrase = null;

    public pEpTestKeyPair(pEpTestIdentity owner, String pathPub, String pathSec, boolean isDefault) {
        type = KeyType.NORMAL;
        init(owner, pathPub, pathSec, isDefault);
    }

    public pEpTestKeyPair(pEpTestIdentity owner, String pathPub, String pathSec, String passphrase, boolean isDefault) {
        this.passphrase = passphrase;
        type = KeyType.PASSPHRASE;
        init(owner, pathPub, pathSec, isDefault);
    }

    private void init(pEpTestIdentity owner, String pathPub, String pathSec, boolean isDefault) {
        this.owner = owner;
        this.pathPub = pathPub;
        this.pathSec = pathSec;
        keyPub = readKeyData(this.pathPub);
        keySec = readKeyData(this.pathSec);
        owner.addKey(this, isDefault);
    }

    public pEpTestIdentity getOwner() {
        return owner;
    }

    public KeyType getType() {
        return type;
    }

    public String getPathPub() {
        return pathPub;
    }

    public String getPathSec() {
        return pathSec;
    }

    public byte[] getKeyPub() {
        return keyPub;
    }

    public byte[] getKeySec() {
        return keySec;
    }

    public String getPassphrase() {
        return passphrase;
    }

    private byte[] readKeyData(String path) {
        byte[] keyData = null;
        try {
            Path p = Paths.get(path);
            keyData = Files.readAllBytes(p);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return keyData;
    }
}
