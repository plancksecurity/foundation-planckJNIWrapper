package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.stateless_rxtx_mp;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils.*;

import java.util.ArrayList;
import java.util.List;


class FsMQManagerTestContext extends FsMQManagerBaseTestContext {
    public FsMQManager qm;

    public FsMQManagerTestContext(String selfAddress) {
        super(selfAddress);
    }

    @Override
    public void init() throws Throwable {
        super.init();
        qm = new FsMQManager(self);
        qm.addIdentities(peerList);
    }

}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        String myself = "Alice";
        FsMQManagerTestContext testCtx = new FsMQManagerTestContext(myself);

        new TestUnit<FsMQManagerTestContext>("I am: " + myself, testCtx, ctx -> {
            log("I am: " + ctx.self.getAddress());
            assert ctx.self.getAddress().equals(myself);
        });

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
        });

        TestSuite.getDefault().run();
    }
}