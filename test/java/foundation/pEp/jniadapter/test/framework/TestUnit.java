package foundation.pEp.jniadapter.test.framework;
import foundation.pEp.jniadapter.test.utils.TestUtils;

import java.util.function.Consumer;

public class TestUnit<T extends TestContext> implements Runnable {
    String testUnitName = "default test unit";
    T ctx;
    Consumer<T> lambda;

    public TestUnit(String name, T c, Consumer<T> consumer) {
        testUnitName = name;
        lambda = consumer;
        ctx = c;
    }

    public void run() {
        TestUtils.logH1(testUnitName);
        try {
            ctx.init();
            lambda.accept(ctx);
        } catch (Throwable e) {
            TestUtils.logH1("TestUnit FAILED: " + e.toString());
            return;
        }
        TestUtils.logH2("SUCCESS!");
    }
}

