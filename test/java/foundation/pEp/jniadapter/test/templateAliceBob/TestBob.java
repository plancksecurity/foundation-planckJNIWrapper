package foundation.pEp.jniadapter.test.templateAliceBob;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(true);
        TestSuite.setTestColor(TestUtils.TermColor.YELLOW);

        new TestUnit<AdapterBaseTestContext>("Bob rx msg", new AdapterBaseTestContext(), ctx -> {
            for (int i = 0; i < 1000; i++) {
                log("Bob is waiting for msg...");
                TestUtils.sleep(1000);
            }

        }).add();

        new TestUnit<AdapterBaseTestContext>("Bob tx msg", new AdapterBaseTestContext(), ctx -> {
            ctx.bob = ctx.engine.myself(ctx.bob);

            if (ctx.bob.fpr == null) {
                throw new RuntimeException();
            }

            //send message
        }).add();


        TestSuite.run();
    }
}


