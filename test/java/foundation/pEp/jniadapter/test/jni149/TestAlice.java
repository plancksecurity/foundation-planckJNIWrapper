package foundation.pEp.jniadapter.test.jni149;

import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-149 - Wrap disable_all_sync_channels into the adapter

Test:
create 3 identities a,b,c as new own identities using myself()
assert a,b,c are enabled for sync (ident.flags == 0)
call disable_all_sync_channels();
assert a,b,c, are disabled for sync (ident.flags == 1)
*/


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<CTXBase>("disable_all_sync_channels()", new CTXBase(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);
            ctx.carol = ctx.engine.myself(ctx.carol);
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            log(AdapterTestUtils.identityToString(ctx.bob, true));
            log(AdapterTestUtils.identityToString(ctx.carol, true));
            assert ctx.alice.flags == 0 : ctx.alice.address + ": flags are expected to be 0, but are: " + ctx.alice.flags;
            assert ctx.bob.flags == 0 : ctx.bob.address + ": flags are expected to be 0, but are: " + ctx.bob.flags;
            assert ctx.carol.flags == 0 : ctx.carol.address + ": flags are expected to be 0, but are: " + ctx.carol.flags;

            // disable_all_sync_channels
            ctx.engine.disable_all_sync_channels();

            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);
            ctx.carol = ctx.engine.myself(ctx.carol);
            log("\n\n");
            log(AdapterTestUtils.identityToString(ctx.alice, true));
            log(AdapterTestUtils.identityToString(ctx.bob, true));
            log(AdapterTestUtils.identityToString(ctx.carol, true));
            assert ctx.alice.flags == 1 : ctx.alice.address + ": flags are expected to be 1, but are: " + ctx.alice.flags;
            assert ctx.bob.flags == 1 : ctx.bob.address + ": flags are expected to be 1, but are: " + ctx.bob.flags;
            assert ctx.carol.flags == 1 : ctx.carol.address + ": flags are expected to be 1, but are: " + ctx.carol.flags;
        });

        TestSuite.getDefault().run();
    }
}


