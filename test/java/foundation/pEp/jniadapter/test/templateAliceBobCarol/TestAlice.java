package foundation.pEp.jniadapter.test.templateAliceBobCarol;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Test Alice",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS

            ctx.alice = ctx.engine.myself(ctx.alice);

            if(ctx.alice.fpr == null) {
                throw new RuntimeException();
            }

            for(int i=0; i < 1000; i++ ) {
                log("Alice is waiting...");
                TestUtils.sleep(1000);
            }

        });

        TestSuite.getDefault().run();
    }
}


