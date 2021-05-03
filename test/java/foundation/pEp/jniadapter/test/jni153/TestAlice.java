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
            ctx.myself.pEpIdent = ctx.engine.myself(ctx.myself.pEpIdent);
            log(AdapterTestUtils.identityToString(ctx.myself.pEpIdent, true));
            ctx.transport.clearOwnQueue();
            int counter = 0;
            while (true) {
                Message src = AdapterTestUtils.makeNewTestMessage(ctx.myself.pEpIdent, ctx.partner.pEpIdent, Message.Direction.Outgoing);
                src.setLongmsg("UNIQUE_" + String.valueOf(counter));
                ctx.sendMessage(ctx.partner.pEpIdent, src.getLongmsg());
                ctx.reveiveMessage();

                counter++;
                TestUtils.sleep(3000);
//                TestUtils.readKey();
            }
        });

        TestSuite.getDefault().run();
    }
}


