package foundation.pEp.jniadapter.test.templateAliceBobCarol;
import static foundation.pEp.pitytest.TestLogger.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

class TestCarol {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.RED);

        new TestUnit<AdapterBaseTestContext>("Test Carol",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail

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


