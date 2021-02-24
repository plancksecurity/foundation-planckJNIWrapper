package foundation.pEp.jniadapter.test.jni126;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.*;

class Jni126TestContext extends AdapterBaseTestContext {
    @Override
    public AdapterBaseTestContext init() throws Throwable {
        super.init();

        alice = engine.importKey(keyAliceSecPassphrase).get(0);
//        log(AdapterTestUtils.identityToString(alice, true));

        alice.user_id = "23";
        alice = engine.setOwnKey(alice, alice.fpr);
//        log(AdapterTestUtils.identityToString(alice, true));

        engine.config_passphrase("passphrase_alice");
        engine.setPassphraseRequiredCallback(new Sync.PassphraseRequiredCallback() {
            @Override
            public String passphraseRequired(PassphraseType type) {
                log("passphraseRequired() called");
                log("Please Enter Passphrase...");
                sleep(2000);
                return "passphrase_alice";
            }
        });
        return this;
    }

}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        AdapterBaseTestContext jni126Ctx = new Jni126TestContext();

        new TestUnit<AdapterBaseTestContext>("startSync()/stopSync()", jni126Ctx, ctx -> {
            for(int reps = 0; reps < 3; reps++) {
                ctx.engine.startSync();
                for (int i = 0; i < 10; i++) {
                    log("sync enabled");
                    sleep(100);
                }
                ctx.engine.stopSync();
                for (int i = 0; i < 10; i++) {
                    log("sync disbled");
                    sleep(100);
                }
            }
        });

        TestSuite.getDefault().run();
    }
}


