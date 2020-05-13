package foundation.pEp.jniadapter.test.framework;
import foundation.pEp.jniadapter.test.utils.TestUtils;

import java.util.function.Consumer;

public class TestUnit {
    String testUnitName = "default test unit";
    TestContext ctx;
    Consumer<TestContext> lambda;

    public TestUnit(String name, TestContext c, Consumer<TestContext> consumer) throws Exception {
        testUnitName = name;
        lambda = consumer;
        ctx = c;
    }

    public void run() {
        TestUtils.logH1(testUnitName);
        try {
            lambda.accept(ctx);
        } catch (Throwable e) {
            TestUtils.logH1("TestUnit FAILED: " + e.toString());
            return;
        }
        TestUtils.logH2("SUCCESS!");
    }
}

