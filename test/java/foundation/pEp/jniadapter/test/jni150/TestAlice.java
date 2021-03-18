package foundation.pEp.jniadapter.test.jni150;

import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-150 - Test: Ident.user_id For Own Identities

Expected Behaviour
after creating keypairs on new and first identity in a pEp-DB (alice) using myself()
alice.user_id has the user_id that has been provided to myself()

For every following ident created using myself():
ident.user_id has to be equal to alice.user_id
*/


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXBase ctxBase = new CTXBase();

        new TestUnit<CTXBase>("user_id after first myself() ever", ctxBase, ctx -> {
            Identity result = ctx.engine.myself(ctx.alice);
            log(AdapterTestUtils.identityToString(result, true));
            assert result.user_id.equals(ctx.alice.user_id): result.address + ": user_id is expected to be "+ctx.alice.user_id+", but is: " + result.user_id;
        });

        new TestUnit<CTXBase>("user_id constant for all own_identities", ctxBase, ctx -> {
            Identity result = ctx.engine.myself(ctx.bob);
            log(AdapterTestUtils.identityToString(result, true));
            assert result.user_id.equals(ctx.alice.user_id): result.address + ": user_id is expected to be "+ctx.alice.user_id+", but is: " + result.user_id;
        });

        new TestUnit<CTXBase>("user_id constant for all own_identities", ctxBase, ctx -> {
            Identity result = ctx.engine.myself(ctx.carol);
            log(AdapterTestUtils.identityToString(result, true));
            assert result.user_id.equals(ctx.alice.user_id): result.address + ": user_id is expected to be "+ctx.alice.user_id+", but is: " + result.user_id;
        });

        TestSuite.getDefault().run();
    }
}


