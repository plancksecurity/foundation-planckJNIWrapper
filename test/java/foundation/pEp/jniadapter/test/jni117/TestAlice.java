package foundation.pEp.jniadapter.test.jni117;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;

// Test for JNI-117 - Key Export

// Tests: For an ad-hoc generated identity "alice" using myself()"
// - export_key is not throwing
// - keydata returned is longer than 100 bytes
// - keydata contains header: "-----BEGIN PGP PBLIC KEY BLOCK-----"

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Test Alice",new AdapterBaseTestContext() , ctx  -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            byte[] keydata = ctx.engine.export_key(ctx.alice.fpr);
            String keydataStr = new String(keydata);
            log(keydataStr);
            assert keydata.length > 100: "returned keydata is too short";
            assert keydataStr.contains("-----BEGIN PGP PUBLIC KEY BLOCK-----") : "Keydata doesnt contain: -----BEGIN PGP PBLIC KEY BLOCK-----";
        });

        TestSuite.getDefault().run();
    }
}


