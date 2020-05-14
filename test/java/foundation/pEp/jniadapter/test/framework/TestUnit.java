package foundation.pEp.jniadapter.test.framework;

import java.util.function.Consumer;

public class TestUnit<T extends AbstractTestContext> implements Runnable {
    String testUnitName = "default test unit";
    T ctx;
    Consumer<T> lambda;

    public TestUnit(String name, T context, Consumer<T> lambda) {
        this.testUnitName = name;
        this.lambda = lambda;
        this.ctx = context;
    }

    public void run() {
        TestLogger.logH1(testUnitName);

        try {
            //Init the Context
            ctx.init();
            //Run the test against the context
            lambda.accept(ctx);
        } catch (Throwable t) {
            //Test fails, upon cought exception, otherwise succeeds
            TestLogger.logH1("TestUnit FAILED: " + t.toString());
            return;
        }

        TestLogger.logH2("SUCCESS!");
    }
}
