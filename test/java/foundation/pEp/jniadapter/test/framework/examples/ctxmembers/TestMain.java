package foundation.pEp.jniadapter.test.framework.examples.ctxmembers;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.framework.utils.TestUtils;


// Context member object instantiation
//
// In the Context, do not use member objects like this:
//     A a = new A();    // WRONG
// Always, declare them as members and instantiate them in the constructor
// Otherwise, all test contexts in a program partially execute before the test using it is actually being run
// Context init() is part of the test

class CtxMembersTestContext extends AbstractTestContext {
    String name;
    ExampleCtxMember correct;
    ExampleCtxMember incorrect = new ExampleCtxMember(false); // WRONG

    @Override
    public void init() throws Throwable {
        log("=== OUTPUT FROM TEST CONTEXT INIT BEGIN ===");
        log(getTestContextName()+ " - init() called");
        correct = new ExampleCtxMember(true);
        name = "UnitTestFrameWorkWithoutAName";
        log("=== OUTPUT FROM TEST CONTEXT INIT END   ===");
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestUnit test = new TestUnit<CtxMembersTestContext>("ctxmembers",new CtxMembersTestContext() , ctx  -> {
            // do stuff using the context
            // Test FAILS on unhandled exception, otherwise SUCCESS
            log("=== OUTPUT FROM THE TEST ITSELF BEGIN ===");
            log("Hello World from: " + ctx.name);
            log("=== OUTPUT FROM THE TEST ITSELF END   ===");
        });

        TestUtils.sleep(2000);

        test.run();
    }
}



// Just an example member obj
class ExampleCtxMember {
    ExampleCtxMember(boolean correct){
        if(correct) {
            log("Hya from member obj used correctly");
        } else {
            log("Hya from member obj used wrong", TestUtils.TermColor.RED);
        }
    }
}
