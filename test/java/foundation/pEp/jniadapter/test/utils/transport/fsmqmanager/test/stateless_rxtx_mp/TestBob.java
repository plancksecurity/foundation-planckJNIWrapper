package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.stateless_rxtx_mp;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.*;

import java.io.IOException;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);
        String myself = "Bob";
        FsMQManagerTestContext testCtx = new FsMQManagerTestContext(myself);

        new TestUnit<FsMQManagerTestContext>("I am: " + myself, testCtx, ctx -> {
            log("I am: " + ctx.self.getAddress());
            assert ctx.self.getAddress().equals(myself);
        });

        new TestUnit<FsMQManagerTestContext>("I know Alice and Carol", testCtx, ctx -> {
            log("I know:");
            log("QM");
            for (FsMQIdentity ident : ctx.qm.identities.getAll()) {
                log(ident.toString());
            }
        });

        new TestUnit<FsMQManagerTestContext>("Clear own queue", testCtx, ctx -> {
            ctx.qm.clearOwnQueue();
        });

        new TestUnit<FsMQManagerTestContext>("PingPong", testCtx, ctx -> {
            try {
                FsMQMessage msg;
                while((msg = ctx.qm.receiveMessage(5)) != null) {
                    // RX
                    String fromStr =  msg.getFrom().getAddress();
                    String msgRx =  msg.getMsg();
                    log("RX From: " + fromStr);
                    log(msgRx);

                    // TX
                    String toStr = fromStr;
                    String msgTx = ctx.getMessages().get(0) + msgRx;
                    log("TX to:" + toStr);
                    log(msgTx);
                    ctx.qm.sendMessage(fromStr,msgTx);
                }
            } catch (IOException e) {
                assert false :e.toString();
            } catch (ClassNotFoundException e) {
                assert false : e.toString();
            }
        });

        TestSuite.getDefault().run();
    }
}