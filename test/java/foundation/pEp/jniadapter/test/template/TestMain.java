package foundation.pEp.jniadapter.test.template;
import  foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;


class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<TestContext>("Test Template",new TestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS

            ctx.alice = ctx.engine.myself(ctx.alice);

            if(ctx.alice.fpr == null) {
                throw new RuntimeException();
            }
        }).run();
    }
}


