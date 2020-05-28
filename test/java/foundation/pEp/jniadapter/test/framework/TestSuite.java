package foundation.pEp.jniadapter.test.framework;
import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import static foundation.pEp.jniadapter.test.framework.TestUtils.TermColor;

import java.util.ArrayList;

public class TestSuite {
    private static ArrayList<TestUnit> tests = new ArrayList<TestUnit>();
    private static boolean verbose = false;

    private TestSuite() { }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean v) {
        verbose = v;
    }

    public static void add(TestUnit t) {
        tests.add(t);
    }

    public static void run() {
        for (TestUnit t : tests) {
            t.setVerboseMode(verbose);
            t.run();
        }
        printStats();
    }

    private static void printStats() {
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
        if(skippedCount > 0 ) failedStr += " ("+skippedCount + " Skipped)";
        log(failedStr, failedColor);
        log("TOTAL  : " + totalCount);
    }
}
