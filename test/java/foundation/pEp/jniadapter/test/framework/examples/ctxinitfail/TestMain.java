package foundation.pEp.jniadapter.test.framework.examples.ctxinitfail;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.framework.*;


class CtxInitFailContext extends AbstractTestContext {
    String name;
    int result;

    @Override
    public void init() throws Throwable {
        name = "UnitTestFrameWorkWithoutAName";
        result = 50 / 0;
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);

        new TestUnit<CtxInitFailContext>("ctxinitfail1", new CtxInitFailContext(), ctx -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail1", new CtxInitFailContext(), ctx -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        });

        CtxInitFailContext failingContext = new CtxInitFailContext();

        new TestUnit<CtxInitFailContext>("ctxinitfail2", failingContext, ctx -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail3", failingContext, ctx -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        });

        new TestUnit<CtxInitFailContext>("ctxinitfail4", failingContext, ctx -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        });

        TestSuite.getDefault().run();
    }
}


