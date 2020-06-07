package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.stateless_ping;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.*;

import java.io.IOException;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
        String myself = "Alice";
        FsMQManagerTestContext testCtx = new FsMQManagerTestContext(myself);

        new TestUnit<FsMQManagerTestContext>("I am: " + myself, testCtx, ctx -> {
            log("I am: " + ctx.self.getAddress());
            assert ctx.self.getAddress().equals(myself);
        });

        new TestUnit<FsMQManagerTestContext>("I know Bob and Carol", testCtx, ctx -> {
            log("I know:");
            log("QM");
            for (FsMQIdentity ident : ctx.qm.identities.getAll()) {
                log(ident.toString());
            }
        });

        new TestUnit<FsMQManagerTestContext>("Clear own queue", testCtx, ctx -> {
            ctx.qm.clearOwnQueue();
        });

        new TestUnit<FsMQManagerTestContext>("Ping initiator", testCtx, ctx -> {
            int pingMax = 10;
            int pingCount = 0;
            while (pingCount < pingMax) {
                // TX
                String toStr = "Bob";
                String msgTx = "ping";
                try {
                    log("TX to:" + toStr);
                    log(msgTx);
                    ctx.qm.sendMessage(toStr, msgTx);
                } catch (IOException e) {
                    assert false : e.toString();
                }

                // RX
                String fromStr = null;
                String msgRx = null;
                try {
                    FsMQMessage msg;
                    while ((msg = ctx.qm.receiveMessage()) == null) {
                        log("Waiting for ping-reply...");
                        TestUtils.sleep(250);
                    }
                    fromStr = msg.getFrom().getAddress();
                    msgRx = msg.getMsg();
                    log("RX From: " + fromStr);
                    log(msgRx);
                } catch (Exception e) {
                    assert false : e.toString();
                }

                if (fromStr.equals(toStr)) {
                    if (msgRx.equals(msgTx)) {
                        pingCount++;
                    }
                }
            }
        });

        TestSuite.getDefault().run();
    }
}