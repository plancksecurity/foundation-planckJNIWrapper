package foundation.pEp.pitytest;

import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.*;
import static foundation.pEp.pitytest.utils.TestUtils.*;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.function.Consumer;

// Automatically gets added to the default TestSuite always
// Can be added to any nr of TestSuites

public class TestUnit<T extends TestContextInterface> implements Runnable {
    private String testUnitName = "default test unit";
    private T ctx;
    private Consumer<T> lambda;

    private TestState result = TestState.UNEVALUATED;
    private TestState state = TestState.UNEVALUATED;
    private Throwable lastException;
    private Duration testDuration = null;

    private boolean verboseMode = true;
    private TermColor testColor = TermColor.CYAN;

    // Defaults (line width 80)
    // fixed width
    private int logFmtPadding = 4;
    private int logFmtMsgLen = 12;
    private int logFmtTestDuration = 14;
    private int lineWidthMin = logFmtPadding + logFmtMsgLen + logFmtTestDuration + 20;

    // dynamic
    private int logFmtTestNameLen = 32;
    private int logFmtCtxNameLen = 20;

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

    public TestState getResult() {
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
            setTestState(TestState.CTX_FAIL);
            TestUtils.standardOutErrEnabled(true);
        } else {
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
                        ctx.setUninitializable(true);
                        setTestState(TestState.CTX_FAIL);
                        TestUtils.standardOutErrEnabled(true);
                        return;
                    }
                    ctx.setInitialized(true);
                }
                //tests need to throw to fail
                setTestState(TestState.RUNNING);
                setTermColor(testColor);
                testDuration = new StopWatch(() -> {
                    lambda.accept(ctx);
                }).getDuration();
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
    }

    private void setTestState(TestState s) {
        state = s;

        switch (state) {
            case UNEVALUATED:
            case SKIPPED:
            case SUCCESS:
            case FAILED: {
                setTestResult(s);
                break;
            }
            case CTX_INIT:
            case STARTING:
            case RUNNING: {
                logH1(makeLogString());
                break;
            }
            case CTX_FAIL: {
                setTestResult(TestState.SKIPPED);
//                logH1(makeLogString());
                break;
            }
        }
    }

    private void setTestResult(TestState r) {
        assert (r == TestState.SKIPPED || r == TestState.FAILED || r == TestState.SUCCESS || r == TestState.UNEVALUATED ): "PityTest Internal: illegal result value '" + r +"'";
        result = r;
        TestUtils.standardOutErrEnabled(true);
        logH1(makeLogString());
        if (result == TestState.FAILED || result == TestState.CTX_FAIL) {
            log("ERROR: " + getLastException().toString());
        }
        if (verboseMode) logRaw("\n\n");
        TestUtils.standardOutErrEnabled(verboseMode);
    }

    private void logLayout() {
        int lineWidth = TestLogger.getMsgWidth();
        // Fixed sizes
        lineWidth -= logFmtPadding;
        lineWidth -= logFmtMsgLen;
        lineWidth -= logFmtTestDuration;
        lineWidth = clip(lineWidth, lineWidthMin, Integer.MAX_VALUE);

        // Proportional (dynamic sizes)
        logFmtTestNameLen = (int) Math.floor(lineWidth * 0.45);
        logFmtCtxNameLen = (int) Math.floor(lineWidth * 0.45);
    }

    private String makeLogString() {
        String resultStr = state.toString();
        if (state == TestState.FAILED) {
            resultStr = colorString(resultStr, TermColor.RED);
        } else if (state == TestState.SUCCESS) {
            resultStr = colorString(resultStr, TermColor.GREEN);
        }

        String testUnitNameFmtd = TestUtils.padOrClipString(" TEST: '" + testUnitName + "' ", "=", logFmtTestNameLen, TestUtils.Alignment.Left, ".. ");
        String testCtxNameFmtd = TestUtils.padOrClipString(" CTX: '" + ctx.getTestContextName() + "' ", "=", logFmtCtxNameLen, TestUtils.Alignment.Center, ".. ");
        String strTestDuration = "";
        if (state == TestState.SUCCESS) {
            DecimalFormat f = new DecimalFormat("0.000");
            String durationFmtd = f.format(testDuration.toMillis() / 1000.0);
            strTestDuration = TestUtils.padOrClipString(" [" + durationFmtd + " sec] ", "=", logFmtTestDuration, TestUtils.Alignment.Right, ".. ");
        } else  {
            strTestDuration = TestUtils.padOrClipString("", "=", logFmtTestDuration, TestUtils.Alignment.Right, ".. ");
        }

        String strFmtd = TestUtils.padOrClipString(" " + resultStr + " ", "=", logFmtMsgLen, TestUtils.Alignment.Right, ".. ");
        return testUnitNameFmtd + testCtxNameFmtd + strTestDuration + strFmtd;
    }
}

