package foundation.pEp.jniadapter.test.framework;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import java.util.function.Consumer;

public class TestUnit<T extends TestContextInterface> implements Runnable {
    private String testUnitName = "default test unit";
    private T ctx;
    private Consumer<T> lambda;
    private boolean verboseMode = true;

    // Defaults (line width 80)
    private int logFmtTestNameLen = 35;
    private int logFmtCtxNameLen = 24;
    private int logFmtMsgLen = 8;

    public TestUnit(String name, T context, Consumer<T> lambda) {
        this.testUnitName = name;
        this.lambda = lambda;
        this.ctx = context;
        logLayout();
    }

    public boolean isVerboseMode() {
        return verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public TestUnit<T> add() {
        TestSuite.add(this);
        return this;
    }

    public void run() {
        if (!verboseMode) TestUtils.standardOutErrDisable(true);
        try {
            if (!ctx.isUninitializable()) {
                // Init the Context if not already done
                if (!ctx.isInitialized()) {
                    //Context init problems need to throw to fail
                    try {
                        ctxInit();
                    } catch (Throwable t) {
                        ctx.setUninitializable(true);
                        throw new TestFrameWorkContextInitFailedException();
                    }
                    ctx.setInitialized(true);
                }
                //tests need to throw to fail
                runTest();
                if (!verboseMode) TestUtils.standardOutErrDisable(false);
                testSuceeded();
            } else {
                throw new TestFrameWorkContextUnitializableException();
            }
        } catch (Throwable t) {
            if (!verboseMode) TestUtils.standardOutErrDisable(false);
            testFailed(t);
            return;
        }
    }

    private void ctxInit() throws Throwable{
        logH1(makeLogString("CTX INIT"));
        ctx.init();
    }

    private void runTest() throws Throwable{
        logH1(makeLogString("STARTING"));
        //tests need to throw to fail
        lambda.accept(ctx);
    }

    private void testSuceeded() {
        logH1(makeLogString("SUCCESS"));
        if (verboseMode) logRaw("\n\n");
    }

    private void testFailed(Throwable t) {
        logH1(makeLogString("FAILED"));
        log("ERROR: " + t.toString());
        if (verboseMode)  logRaw("\n\n");
    }

    private void logLayout() {
        logFmtTestNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.45);
        logFmtCtxNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.3);
        logFmtMsgLen = (int) Math.floor(TestLogger.getLineWidth() * 0.2);
    }

    private String makeLogString(String str) {
        String testUnitNameFmtd = TestUtils.fixedWidthPaddedString(" TEST: '" + testUnitName + "' ", "=", logFmtTestNameLen, TestUtils.Alignment.Left, ".. ");
        String testCtxNameFmtd = TestUtils.fixedWidthPaddedString(" CTX: '" + ctx.getTestContextName() + "' ", "=", logFmtCtxNameLen, TestUtils.Alignment.Center, ".. ");
        String strFmtd = TestUtils.fixedWidthPaddedString(" " + str + " ", "=", logFmtMsgLen, TestUtils.Alignment.Right, ".. ");
        return testUnitNameFmtd + testCtxNameFmtd + strFmtd;
    }
}

class TestFrameWorkContextInitFailedException extends Exception {

}

class TestFrameWorkContextUnitializableException extends Exception {

}