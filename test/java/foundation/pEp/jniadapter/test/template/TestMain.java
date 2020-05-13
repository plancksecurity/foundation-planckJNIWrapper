package foundation.pEp.jniadapter.test.template;
import  foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;


class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit("Engine.myself",new TestContext() , ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
        }).run();
    }
}


