package foundation.pEp.jniadapter.test.basic;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.framework.TestUnit;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;

import java.util.Vector;

import static foundation.pEp.jniadapter.test.framework.TestLogger.log;

class BasicTestContext extends AdapterBaseTestContext {
    Message enc;
    Engine.decrypt_message_Return result;

    public BasicTestContext() {
        setTestContextName("BasicTestContext");
    }
}

class TestMain {
    public static void main(String[] args) {
        BasicTestContext btc = new BasicTestContext();

        new TestUnit<BasicTestContext>("Gen Keys", btc, ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            log("Keys generated: " + ctx.alice.fpr);
        }).run();

        new TestUnit<BasicTestContext>("Import key", btc, ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        }).run();

        new TestUnit<BasicTestContext>("Trustwords", btc, ctx -> {
            ctx.carol = new Identity();
            ctx.carol.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";
            String t = ctx.engine.trustwords(ctx.carol);
            log("Trustwords: " + t);
        }).run();

        new TestUnit<BasicTestContext>("setAttachments", btc, ctx -> {
            int nrAttachemnts = 3;
            log("Adding " + nrAttachemnts + " attachments");
            Vector<Blob> attachments = new Vector<>();

            for (int i = 0; i < nrAttachemnts; i++) {
                Blob blb = new Blob();
                String dataString = "Attachement nr: " + i + " [TEST DATA]";
                blb.data = dataString.getBytes();
                blb.filename = "testfilename.txt";
                attachments.add(blb);
            }
            ctx.msgToBob.setAttachments(attachments);
        }).run();

        new TestUnit<BasicTestContext>("Encrypt", btc, ctx -> {
            ctx.enc = ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
            log(AdapterTestUtils.msgToString(ctx.enc, false));
        }).run();

        new TestUnit<BasicTestContext>("Rating Preview", btc, ctx -> {
            log("Rating preview: " + ctx.engine.outgoing_message_rating_preview(ctx.msgToBob));
        }).run();

        new TestUnit<BasicTestContext>("Rating", btc, ctx -> {
            log("Rating" + ctx.engine.outgoing_message_rating(ctx.msgToBob));
        }).run();

        new TestUnit<BasicTestContext>("Decrypt", btc, ctx -> {
            ctx.result = ctx.engine.decrypt_message(ctx.enc, new Vector<>(), 0);
            log(AdapterTestUtils.msgToString(ctx.result.dst, false));
        }).run();

        new TestUnit<BasicTestContext>("key_reset_all_own_keys()", btc, ctx -> {
            ctx.engine.key_reset_all_own_keys();
        }).run();

        new TestUnit<BasicTestContext>("startSync()", btc, ctx -> {
            ctx.engine.startSync();
        }).run();

        new TestUnit<BasicTestContext>("Keygen2", btc, ctx -> {
            Identity user2 = new Identity();
            user2.user_id = "pEp_own_userId";
            user2.me = true;
            user2.username = "Test User 2";
            user2.address = "jniTestUser2@peptest.ch";
            user2 = ctx.engine.myself(user2);
            log("Keys generated: " + user2.fpr);
        }).run();

        new TestUnit<BasicTestContext>("stopSync()", btc, ctx -> {
            ctx.engine.stopSync();
        }).run();
    }
}

