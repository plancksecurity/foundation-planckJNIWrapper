package foundation.pEp.jniadapter.test.jni115;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class Jni115TestContext extends AdapterBaseTestContext {
    public int messagesToBobCount = 1000;
    public List<Message> messagesToBob = new ArrayList<>();

    @Override
    public void init() throws Throwable {
        super.init();

        for (int i = 0; i < messagesToBobCount; i++) {
            Message tmp = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);
            tmp.setLongmsg(tmp.getLongmsg() + " nr: " + String.valueOf(i));
            messagesToBob.add(tmp);
        }
    }

}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        Jni115TestContext ctx1 = new Jni115TestContext();

        new TestUnit<Jni115TestContext>("myself()", ctx1, ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            if (ctx.alice.fpr == null) {
                throw new RuntimeException();
            }
        });
        new TestUnit<Jni115TestContext>("importKey()", ctx1, ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        });

        new TestUnit<Jni115TestContext>("#MassEncryption", ctx1, ctx -> {
            TestUtils.readKey();
            log("Encrypting " + ctx.messagesToBobCount + " messages");
            Duration total = new StopWatch(() -> {
                for (Message msg : ctx.messagesToBob) {
                    Message encrypted = ctx.engine.encrypt_message(msg, null, Message.EncFormat.PEP);
                    logRaw(".");
                }
            }).getDuration();
            logRaw("\n");
            log("Total time [ms]: " + total.toMillis());
        });

        TestSuite.getDefault().run();
    }
}



