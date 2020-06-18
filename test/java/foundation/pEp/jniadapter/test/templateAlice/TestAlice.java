package foundation.pEp.jniadapter.test.templateAlice;
import static foundation.pEp.pitytest.TestLogger.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("Test Alice",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail

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


