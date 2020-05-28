package foundation.pEp.jniadapter.test.framework.examples.ctxmembers;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;


// Context member object instantiation
//
// In the Context, do not use member objects like this:
//     A a = new A();    // WRONG
// Always, declare them as members and instantiate them in the constructor
// Otherwise, all test contexts in a program partially execute before the test using it is actually being run
// Context init() is part of the test

class HelloWorldTestContext extends AbstractTestContext {
    String name;
    ExampleCtxMember correct;
    ExampleCtxMember incorrect = new ExampleCtxMember(false); // WRONG

    @Override
    public void init() throws Throwable {
        log("HelloWorldTestContext: init() called");
        correct = new ExampleCtxMember(true);
        name = "UnitTestFrameWorkWithoutAName";
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<HelloWorldTestContext>("Hello World",new HelloWorldTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("Hello World from: " + ctx.name);
        }).run();
    }
}





// Just an example member obj
class ExampleCtxMember {
    ExampleCtxMember(boolean correct){
        if(correct) {
            log("Hya from member obj used correctly");
        } else {
            log("Hya from member obj used wrong");
        }
    }
}
