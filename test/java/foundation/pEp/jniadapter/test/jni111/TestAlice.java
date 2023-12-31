package foundation.pEp.jniadapter.test.jni111;

import foundation.pEp.jniadapter.CommType;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.exceptions.pEpException;
import foundation.pEp.jniadapter.exceptions.pEpPassphraseRequired;
import foundation.pEp.jniadapter.exceptions.pEpWrongPassphrase;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;


// https://pep.foundation/jira/browse/JNI-111

class JNI111TestContext extends CTXBase {
    @Override
    public JNI111TestContext init() throws Throwable {
        super.init();
        alice = null;
        bob = null;
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
//        readKey();
        TestSuite.getDefault().setVerbose(false);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXBase jni111Ctx = new JNI111TestContext();

        new TestUnit<CTXBase>("importKey()", jni111Ctx, ctx -> {
            assert ctx.alice == null: "Alice is not null";
            ctx.alice = ctx.engine.importKey(ctx.keyAliceSecPassphrase).get(0);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            assert ctx.alice != null: "Keyimport failed";
        });

        new TestUnit<CTXBase>("setOwnKey()", jni111Ctx, ctx -> {
            ctx.alice.user_id = "23";
            ctx.alice = ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            assert ctx.alice.me == true;
            assert ctx.alice.comm_type == CommType.PEP_ct_pEp;
        });

        new TestUnit<CTXBase>("encrypt_message() -> pEpPassphraseRequired ", jni111Ctx, ctx -> {
            try {
                Message enc = ctx.engine.encrypt_message(ctx.msgAliceToAlice, new Vector<>(), Message.EncFormat.PEP);
            } catch (pEpException e) {
                assert e instanceof pEpPassphraseRequired: "wrong exception type";
                return;
            }
            assert false:"encrypt_message() should have failed";
        });

        new TestUnit<CTXBase>("config_passphrase() wrong", jni111Ctx, ctx -> {
            ctx.engine.config_passphrase("WRONG PASSPHRASE");
        });

        new TestUnit<CTXBase>("encrypt_message() -> pEpWrongPassphrase ", jni111Ctx, ctx -> {
            try {
                Message enc = ctx.engine.encrypt_message(ctx.msgAliceToAlice, new Vector<>(), Message.EncFormat.PEP);
            } catch (pEpException e) {
                assert e instanceof pEpWrongPassphrase: "wrong exception type";
                return;
            }
            assert false:"encrypt_message() should have failed";
        });

        new TestUnit<CTXBase>("config_passphrase() correct", jni111Ctx, ctx -> {
            ctx.engine.config_passphrase("passphrase_alice");
        });

        new TestUnit<CTXBase>("encrypt_message() -> success", jni111Ctx, ctx -> {
            assert ctx.msgAliceToAlice.getEncFormat() == Message.EncFormat.None : "Orig msg not plain";
            Message enc = ctx.engine.encrypt_message(ctx.msgAliceToAlice, new Vector<>(), Message.EncFormat.PEP);
            assert enc.getEncFormat() == Message.EncFormat.PGPMIME :"Message not encrypted";
            assert !enc.getLongmsg().contains(ctx.msgAliceToAlice.getLongmsg()): "Message not encrypted";
            log(AdapterTestUtils.msgToString(enc, false));
        });

        TestSuite.getDefault().run();
    }
}