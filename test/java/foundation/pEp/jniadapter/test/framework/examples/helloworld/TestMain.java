package foundation.pEp.jniadapter.test.framework.examples.helloworld;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;


class HelloWorldTestContext extends AbstractTestContext {
    String name;

    @Override
    public void init() throws Throwable {
        name = "UnitTestFrameWorkWithoutAName";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<HelloWorldTestContext>("Hello World1",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("OK Hello World 1 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).run();

        new TestUnit<HelloWorldTestContext>("Hello World2",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("OK Hello World 2 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).run();

        new TestUnit<HelloWorldTestContext>("Hello World3",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Failing Hello World 3 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
            throw new RuntimeException();
        }).run();
    }
}


