package foundation.pEp.jniadapter.test.template;
import  foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;


class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<AdapterBaseTestContext>("Test Template",new AdapterBaseTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS

            ctx.alice = ctx.engine.myself(ctx.alice);

            if(ctx.alice.fpr == null) {
                throw new RuntimeException();
            }
        }).run();
    }
}


