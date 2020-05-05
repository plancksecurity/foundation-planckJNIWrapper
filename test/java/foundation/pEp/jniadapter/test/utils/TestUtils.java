package foundation.pEp.jniadapter.test.utils;
import foundation.pEp.jniadapter.*;

public class TestUtils {
    public static void sleep(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException ex) {
            System.out.println("sleep got interrupted");
        }
    }

    public static String identityToString(Identity i) {
        String ret = "address: " + i.address + "\n";
        ret += "fpr: " + i.fpr + "\n";
        ret += "username: " + i.username + "\n";
        ret += "user_id: " + i.user_id + "\n";
        ret += "flags: " + i.flags + "\n";
        ret += "lang: " + i.lang + "\n";
        ret += "me: " + i.me + "\n";
        ret += "comm_type: " + i.comm_type;
        return ret;
    }

    // ------------------------ Logging ------------------------

    private static boolean logEnabled = true;

    public static void setLoggingEnabled(boolean enabled) {
        logEnabled = enabled;
    }

    public static boolean isLoggingEnabled() {
        return logEnabled;
    }

    public static void log(String msg) {
        if(logEnabled) {
            String threadNameFmt = String.format("%-10s", Thread.currentThread().getName());
            String msgOut = threadNameFmt + ": " + msg;
            System.out.println(msgOut);
        }
    }

    public static void logH1(String msg) {
        String decorationStr = getDecoratorString(msg, "=");
        log(decorationStr + " " + msg.toUpperCase() + " " + decorationStr);
    }

    public static void logH2(String msg) {
        String decorationStr = getDecoratorString(msg, "-");
        log(decorationStr + " " + msg + " " + decorationStr);
    }

    private static String getDecoratorString(String msg, String s) {
        int lineWidth = 80;
        String decorationChar = s;
        String decorationStr = "";
        for (int i = 0; i < Math.ceil((lineWidth - msg.length() + 2) / 2); i++) {
            decorationStr += decorationChar;
        }
        return decorationStr;
    }

    public static void logSectEnd(String msg) {
        log(msg + "\n");
    }
}