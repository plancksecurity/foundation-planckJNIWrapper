package foundation.pEp.jniadapter.test.framework.examples.helloworld;
import foundation.pEp.jniadapter.test.framework.*;

class HelloWorldTestContext implements AbstractTestContext {
    String name;

    @Override
    public void init() throws Throwable {
        name = "UnitTestFrameWorkWithoutAName";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<HelloWorldTestContext>("Hello World",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            TestLogger.log("Hello World from: " + ctx.name);
        }).run();
    }
}


