package foundation.pEp.jniadapter.test.framework;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import static foundation.pEp.jniadapter.test.framework.utils.TestUtils.TermColor;
import static foundation.pEp.jniadapter.test.framework.utils.TestUtils.*;

import java.util.ArrayList;

// There is a static defaultTestSuite
// The last created instance is the defaultTestSuite by default
// Env var: TFVERBOSE

public class TestSuite {
    private static TestSuite defaultTestSuite = null;
    private ArrayList<TestUnit> tests = new ArrayList<TestUnit>();
    private boolean verboseMode = false;
    private boolean verboseModeSetByEnv = false;
    private TermColor testColor = TermColor.CYAN;
    private String EVNVARNAME_VERBOSE = "TFVERBOSE";

    public TestSuite() {
        setDefault();
        init();
    }

    public TestSuite(boolean makeDefault) {
        if (makeDefault) {
            setDefault();
        }
        init();
    }

    private void init() {
        try {
            setVerbose(getEnvVerbose());
            verboseModeSetByEnv = true;
        } catch(RuntimeException e) {
            // Env var not set
            verboseModeSetByEnv = false;
        }
    }

    public void setDefault() {
        defaultTestSuite = this;
    }

    public static TestSuite getDefault() {
        if (defaultTestSuite == null) {
            defaultTestSuite = new TestSuite(true);
        }
        return defaultTestSuite;
    }

    public boolean isDefault() {
        return getDefault() == this;
    }

    public boolean isVerbose() {
        return verboseMode;
    }

    // Will only set if not already set in env
    public void setVerbose(boolean v) {
        if(!verboseModeSetByEnv) {
            verboseMode = v;
        }
    }

    public TermColor getTestColor() {
        return testColor;
    }

    public void setTestColor(TermColor color) {
        testColor = color;
    }

    public void add(TestUnit t) {
        tests.add(t);
    }

    public void run() {
        for (TestUnit t : tests) {
            t.setVerboseMode(verboseMode);
            t.setTestColor(testColor);
            t.run();
        }
        printStats();
    }

    private boolean getEnvVerbose() throws RuntimeException {
        boolean ret = Boolean.parseBoolean(getEnvVar(EVNVARNAME_VERBOSE));
        return ret;
    }

    private void printStats() {
        int totalCount = tests.size();
        int skippedCount = 0;
        int failedCount = 0;
        int successCount = 0;
        for (TestUnit t : tests) {
            if (t.getResult() == TestResult.SKIPPED) skippedCount++;
            if (t.getResult() == TestResult.FAILED) failedCount++;
            if (t.getResult() == TestResult.SUCCESS) successCount++;
        }

        failedCount = failedCount + skippedCount;

        log("SUCCESS: " + successCount, TermColor.GREEN);
        TermColor failedColor = TermColor.RED;
        if (failedCount <= 0) {
            failedColor = TermColor.RESET;
        }
        String failedStr = "FAILED : " + failedCount;
        if (skippedCount > 0) failedStr += " (" + skippedCount + " Skipped)";
        log(failedStr, failedColor);
        log("TOTAL  : " + totalCount);
    }
}
