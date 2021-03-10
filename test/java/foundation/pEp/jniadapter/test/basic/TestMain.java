package foundation.pEp.jniadapter.test.basic;

import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;

import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

class BasicTestContext extends AdapterBaseTestContext {
    Message enc;
    decrypt_message_Return result;

    public BasicTestContext() {
        setTestContextName("BasicTestContext");
    }

    public BasicTestContext init() throws Throwable{
        super.init();
        return this;
    }
}

class TestMain {
    public static void main(String[] args) {
        TestSuite.getDefault().setVerbose(false);
        BasicTestContext btc = new BasicTestContext();

        new TestUnit<BasicTestContext>("Gen Keys", btc, ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            log("Keys generated: " + ctx.alice.fpr);
        });

        new TestUnit<BasicTestContext>("Import key", btc, ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        });

        new TestUnit<BasicTestContext>("Trustwords", btc, ctx -> {
            ctx.carol = new Identity();
            ctx.carol.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";
            String t = ctx.engine.trustwords(ctx.carol);
            log("Trustwords: " + t);
        });

        new TestUnit<BasicTestContext>("setAttachments", btc, ctx -> {
            log("Adding " + ctx.attachmentList.getCount() + " attachments");
            ctx.msgAliceToBob.setAttachments(ctx.attachmentList.getAttachments());
        });

        new TestUnit<BasicTestContext>("Encrypt", btc, ctx -> {
            ctx.enc = ctx.engine.encrypt_message(ctx.msgAliceToBob, null, Message.EncFormat.PEP);
            log(AdapterTestUtils.msgToString(ctx.enc, false));
        });

        new TestUnit<BasicTestContext>("Rating Preview", btc, ctx -> {
            log("Rating preview: " + ctx.engine.outgoing_message_rating_preview(ctx.msgAliceToBob));
        });

        new TestUnit<BasicTestContext>("Rating", btc, ctx -> {
            log("Rating" + ctx.engine.outgoing_message_rating(ctx.msgAliceToBob));
        });

        new TestUnit<BasicTestContext>("Decrypt", btc, ctx -> {
            ctx.result = ctx.engine.decrypt_message(ctx.enc, new Vector<>(), 0);
            log(AdapterTestUtils.msgToString(ctx.result.dst, false));
        });

        new TestUnit<BasicTestContext>("key_reset_all_own_keys()", btc, ctx -> {
            ctx.engine.key_reset_all_own_keys();
        });

        new TestUnit<BasicTestContext>("startSync()", btc, ctx -> {
            ctx.engine.startSync();
        });

        new TestUnit<BasicTestContext>("Keygen2", btc, ctx -> {
            Identity user2 = new Identity();
            user2.user_id = "pEp_own_userId";
            user2.me = true;
            user2.username = "Test User 2";
            user2.address = "jniTestUser2@peptest.ch";
            user2 = ctx.engine.myself(user2);
            log("Keys generated: " + user2.fpr);
        });

        new TestUnit<BasicTestContext>("stopSync()", btc, ctx -> {
            ctx.engine.stopSync();
        });

        TestSuite.getDefault().run();
    }
}

