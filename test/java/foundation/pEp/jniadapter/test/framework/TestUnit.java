package foundation.pEp.jniadapter.test.framework;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import java.util.function.Consumer;

public class TestUnit<T extends TestContextInterface> implements Runnable {
    String testUnitName = "default test unit";
    T ctx;
    Consumer<T> lambda;

    public TestUnit(String name, T context, Consumer<T> lambda) {
        this.testUnitName = name;
        this.lambda = lambda;
        this.ctx = context;
    }

    public void run() {
        if (ctx.isUninitializable()) {
            // Context uninitializable
            log("Context has been uninitializable");
        } else {
            // Init the Context
            try {
                if (!ctx.isInitialized()) {
                    logH1("TEST: '" + testUnitName + "' ==== CTX: '" + ctx.getTestContextName() + "' ===== CTX INIT");
                    ctx.init();
                    ctx.setInitialized(true);
                }
            } catch (Throwable t) {
                //Context Init problems need to throw for fail
                logH1("TEST: '" + testUnitName + "' ==== CTX: '" + ctx.getTestContextName() + "' ===== CTX FAIL");
                log(t.toString());
                spacer();
                ctx.setUninitializable(true);
                return;
            }

            // Run the test
            logH1("TEST: '" + testUnitName + "' ==== CTX: '" + ctx.getTestContextName() + "' ===== STARTING");
            try {
                lambda.accept(ctx);
            } catch (Throwable t) {
                //Test fails, upon cought exception, otherwise succeeds
                logH1("TEST: '" + testUnitName + "' ==== CTX: '" + ctx.getTestContextName() + "' ===== FAILED");
                log(t.toString());
                spacer();
                return;
            }
            logH1("TEST: '" + testUnitName + "' ==== CTX: '" + ctx.getTestContextName() + "' ===== SUCCESS");
            spacer();
        }
    }
}
