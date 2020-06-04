package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.stateless_rxtx_mp;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils.*;

import java.util.ArrayList;
import java.util.List;


class FsMQManagerTestContext extends FsMQManagerBaseTestContext {
    private String selfAddress = null;
    public FsMQIdentity self = null;
    public FsMQManager qm;

    private int MSG_COUNT = 10;
    private List<String> messages;

    public FsMQManagerTestContext(String selfAddress) {
        this.selfAddress = selfAddress;
    }

    @Override
    public void init() throws Throwable {
        super.init();
        defineSelfAndUpdatePeers();
        qm = new FsMQManager(self);
        qm.addIdentities(peerList);
        messages = FsMQManagerTestUtils.createTestMessages(self.getAddress(), MSG_COUNT);
    }

    private void defineSelfAndUpdatePeers() {
        self = peerMap.get(selfAddress);
        if (self == null) {
            throw new RuntimeException("selfAddress not found");
        }
        peerMap.remove(selfAddress);
        peerList.removeIf(p -> p.getAddress().equals(self.getAddress()));
    }

    public List<String> getMessages() {
        return messages;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(true);
        String myself = "Alice";
        FsMQManagerTestContext testCtx = new FsMQManagerTestContext(myself);

        new TestUnit<FsMQManagerTestContext>("I am: " + myself, testCtx, ctx -> {
            log("I am: " + ctx.self.getAddress());
            assert ctx.self.getAddress().equals(myself);
        }).add();

        new TestUnit<FsMQManagerTestContext>("I know Bob and Carol", testCtx, ctx -> {
            log("I know:");
            log("QM");
            for (FsMQIdentity ident : ctx.qm.getIdentities()) {
                log(ident.toString());
            }
            log("PeerMap:");
            for (String addr : ctx.peerMap.keySet()) {
                log(addr);
            }
            log("PeerList:");
            for (FsMQIdentity ident : ctx.peerList) {
                log(ident.getAddress());
            }
            assert !ctx.peerMap.containsKey(myself) : "peers should not contain" + myself;
            assert ctx.peerMap.containsKey("Bob") : "peers must contain Bob";
            assert ctx.peerMap.containsKey("Carol") : "peers must contain Carol";
        }).add();

        TestSuite.run();
    }
}