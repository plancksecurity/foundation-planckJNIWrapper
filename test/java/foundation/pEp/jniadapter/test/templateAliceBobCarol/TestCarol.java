package foundation.pEp.jniadapter.test.templateAliceBobCarol;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;

class TestCarol {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.RED);

        new TestUnit<AdapterBaseTestContext>("Test Carol",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS

//            ctx.carol = ctx.engine.myself(ctx.carol);

//            if(ctx.carol.fpr == null) {
//                throw new RuntimeException();
//            }

            for(int i=0; i < 1000; i++ ) {
                log("Carol is waiting...");
                TestUtils.sleep(1000);
            }

        });

        TestSuite.getDefault().run();
    }
}


