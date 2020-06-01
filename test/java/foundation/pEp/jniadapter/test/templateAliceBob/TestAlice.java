package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(true);
        TestSuite.setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Alice tx msg", new AdapterBaseTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);

            if (ctx.alice.fpr == null) {
                throw new RuntimeException();
            }

            //send message
        }).add();

        new TestUnit<AdapterBaseTestContext>("Alice rx msg", new AdapterBaseTestContext(), ctx -> {
            for (int i = 0; i < 1000; i++) {
                log("Alice is waiting for msg...");
                TestUtils.sleep(1000);
            }

        }).add();


        TestSuite.run();
    }
}


