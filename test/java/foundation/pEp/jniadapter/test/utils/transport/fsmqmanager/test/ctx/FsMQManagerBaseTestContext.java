package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.pitytest.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils.FsMQManagerTestUtils;

import java.util.*;

public class FsMQManagerBaseTestContext extends AbstractTestContext {
    private String qDirBase = "../resources/fsmsgqueue-test/";
    public String address = null;
    public FsMQIdentity self = null;
    private List<String> peerNames = null;
    protected Map<String, FsMQIdentity> peerMap = null;
    protected List<FsMQIdentity> peerList = null;

    private int MSG_COUNT = 10;
    private List<String> messages;


    public FsMQManagerBaseTestContext(String selfAddress) {
        address = selfAddress;
    }

    @Override
    public void init() throws Throwable {
        peerNames = new ArrayList<>();
        peerNames.add("Alice");
        peerNames.add("Bob");
        peerNames.add("Carol");
        createPeerMapAndPeerList();
        defineSelfAndUpdatePeers();
        messages = FsMQManagerTestUtils.createTestMessages(self.getAddress(), MSG_COUNT);
    }

    private void createPeerMapAndPeerList() {
        peerMap = new HashMap<>();
        peerList = new ArrayList<>();
        for (String addr : peerNames) {
            FsMQIdentity ident = new FsMQIdentity(addr, getQDirForAddress(addr));
            peerMap.put(addr, ident);
            peerList.add(ident);
        }
    }

    private void defineSelfAndUpdatePeers() {
        self = peerMap.get(address);
        if (self == null) {
            throw new RuntimeException("selfAddress not found");
        }
        peerMap.remove(address);
        peerList.removeIf(p -> p.getAddress().equals(self.getAddress()));
    }

    private String getQDirForAddress(String addr) {
        return qDirBase + "/" + addr;
    }

    public List<String> getMessages() {
        return messages;
    }

}