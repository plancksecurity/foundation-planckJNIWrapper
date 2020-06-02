package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.regression;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;

import java.util.HashMap;
import java.util.Map;


class FsMsgQueueTestContext extends AbstractTestContext {
    Map<String, String> peers;
    String ownAddress = "Alice";
    String ownQDir = "../resources/fsmsgqueue-test/alice";
    String addressBob = "Bob";
    String addressCarol = "Carol";

    FsMQManager qm;

    @Override
    public void init() throws Throwable {
        peers = new HashMap<>();
        peers.put(addressBob, "../resources/fsmsgqueue-test/bob");
        peers.put(addressCarol, "../resources/fsmsgqueue-test/carol");
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(true);
        FsMsgQueueTestContext testCtx = new FsMsgQueueTestContext();

        new TestUnit<FsMsgQueueTestContext>("Constructor", testCtx, ctx -> {
            log("Creating QM for: " + ctx.ownAddress);
            ctx.qm = new FsMQManager(ctx.ownAddress, ctx.ownQDir);
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Clear own queue", testCtx, ctx -> {
            ctx.qm.clearOwnQueue();
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Add peer bob", testCtx, ctx -> {
            for(String k : ctx.peers.keySet()) {
                log("Adding peer: " + k);
                ctx.qm.addPeer(k, ctx.peers.get(k));
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Broadcast online", testCtx, ctx -> {
            ctx.qm.broadcastSigOnline();
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Wait for bob", testCtx, ctx -> {
            log("Waiting for Bob to signal online");
            ctx.qm.waitForPeerOnline(ctx.addressBob);
            log("Bob is online");
        }).add();




        TestSuite.run();
    }
}



