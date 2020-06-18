package foundation.pEp.jniadapter.test.templateAliceBobCarol;
import static foundation.pEp.jniadapter.test.pitytest.TestLogger.*;
import foundation.pEp.jniadapter.test.pitytest.*;
import foundation.pEp.jniadapter.test.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

class TestBob {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.YELLOW);

        new TestUnit<AdapterBaseTestContext>("Test Bob",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail

            ctx.bob = ctx.engine.myself(ctx.bob);

            if(ctx.bob.fpr == null) {
                throw new RuntimeException();
            }

            for(int i=0; i < 1000; i++ ) {
                log("Bob is waiting...");
                TestUtils.sleep(1000);
            }

        });

        TestSuite.getDefault().run();
    }
}


