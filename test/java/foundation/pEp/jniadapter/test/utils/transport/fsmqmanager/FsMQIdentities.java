package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager;

import foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue.FsMsgQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FsMQIdentities {
    public FsMQIdentity self = null;
    private List<FsMQIdentity> identities = new ArrayList<>();
    private Map<String, FsMsgQueue> identityAddressQueues = new HashMap<String, FsMsgQueue>();

    public FsMQIdentities(FsMQIdentity self) throws NullPointerException {
        if (self != null) {
            this.self = self;
            addOrUpdate(self);
        } else {
            throw new NullPointerException("self cant be null");
        }
    }

    // Identity address must be unique
    // Returns
    // - true for added
    // - false for updated or own ident (which cant be updated)
    public boolean addOrUpdate(FsMQIdentity ident) throws NullPointerException {
        boolean ret = false;
        if (ident != null) {
            if (addIdent(ident)) {
                // Good, add new ident
                ret = true;
            } else {
                // Ok, update ident
                update(ident);
                ret = false;
            }
        } else {
            throw new NullPointerException("ident cant be null");
        }
        return ret;
    }

    // cant update own identity
    // True - Success
    // False - ident not existing or own identity
    public boolean update(FsMQIdentity ident) throws NullPointerException {
        boolean ret = false;
        if (ident != null) {
            if (!isSelf(ident.getAddress()) && exists(ident.getAddress())) {
                remove(ident.getAddress());
                addIdent(ident);
                ret = true;
            }
        } else {
            throw new NullPointerException("ident cant be null");
        }
        return ret;
    }

    // Removes the identity from identities and identityQueues by address
    public boolean remove(String address) throws NullPointerException {
        boolean ret = false;
        if (address != null) {
            if (exists(address) && !isSelf(address)) {
                identities.removeIf(i -> i.getAddress().equals(address));
                identityAddressQueues.entrySet().removeIf(iq -> iq.getKey().equals(address));
                ret = true;
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }

    // cant fail haha
    public void removeAll() {
        for (FsMQIdentity i : getAll()) {
            remove(i.getAddress());
        }
    }

    // Returns number of identities added
    public int addAll(List<FsMQIdentity> idents) throws NullPointerException {
        int ret = 0;
        if (idents != null) {
            for (FsMQIdentity i : idents) {
                if (addOrUpdate(i)) {
                    ret++;
                }
            }
        } else {
            throw new NullPointerException("idents cant be null");
        }
        return ret;
    }

    public boolean isSelf(String address) throws NullPointerException {
        boolean ret = false;
        if (address != null) {
            ret = self.getAddress() == address;
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }

    // True if existing
    // False if not
    // Exception on not unique
    public boolean exists(String address) throws IllegalStateException, NullPointerException{
        boolean ret = false;
        if (address != null) {
            List<FsMQIdentity> matches = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList());
            if (matches.size() > 1) {
                throw new IllegalStateException("Internal Error: Identity address not unique: " + address);
            }
            if (matches.size() == 1) {
                ret = true;
            }
        } else {
            throw new NullPointerException("address cant be null");
        }
        return ret;
    }

    // Returns null if not existing
    public FsMQIdentity getByAddress(String address) {
        FsMQIdentity ret = null;
        if (exists(address)) {
            ret  = identities.stream().filter(i -> i.getAddress().equals(address)).collect(Collectors.toList()).get(0);
        }
        return ret;
    }

    public List<FsMQIdentity> getAll() {
        return new ArrayList<FsMQIdentity>(identities);
    }

    public List<String> getAddresses() {
        List<String> ret = new ArrayList<>();
        for (FsMQIdentity i : identities) {
            ret.add(i.getAddress());
        }
        return ret;
    }

    // True  - success
    // False - already existing
    private boolean addIdent(FsMQIdentity ident) {
        boolean ret = false;
        if (!exists(ident.getAddress())) {
            identities.add(ident);
            createQueueForIdent(ident);
            ret = true;
        }
        return ret;
    }

    private void createQueueForIdent(FsMQIdentity ident) {
        FsMsgQueue q = new FsMsgQueue(ident.getqDir());
        identityAddressQueues.put(ident.getAddress(), q);
    }

    public FsMsgQueue getQueueForIdentity(String address) throws UnknownIdentityException {
        FsMsgQueue ret = null;
        ret = identityAddressQueues.get(address);
        if (ret == null) {
            throw new UnknownIdentityException("Unknown identity address: " + address);
        }
        return ret;
    }

}
