//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package foundation.pEp.jniadapter.test.framework;

public class TestLogger {
    private static boolean logEnabled = true;

    public TestLogger() {
    }

    public static void setLoggingEnabled(boolean enabled) {
        logEnabled = enabled;
    }

    public static boolean isLoggingEnabled() {
        return logEnabled;
    }

    public static void log(String msg) {
        if (logEnabled) {
            String threadStr = String.format("%-10s", Thread.currentThread().getName());
            String logStr = threadStr + ": " + msg;
            System.out.println(logStr);
        }

    }

    public static void logH1(String msg) {
        log(getDecoratedString(msg, "="));
    }

    public static void logH2(String msg) {
        log(getDecoratedString(msg, "-"));
    }

    private static String getDecoratedString(String msg, String decoration) {
        byte var2 = 80;
        String ret = "";

        for(int i = 0; (double)i < Math.ceil((double)((var2 - msg.length() + 2) / 2)); ++i) {
            ret = ret + decoration;
        }

        return ret + " " + msg + " " + ret;
    }

    public static void logSectEnd(String msg) {
        log(msg + "\n");
    }
}
