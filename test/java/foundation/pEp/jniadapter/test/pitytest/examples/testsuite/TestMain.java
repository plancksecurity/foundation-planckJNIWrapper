package foundation.pEp.jniadapter.test.pitytest.examples.testsuite;
import static foundation.pEp.jniadapter.test.pitytest.TestLogger.*;
import foundation.pEp.jniadapter.test.pitytest.*;

class TestSuiteContext extends AbstractTestContext {
    String name;

    @Override
    public void init() throws Throwable {
        name = "PityTest";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);

        new TestUnit<TestSuiteContext>("Unit Test 1", new TestSuiteContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 1 " + ctx.name);
            ctx.name = "new name";
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        });

        new TestUnit<TestSuiteContext>("Unit Test 3", new TestSuiteContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 3 Failing " + ctx.name);
            int x = 4 / 0;
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        });

        new TestUnit<TestSuiteContext>("Unit Test 2", new TestSuiteContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 2 " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        });

        TestSuite.getDefault().run();
    }
}