package foundation.pEp.jniadapter.test.framework;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import java.util.function.Consumer;

public class TestUnit<T extends TestContextInterface> implements Runnable {
    private String testUnitName = "default test unit";
    private T ctx;
    private Consumer<T> lambda;

    private TestResult result = TestResult.UNEVALUATED;
    private TestState state = TestState.UNEVALUATED;
    private Throwable lastException;

    private boolean verboseMode = true;

    // Defaults (line width 80)
    private int logFmtTestNameLen = 35;
    private int logFmtCtxNameLen = 25;
    private int logFmtMsgLen = 12;

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

    public TestResult getResult() {
        return result;
    }

    public Throwable getLastException() {
        return lastException;
    }

    public TestUnit<T> add() {
        TestSuite.add(this);
        return this;
    }

    public void run() {
        TestUtils.standardOutErrEnabled(verboseMode);
        if (ctx.isUninitializable()) {
            setTestState(TestState.CTX_INIT_FAILED);
            return;
        }
        try {
            setTestState(TestState.STARTING);
            // Init the Context if not already done
            if (!ctx.isInitialized()) {
                //Context init problems need to throw to fail
                try {
                    setTestState(TestState.CTX_INIT);
                    ctx.init();
                } catch (Throwable t) {
                    lastException = t;
                    setTestState(TestState.CTX_INIT_FAILED);
                    return;
                }
                ctx.setInitialized(true);
            }
            //tests need to throw to fail
            setTestState(TestState.RUNNING);
            lambda.accept(ctx);
            setTestState(TestState.SUCCESS);
        } catch (Throwable t) {
            lastException = t;
            setTestState(TestState.FAILED);
            return;
        }
        TestUtils.standardOutErrEnabled(true);
    }

    private void setTestState(TestState s) {
        state = s;

        switch (state) {
            case UNEVALUATED: {
                setTestResult(TestResult.UNEVALUATED);
                break;
            }
            case SKIPPED: {
                setTestResult(TestResult.SKIPPED);
                break;
            }
            case SUCCESS: {
                setTestResult(TestResult.SUCCESS);
                break;
            }
            case FAILED: {
                setTestResult(TestResult.FAILED);
                break;
            }
            case STARTING:
            case CTX_INIT:
            case RUNNING: {
                logH1(makeLogString(state.toString()));
                break;
            }
            case CTX_INIT_FAILED: {
                logH1(makeLogString(state.toString()));
                setTestResult(TestResult.SKIPPED);
                break;
            }
        }
    }

    private void setTestResult(TestResult r) {
        result = r;
        TestUtils.standardOutErrEnabled(true);
        logH1(makeLogString(result.toString()));
        if( r != TestResult.SUCCESS) {
            log("ERROR: " + getLastException().toString());
        }
        if (verboseMode) logRaw("\n\n");
        TestUtils.standardOutErrEnabled(verboseMode);
    }

    private void logLayout() {
        logFmtTestNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.39);
        logFmtCtxNameLen = (int) Math.floor(TestLogger.getLineWidth() * 0.28);
        logFmtMsgLen = (int) Math.floor(TestLogger.getLineWidth() * 0.25);
    }

    private String makeLogString(String str) {
        String testUnitNameFmtd = TestUtils.fixedWidthPaddedString(" TEST: '" + testUnitName + "' ", "=", logFmtTestNameLen, TestUtils.Alignment.Left, ".. ");
        String testCtxNameFmtd = TestUtils.fixedWidthPaddedString(" CTX: '" + ctx.getTestContextName() + "' ", "=", logFmtCtxNameLen, TestUtils.Alignment.Center, ".. ");
        String strFmtd = TestUtils.fixedWidthPaddedString(" " + str + " ", "=", logFmtMsgLen, TestUtils.Alignment.Right, ".. ");
        return testUnitNameFmtd + testCtxNameFmtd + strFmtd;
    }
}

