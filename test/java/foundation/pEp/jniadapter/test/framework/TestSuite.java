package foundation.pEp.jniadapter.test.framework;

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
    }
}
