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
    private String logFormat = "";

    public boolean isVerboseMode() {
        return verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public TestUnit(String name, T context, Consumer<T> lambda) {
        this.testUnitName = name;
        this.lambda = lambda;
        this.ctx = context;

        logFmtTestNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.45);
        logFmtCtxNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.3);
        logFmtMsgLen = (int) Math.floor(TestLogger.getLineWidth() * 0.2);
    }

    public void run() {
        if (ctx.isUninitializable()) {
            logH1("Skipping, context has been uninitializable");
        } else {

            // Init the Context if not already done
            if (!ctx.isInitialized()) {
                try {
                    if (!verboseMode) TestUtils.standardOutErrDisable(true);
                    logH1(logString("CTX INIT"));
                    ctx.init();
                    ctx.setInitialized(true);
                    if (!verboseMode) TestUtils.standardOutErrDisable(false);
                } catch (Throwable t) {
                    //Context init problems need to throw to fail
                    if (!verboseMode) TestUtils.standardOutErrDisable(false);
                    ctx.setUninitializable(true);
                    logH1(logString("CTX FAIL"));
                    log(t.toString());
                    logRaw("\n");
                    return;
                }
            }

            // Run the test
            try {
                if (!verboseMode) TestUtils.standardOutErrDisable(true);
                logH1(logString("STARTING"));
                lambda.accept(ctx);
                if (!verboseMode) TestUtils.standardOutErrDisable(false);
                logH1(logString("SUCCESS"));
                if (verboseMode) logRaw("\n\n");
            } catch (Throwable t) {
                //Test fails, upon cought exception, otherwise succeeds
                if (!verboseMode) TestUtils.standardOutErrDisable(false);
                logH1(logString("FAILED"));
                log(t.toString());
                if (verboseMode) logRaw("\n\n");
                return;
            }
        }
    }

    private String logString(String str) {
        String testUnitNameFmtd = TestUtils.fixedWidthPaddedString(" TEST: '" + testUnitName + "' ", "=", logFmtTestNameLen, TestUtils.Alignment.Left, ".. ");
        String testCtxNameFmtd = TestUtils.fixedWidthPaddedString(" CTX: '" + ctx.getTestContextName() + "' ", "=", logFmtCtxNameLen, TestUtils.Alignment.Center, ".. ");
        String strFmtd = TestUtils.fixedWidthPaddedString(" " + str + " ", "=", logFmtMsgLen, TestUtils.Alignment.Right, ".. ");
        return testUnitNameFmtd + testCtxNameFmtd +  strFmtd;
    }


}
