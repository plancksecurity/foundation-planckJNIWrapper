package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils.FsMQManagerTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FsMQManagerBaseTestContext extends AbstractTestContext {
    private String qDirBase = "../resources/fsmsgqueue-test/";
    public String address = null;
    public FsMQIdentity self = null;
    private List<String> peerNames = null;
    public Map<String, FsMQIdentity> peerMap = null;
    public List<FsMQIdentity> peerList = null;

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
        messages = FsMQManagerTestUtils.createTestMessages(self.getAddress(), MSG_COUNT);
    }

    private void createPeerMapAndPeerList() {
        peerMap = new HashMap<>();
        peerList = new ArrayList<>();
        for (String addr : peerNames) {
            FsMQIdentity ident = new FsMQIdentity(addr, getOwnQDir());
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

    private String getOwnQDir() {
        return qDirBase + "/" + address;
    }


    public List<String> getMessages() {
        return messages;
    }

}