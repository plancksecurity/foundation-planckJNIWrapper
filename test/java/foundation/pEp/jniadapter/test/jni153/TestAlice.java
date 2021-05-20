package foundation.pEp.jniadapter.test.jni153;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.model.NodeName;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXMultiNode JNI153Ctx = new CTXMultiNode(NodeName.NODE_A1);

        new TestUnit<CTXMultiNode>("test", JNI153Ctx, ctx -> {
            ctx.transport.clearOwnQueue();
            ctx.myself.pEpIdent = ctx.engine.myself(ctx.myself.pEpIdent);
            log(AdapterTestUtils.identityToString(ctx.myself.pEpIdent, true));

            ctx.transport.start();
            Message outMsg = AdapterTestUtils.newOutMessage(ctx.myself.pEpIdent, ctx.partner.pEpIdent, "UNIQUE_" + String.valueOf(0));
            ctx.transport.sendAsync(outMsg.encodeMIME(), ctx.partner);
            int counter = 0;
//            TestUtils.readKey();
            while (true) {
                while (ctx.transport.canReceiveAsync()) {
                    outMsg = AdapterTestUtils.newOutMessage(ctx.myself.pEpIdent, ctx.partner.pEpIdent, "UNIQUE_" + String.valueOf(counter));
                    ctx.transport.sendAsync(outMsg.encodeMIME(), ctx.partner);
                    Message msg = new Message(ctx.transport.receiveAsyncNonBlocking());
                    counter++;
                }
            }
        });

        TestSuite.getDefault().run();
    }
}


