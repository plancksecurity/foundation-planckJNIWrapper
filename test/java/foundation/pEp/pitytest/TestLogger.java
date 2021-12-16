package foundation.pEp.pitytest;

import foundation.pEp.pitytest.utils.TestUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static foundation.pEp.pitytest.utils.TestUtils.*;

public class TestLogger {
    static {
        init();
    }

    // options
    private static boolean logEnabled;
    private static int lineWidth;

    // constants
    private static int threadStrLen;
    private static String threadSeparator;
    private static boolean initialized;

    private static void init() {
        if (!initialized) {
            logEnabled = true;
            threadStrLen = 10;
            lineWidth = 120;
            threadSeparator = ": ";

// TODO: Disabled term size detection, not working anymore on macOS at least.... grmpl
//            tryDetermineTermSize();
            initialized = true;
        }
    }

    private static void tryDetermineTermSize() {
        int nrCols = lineWidth;
        try {
            Process p = Runtime.getRuntime().exec("tput cols");
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String cmdOutput = "";
            String buf = null;
            while ((buf = stdInput.readLine()) != null) {
                cmdOutput += buf;
            }

            nrCols = Integer.valueOf(cmdOutput);
            setLineWidth(clip(nrCols, 40, 2000));

        } catch (Exception e) {
            // something went wrong
        }
    }


    public static void setLoggingEnabled(boolean enabled) {
        logEnabled = enabled;
    }

    public static boolean isLoggingEnabled() {
        return logEnabled;
    }

    public static int getLineWidth() {
        return lineWidth;
    }

    public static int getMsgWidth() {
        return lineWidth - threadStrLen - threadSeparator.length();
    }

    public static void setLineWidth(int width) {
        lineWidth = width;
    }

    // Log
    public static void log(String msg) {
        if (logEnabled) {
            String threadStr = padOrClipString(Thread.currentThread().getName(), " ", threadStrLen, Alignment.Left, "");
            int indentStrLen = threadStrLen + threadSeparator.length();
            String indent = repeatString(" ", indentStrLen);
            msg = msg.replace("\n", "\n" + indent);
            String logStr = threadStr + threadSeparator + msg;
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
        log(TestUtils.padOrClipString(msg, "=", lineWidth - threadSeparator.length() - threadStrLen, TestUtils.Alignment.Center, null));
    }

    public static void logH1(String msg, TermColor color) {
        setTermColor(color);
        logH1(msg);
        setTermColor(TermColor.RESET);
    }

    // LogH2
    public static void logH2(String msg) {
        log(TestUtils.padOrClipString(msg, "-", lineWidth - threadSeparator.length() - threadStrLen, TestUtils.Alignment.Center, null));
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


