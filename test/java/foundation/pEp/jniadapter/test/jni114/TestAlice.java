package foundation.pEp.jniadapter.test.jni114;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.readKey;
import static foundation.pEp.pitytest.utils.TestUtils.sleep;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.exceptions.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.util.Vector;


// https://pep.foundation/jira/browse/JNI-111


class TestAlice {
    public static void main(String[] args) throws Exception {
//        readKey();
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        AdapterBaseTestContext jni114Ctx = new AdapterBaseTestContext();
        new TestUnit<AdapterBaseTestContext>("ImportKey/SetOwnKey", jni114Ctx, ctx -> {
            // ImportKey and setOwnKey (with passphrase, of course)
            ctx.alice = ctx.engine.importKey(ctx.keyAliceSecPassphrase).get(0);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            ctx.alice.user_id = "23";
            ctx.alice = ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
            assert ctx.alice != null : "Keyimport failed";
            assert ctx.alice.me == true;
            assert ctx.alice.comm_type == CommType.PEP_ct_pEp;
        });


        new TestUnit<AdapterBaseTestContext>("no callback / encrypt fails nonblocking", jni114Ctx, ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            try {
                Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            } catch (pEpException e) {
                assert e instanceof pEpPassphraseRequired : "wrong exception type";
                return;
            }
            assert false : "encrypt_message() should have failed";
        });


        new TestUnit<AdapterBaseTestContext>("use callback for encrypt", jni114Ctx, ctx -> {
            // Register callback passphraseRequired()
            ctx.engine.setPassphraseRequiredCallback(new Sync.PassphraseRequiredCallback() {
                @Override
                public String passphraseRequired() {
                    log("passphraseRequired() called");
                    log("Please Enter Passphrase...");
                    sleep(2000);
                    return "passphrase_alice";
                }
            });

            // myself
            ctx.alice = ctx.engine.myself(ctx.alice);
            log(AdapterTestUtils.identityToString(ctx.alice, true));

            // Encrypt
            assert ctx.msgToSelf.getEncFormat() == Message.EncFormat.None : "Orig msg not plain";
            Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            assert enc.getEncFormat() == Message.EncFormat.PGPMIME : "Message not encrypted";
            assert !enc.getLongmsg().contains(ctx.msgToSelf.getLongmsg()) : "Message not encrypted";
            log(AdapterTestUtils.msgToString(enc, false));
        });


        TestSuite.getDefault().run();
    }
}