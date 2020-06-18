package foundation.pEp.jniadapter.test.pitytest.examples.helloworld;
import static foundation.pEp.jniadapter.test.pitytest.TestLogger.*;
import foundation.pEp.jniadapter.test.pitytest.*;


class HelloWorldTestContext extends AbstractTestContext {
    String name;

    @Override
    public void init() throws Throwable {
        name = "PityTest";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<HelloWorldTestContext>("Hello World1",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("OK Hello World 1 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).run();

        new TestUnit<HelloWorldTestContext>("Hello World2",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("OK Hello World 2 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        }).run();

        new TestUnit<HelloWorldTestContext>("Hello World3",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // throw or assert, to let a testunit fail
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Failing Hello World 3 from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
            assert false;
        }).run();
    }
}


