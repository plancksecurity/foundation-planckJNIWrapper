package foundation.pEp.jniadapter.test.jni119;

import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Vector;


class TestAlice {
    public static void main(String[] args) throws Throwable {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("enter_device_group() no exception with no identities", new AdapterBaseTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.enter_device_group(new Vector<Identity>());
        });

        new TestUnit<AdapterBaseTestContext>("enter_device_group() no exception with 2 identities", new AdapterBaseTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            Vector<Identity> grpIdents = new Vector<Identity>();
            grpIdents.add(ctx.alice);
            grpIdents.add(ctx.bob);

            ctx.engine.enter_device_group(grpIdents);
        });

        TestSuite.getDefault().run();
    }
}


