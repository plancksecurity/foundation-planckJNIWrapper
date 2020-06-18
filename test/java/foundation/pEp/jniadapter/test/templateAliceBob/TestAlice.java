package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Alice tx msg", new AdapterBaseTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);

            if (ctx.alice.fpr == null) {
                throw new RuntimeException();
            }

            //send message
        });

        new TestUnit<AdapterBaseTestContext>("Alice rx msg", new AdapterBaseTestContext(), ctx -> {
            for (int i = 0; i < 1000; i++) {
                log("Alice is waiting for msg...");
                TestUtils.sleep(1000);
            }

        });

        TestSuite.getDefault().run();
    }
}


