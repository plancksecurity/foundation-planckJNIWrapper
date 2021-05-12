package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.model.NodeName;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static foundation.pEp.pitytest.TestLogger.log;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);

        CTXMultiNode JNI153Ctx = new CTXMultiNode(NodeName.NODE_B1);

        new TestUnit<CTXMultiNode>("test", JNI153Ctx, ctx -> {
            ExecutorService executorService = Executors.newFixedThreadPool(10);
            ctx.transport.clearOwnQueue();
            ctx.myself.pEpIdent = ctx.engine.myself(ctx.myself.pEpIdent);
            log(AdapterTestUtils.identityToString(ctx.myself.pEpIdent, true));

            ctx.transport.start();

            int counter = 0;
            while (true) {

                while (ctx.transport.canReceiveAsync()) {
                    Message msgRx = ctx.transport.receiveAsyncNonBlocking();
                    log(AdapterTestUtils.identityToString(msgRx.getFrom(),true));
                    ctx.transport.sendAsync(AdapterTestUtils.newOutMessage(ctx.myself.pEpIdent, msgRx.getFrom(), msgRx.getLongmsg() + " - ACK"));
                    counter++;

                    if (counter % 4 == 0) {
                        executorService.submit(() -> {
                            Engine eng = new Engine();
                            log("Mistrusting");
                            eng.keyMistrusted(msgRx.getFrom());

                        });
                    }
                }
            }
        });

        TestSuite.getDefault().run();
    }


}
