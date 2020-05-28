package foundation.pEp.jniadapter.test.framework.examples.testsuite;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;

class TestSuiteTestContext extends AbstractTestContext {
    String name;

    @Override
    public void init() throws Throwable {
        name = "UnitTestFrameWorkWithoutAName";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
//        TestSuite.setVerbose(true);

        new TestUnit<TestSuiteTestContext>("Unit Test 1", new TestSuiteTestContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 1 " + ctx.name);
            ctx.name = "new name";
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).add();

        new TestUnit<TestSuiteTestContext>("Unit Test 2", new TestSuiteTestContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 2 " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).add();

        new TestUnit<TestSuiteTestContext>("Unit Test 3", new TestSuiteTestContext(), ctx -> {
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Unit Test 3 Failing " + ctx.name);
            int x = 4 / 0;
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).add();

        TestSuite.run();
    }
}