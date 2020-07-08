package foundation.pEp.jniadapter.test.jni114;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.readKey;

import foundation.pEp.jniadapter.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.util.Vector;


// https://pep.foundation/jira/browse/JNI-111

class JNI114TestContext extends AdapterBaseTestContext {
    @Override
    public void init() throws Throwable {
        super.init();
        alice = null;
        bob = null;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
//        readKey();
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        AdapterBaseTestContext jni111Ctx = new JNI114TestContext();

        new TestUnit<AdapterBaseTestContext>("importKey()", jni111Ctx, ctx -> {
            ctx.alice = ctx.engine.importKey(ctx.keyAliceSecPassphrase).get(0);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            ctx.alice.user_id = "23";
            ctx.alice = ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
            log(AdapterTestUtils.identityToString(ctx.alice, true));

            Message enc = ctx.engine.encrypt_message(ctx.msgToSelf, new Vector<>(), Message.EncFormat.PEP);
            log(AdapterTestUtils.msgToString(enc, false));
//            ctx.engine.startSync();

        });

        TestSuite.getDefault().run();
    }
}