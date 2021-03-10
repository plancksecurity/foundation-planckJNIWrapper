package foundation.pEp.jniadapter.test.jni96;

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

        new TestUnit<CTXBase>("import_key() 1 pub key",new CTXBase() , ctx  -> {
            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(ctx.keyAlicePub);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 0: "pub key should not be in return";
        });

        new TestUnit<CTXBase>("import_key() 1 sec key",new CTXBase() , ctx  -> {
            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(ctx.keyAliceSec);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 1: "imported sec key should be returned";
        });

        new TestUnit<CTXBase>("import_key() 2 pub keys",new CTXBase() , ctx  -> {
            byte[] keys = concat(ctx.keyAlicePub, ctx.keyBobPub);

            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(keys);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 0: "imported pub keys should not be returned";
        });

        new TestUnit<CTXBase>("import_key() 2 sec keys",new CTXBase() , ctx  -> {
            byte[] keys = concat(ctx.keyAliceSec, ctx.keyBobSec);

            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(keys);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 2: "imported sec keys should be returned";
        });

        new TestUnit<CTXBase>("import_key() 4 keys pub/sec",new CTXBase() , ctx  -> {
            byte[] keys = concat(ctx.keyAlicePub, concat(ctx.keyAliceSec, concat(ctx.keyBobPub, ctx.keyBobSec)));

            Vector<Identity> privKeys = null;
            privKeys = ctx.engine.importKey(keys);
            log(AdapterTestUtils.identityListToString(privKeys, false));
            assert privKeys.size() == 2: "nr of imported keys doesnt match";
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


