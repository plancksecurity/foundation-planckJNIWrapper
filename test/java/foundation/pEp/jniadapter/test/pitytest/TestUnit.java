package foundation.pEp.jniadapter.test.pitytest;

import foundation.pEp.jniadapter.test.pitytest.utils.TestUtils;

import static foundation.pEp.jniadapter.test.pitytest.TestLogger.*;
import static foundation.pEp.jniadapter.test.pitytest.utils.TestUtils.TermColor;
import static foundation.pEp.jniadapter.test.pitytest.utils.TestUtils.colorString;

import java.util.function.Consumer;

//Automatically get added to the default TestSuite always
//Can be added to any nr of TestSuites

public class TestUnit<T extends TestContextInterface> implements Runnable {
    private String testUnitName = "default test unit";
    private T ctx;
    private Consumer<T> lambda;

    private TestResult result = TestResult.UNEVALUATED;
    private TestState state = TestState.UNEVALUATED;
    private Throwable lastException;

    private boolean verboseMode = true;
    private TermColor testColor = TermColor.CYAN;

    // Defaults (line width 80)
    private int logFmtTestNameLen = 35;
    private int logFmtCtxNameLen = 25;
    private int logFmtMsgLen = 12;

    public TestUnit(String name, T context, Consumer<T> lambda) {
        this.testUnitName = name;
        this.lambda = lambda;
        this.ctx = context;
        logLayout();
        add(TestSuite.getDefault());
    }

    public boolean isVerboseMode() {
        return verboseMode;
    }

    public void setVerboseMode(boolean verboseMode) {
        this.verboseMode = verboseMode;
    }

    public TermColor getTestColor() {
        return testColor;
    }

    public void setTestColor(TermColor testColor) {
        this.testColor = testColor;
    }

    public TestResult getResult() {
        return result;
    }

    public Throwable getLastException() {
        return lastException;
    }

    public TestUnit<T> add(TestSuite suite) {
        suite.add(this);
        return this;
    }

    public TestUnit<T> add() {
        TestSuite.getDefault().add(this);
        return this;
    }

    public void run() {
        TestUtils.standardOutErrEnabled(verboseMode);
        if (ctx.isUninitializable()) {
            setTestState(TestState.CTX_INIT_FAILED);
            TestUtils.standardOutErrEnabled(true);
            return;
        }
        try {
            setTestState(TestState.STARTING);
            // Init the Context if not already done
            if (!ctx.isInitialized()) {
                //Context init problems need to throw to fail
                try {
                    setTestState(TestState.CTX_INIT);
                    setTermColor(testColor);
                    ctx.init();
                    setTermColor(TermColor.RESET);
                } catch (Throwable t) {
                    lastException = t;
                    setTermColor(TermColor.RESET);
                    setTestState(TestState.CTX_INIT_FAILED);
                    TestUtils.standardOutErrEnabled(true);
                    return;
                }
                ctx.setInitialized(true);
            }
            //tests need to throw to fail
            setTestState(TestState.RUNNING);
            setTermColor(testColor);
            lambda.accept(ctx);
            setTermColor(TermColor.RESET);
            setTestState(TestState.SUCCESS);
        } catch (Throwable t) {
            lastException = t;
            setTermColor(TermColor.RESET);
            setTestState(TestState.FAILED);
            TestUtils.standardOutErrEnabled(true);
            return;
        }
        setTermColor(TermColor.RESET);
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
        String resultStr = r.toString();

        if(r != TestResult.SUCCESS) {
            resultStr = colorString(resultStr, TermColor.RED);
        } else {
            resultStr = colorString(resultStr, TermColor.GREEN);
        }

        TestUtils.standardOutErrEnabled(true);
        logH1(makeLogString(resultStr));
        if( r != TestResult.SUCCESS) {
            log("ERROR: " + getLastException().toString());
        }
        if (verboseMode) logRaw("\n\n");
        TestUtils.standardOutErrEnabled(verboseMode);
    }

    private void logLayout() {
        logFmtTestNameLen = (int) Math.floor(TestLogger.getMsgWidth() * 0.39);
        logFmtCtxNameLen = (int) Math.floor(TestLogger.getMsgWidth() * 0.28);
        logFmtMsgLen = (int) Math.floor(TestLogger.getMsgWidth() * 0.25);
    }

    private String makeLogString(String str) {
        String testUnitNameFmtd = TestUtils.padOrClipString(" TEST: '" + testUnitName + "' ", "=", logFmtTestNameLen, TestUtils.Alignment.Left, ".. ");
        String testCtxNameFmtd = TestUtils.padOrClipString(" CTX: '" + ctx.getTestContextName() + "' ", "=", logFmtCtxNameLen, TestUtils.Alignment.Center, ".. ");
        String strFmtd = TestUtils.padOrClipString(" " + str + " ", "=", logFmtMsgLen, TestUtils.Alignment.Right, ".. ");
        return testUnitNameFmtd + testCtxNameFmtd + strFmtd;
    }
}

