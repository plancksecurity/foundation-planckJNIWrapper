package foundation.pEp.jniadapter.test.jni96;
import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.Identity;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import static foundation.pEp.pitytest.TestLogger.log;

import java.util.Vector;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("import_key() with pub no return",new AdapterBaseTestContext() , ctx  -> {
            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(ctx.keyAlicePub);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 0: "pub key should not be in return";
        });

        new TestUnit<AdapterBaseTestContext>("import_key() with priv key",new AdapterBaseTestContext() , ctx  -> {
            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(ctx.keyAliceSec);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 1: "imported priv key should be returned";
        });

        new TestUnit<AdapterBaseTestContext>("import_key() 2 pub",new AdapterBaseTestContext() , ctx  -> {
            byte[] keys = concat(ctx.keyAlicePub, ctx.keyBobPub);

            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(keys);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 0: "imported priv key should be returned";
        });

        new TestUnit<AdapterBaseTestContext>("import_key() with key array",new AdapterBaseTestContext() , ctx  -> {
            byte[] keys = concat(ctx.keyAlicePub, concat(ctx.keyAliceSec, concat(ctx.keyBobPub, ctx.keyBobSec)));

            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(keys);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 3: "imported priv key should be returned";
        });

        TestSuite.getDefault().run();
    }

    public static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}


