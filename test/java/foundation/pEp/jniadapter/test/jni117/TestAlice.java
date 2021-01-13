package foundation.pEp.jniadapter.test.jni117;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Test Alice",new AdapterBaseTestContext() , ctx  -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            log(new String(ctx.engine.export_key(ctx.alice.fpr)));
        });

        TestSuite.getDefault().run();
    }
}


