package foundation.pEp.jniadapter.test.jni111;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.readKey;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.exceptions.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.util.Vector;


// https://pep.foundation/jira/browse/JNI-111

class JNI111TestContext extends AdapterBaseTestContext {
    @Override
    public void init() throws RuntimeException {
        super.init();
        alice = null;
        bob = null;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
//        readKey();
        TestSuite.getDefault().setVerbose(false);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        AdapterBaseTestContext jni111Ctx = new JNI111TestContext();

        new TestUnit<AdapterBaseTestContext>("importKey()", jni111Ctx, ctx -> {
            assert ctx.alice == null: "Alice is not null";
            ctx.alice = ctx.engine.importKey(ctx.keyAliceSecPassphrase).get(0);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            assert ctx.alice != null: "Keyimport failed";
        });

        new TestUnit<AdapterBaseTestContext>("setOwnKey()", jni111Ctx, ctx -> {
            ctx.alice.user_id = "23";
            ctx.alice = ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            assert ctx.alice.me == true;
            assert ctx.alice.comm_type == CommType.PEP_ct_pEp;
        });

        new TestUnit<AdapterBaseTestContext>("encrypt_message() -> pEpPassphraseRequired ", jni111Ctx, ctx -> {
            try {
                Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            } catch (pEpException e) {
                assert e instanceof pEpPassphraseRequired: "wrong exception type";
                return;
            }
            assert false:"encrypt_message() should have failed";
        });

        new TestUnit<AdapterBaseTestContext>("config_passphrase() wrong", jni111Ctx, ctx -> {
            ctx.engine.config_passphrase("WRONG PASSPHRASE");
        });

        new TestUnit<AdapterBaseTestContext>("encrypt_message() -> pEpWrongPassphrase ", jni111Ctx, ctx -> {
            try {
                Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            } catch (pEpException e) {
                assert e instanceof pEpWrongPassphrase: "wrong exception type";
                return;
            }
            assert false:"encrypt_message() should have failed";
        });

        new TestUnit<AdapterBaseTestContext>("config_passphrase() correct", jni111Ctx, ctx -> {
            ctx.engine.config_passphrase("passphrase_alice");
        });

        new TestUnit<AdapterBaseTestContext>("encrypt_message() -> success", jni111Ctx, ctx -> {
            assert ctx.msgToSelf.getEncFormat() == Message.EncFormat.None : "Orig msg not plain";
            Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            assert enc.getEncFormat() == Message.EncFormat.PGPMIME :"Message not encrypted";
            assert !enc.getLongmsg().contains(ctx.msgToSelf.getLongmsg()): "Message not encrypted";
            log(AdapterTestUtils.msgToString(enc, false));
        });

        TestSuite.getDefault().run();
    }
}