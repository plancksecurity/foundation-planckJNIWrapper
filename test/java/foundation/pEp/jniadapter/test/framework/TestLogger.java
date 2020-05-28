package foundation.pEp.jniadapter.test.framework;

import static foundation.pEp.jniadapter.test.framework.TestUtils.TermColor;

public class TestLogger {
    // options
    private static boolean logEnabled = true;
    private static int lineWidth = 80;

    // constants
    private static int threadStrLen = 10;

    public static void setLoggingEnabled(boolean enabled) {
        logEnabled = enabled;
    }

    public static boolean isLoggingEnabled() {
        return logEnabled;
    }

    public static int getLineWidth() {
        return lineWidth;
    }

    public static void setLineWidth(int lineWidth) {
        TestLogger.lineWidth = lineWidth;
    }

    // Log
    public static void log(String msg) {
        if (logEnabled) {
            String indent = "";
            String separator = ": ";
            int indentStrLen = threadStrLen + separator.length();
            String threadStr = String.format("%-" + threadStrLen + "s", Thread.currentThread().getName());
            indent = String.format("%" + indentStrLen + "s", " ");
            msg = msg.replace("\n", "\n" + indent);
            String logStr = threadStr + separator + msg;
            System.out.println(logStr);
        }
    }

    public static void log(String msg, TermColor color) {
        setTermColor(color);
        log(msg);
        setTermColor(TermColor.RESET);
    }

    // LogH1
    public static void logH1(String msg) {
        log(TestUtils.padOrClipString(msg, "=", lineWidth, TestUtils.Alignment.Center, null));
    }

    public static void logH1(String msg, TermColor color) {
        setTermColor(color);
        logH1(msg);
        setTermColor(TermColor.RESET);
    }

    // LogH2
    public static void logH2(String msg) {
        log(TestUtils.padOrClipString(msg, "-", lineWidth, TestUtils.Alignment.Center, null));
    }

    public static void logH2(String msg, TermColor color) {
        setTermColor(color);
        logH2(msg);
        setTermColor(TermColor.RESET);
    }

    // LogRaw
    public static void logRaw(String msg) {
        System.out.print(msg);
    }

    public static void logRaw(String msg, TermColor color) {
        setTermColor(color);
        logRaw(msg);
        setTermColor(TermColor.RESET);
    }

    public static void setTermColor(TermColor c) {
        logRaw(c.toString());
    }

}


