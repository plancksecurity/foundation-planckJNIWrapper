package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.regression;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;


class FsMsgQueueTestContext extends AbstractTestContext {
    String ownAddress = "Alice";
    String ownQDir = "../resources/fsmsgqueue-test/alice";

    FsMQIdentity bob = null;

    FsMQManager qm;

    @Override
    public void init() throws Throwable {
        bob = new FsMQIdentity("Bob","../resources/fsmsgqueue-test/bob");
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

        new TestUnit<FsMsgQueueTestContext>("Add ident bob", testCtx, ctx -> {
                log("Adding ident: " + ctx.bob.getAddress());
                ctx.qm.addOrUpdateIdentity(ctx.bob);
        }).add();


        TestSuite.run();
    }
}



