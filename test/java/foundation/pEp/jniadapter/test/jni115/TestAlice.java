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
    public int messagesToBobCount = 10;
    public List<Message> messagesToBobSmall;
    public List<Message> messagesToBobBig;
    public List<Message> messagesToBobHuge;
    public List<Message> messagesToBob;

    @Override
    public Jni115TestContext init() throws Throwable {
        super.init();
        messagesToBobSmall = new ArrayList<>();
        messagesToBobBig = new ArrayList<>();
        messagesToBobHuge = new ArrayList<>();

        log("Preparing " + messagesToBobCount + " messagesToBob small");
        for (int i = 0; i < messagesToBobCount; i++) {
            logRaw(".");
            Message tmp = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);
            tmp.setLongmsg(tmp.getLongmsg() + " nr: " + String.valueOf(i));
            messagesToBobSmall.add(tmp);
        }
        logRaw("\n");


        log("Preparing " + messagesToBobCount + " messagesToBob big");
        for (int i = 0; i < messagesToBobCount; i++) {
            logRaw(".");
            Message tmp = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);
            tmp.setLongmsg(TestUtils.repeatString(tmp.getLongmsg() + " nr: " + String.valueOf(i), 100));
            messagesToBobBig.add(tmp);
        }
        logRaw("\n");

        log("Preparing " + messagesToBobCount + " messagesToBob huge");
        for (int i = 0; i < messagesToBobCount; i++) {
            logRaw(".");
            Message tmp = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);
            tmp.setLongmsg(TestUtils.repeatString(tmp.getLongmsg() + " nr: " + String.valueOf(i), 10000));
            messagesToBobHuge.add(tmp);
        }
        logRaw("\n");
        return this;
    }

}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(false);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        Jni115TestContext ctx1 = new Jni115TestContext();

        new TestUnit<Jni115TestContext>("myself()", ctx1, ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            if (ctx.alice.fpr == null) {
                throw new RuntimeException();
            }
        }).run();


        TestUnit perfTest = new TestUnit<Jni115TestContext>("EncPerf mini message", ctx1, ctx -> {
//            TestUtils.readKey();
            log("encrypting " + ctx.messagesToBobCount + " messages");
            Message encrypted = null;
            for (Message msg : ctx.messagesToBob) {
                encrypted = ctx.engine.encrypt_message(msg, null, Message.EncFormat.PEP);
                logRaw(".");
                if (encrypted == null) {
//                        log(msg.getEncFormat().toString());
                } else {
//                        log(encrypted.getEncFormat().toString());
                }
            }
            logRaw("\n");
//            log(AdapterTestUtils.msgToString(encrypted,false));

        });

        // Perf test for UNENCRTYPTED (no pubkey)
        ctx1.messagesToBob = ctx1.messagesToBobSmall;
        perfTest.run();

        ctx1.messagesToBob = ctx1.messagesToBobBig;
        perfTest.run();

        ctx1.messagesToBob = ctx1.messagesToBobHuge;
        perfTest.run();


        // Key import
        new TestUnit<Jni115TestContext>("importKey()", ctx1, ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        }).run();

        // Perf test for ENCRTYPTED (with pubkey)
        ctx1.messagesToBob = ctx1.messagesToBobSmall;
        perfTest.run();

        ctx1.messagesToBob = ctx1.messagesToBobBig;
        perfTest.run();

        ctx1.messagesToBob = ctx1.messagesToBobHuge;
        perfTest.run();

//        TestSuite.getDefault().run();
    }
}



