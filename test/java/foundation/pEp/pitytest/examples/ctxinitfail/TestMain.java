package foundation.pEp.pitytest.examples.ctxinitfail;

import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;

import static foundation.pEp.pitytest.TestLogger.log;


class CtxInitFailContext extends AbstractTestContext {
    String name;
    int result;

    @Override
    public CtxInitFailContext init() throws RuntimeException {
        name = "PityTest";
        log("Hello World from: " + name);
//        throw  new RuntimeException("regddjkl");
        result = 50 / 0;

        return this;
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);

        new TestUnit<CtxInitFailContext>("ctxinitfail1", new CtxInitFailContext(), ctx -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail1", new CtxInitFailContext(), ctx -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("Hello World from: " + ctx.name);
        });

        CtxInitFailContext failingContext = new CtxInitFailContext();

        new TestUnit<CtxInitFailContext>("ctxinitfail2", failingContext, ctx -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail3", failingContext, ctx -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail4", failingContext, ctx -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("Hello World from: " + ctx.name);
        });

        TestSuite.getDefault().run();
    }
}


