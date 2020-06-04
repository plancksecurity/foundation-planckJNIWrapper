package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FsMQManagerBaseTestContext extends AbstractTestContext {
    private String qDirBase = "../resources/fsmsgqueue-test/";
    private List<String> peerNames = null;
    public Map<String, FsMQIdentity> peerMap = null;
    public List<FsMQIdentity> peerList = null;

    @Override
    public void init() throws Throwable {
        peerNames = new ArrayList<>();
        peerNames.add("Alice");
        peerNames.add("Bob");
        peerNames.add("Carol");
        createPeerMapAndPeerList();
    }

    private void createPeerMapAndPeerList() {
        peerMap = new HashMap<>();
        peerList = new ArrayList<>();
        for (String addr : peerNames) {
            FsMQIdentity ident = new FsMQIdentity(addr, getQDir(addr));
            peerMap.put(addr, ident);
            peerList.add(ident);
        }
    }

    private String getQDir(String address) {
        return qDirBase + "/" + address;
    }
}