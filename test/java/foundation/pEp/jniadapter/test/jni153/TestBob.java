package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.model.NodeName;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);

        CTXMultiNode JNI153Ctx = new CTXMultiNode(NodeName.NODE_B1);

        new TestUnit<CTXMultiNode>("test", JNI153Ctx, ctx -> {
            ctx.myself.pEpIdent = ctx.engine.myself(ctx.myself.pEpIdent);
            log(AdapterTestUtils.identityToString(ctx.myself.pEpIdent, true));
            ctx.transport.clearOwnQueue();
            int counter = 0;

            while (true) {
                TestUtils.sleep(3000);
                Message msgRx = ctx.reveiveMessage();

                //Mistrust
                if (counter == 1) {
                    log("Mistrusting");
                    ctx.engine.keyMistrusted(msgRx.getFrom());
                }

                ctx.sendMessage(msgRx.getFrom(), msgRx.getLongmsg() + " - ACK");

                counter++;
            }
        });

        TestSuite.getDefault().run();
    }


}
