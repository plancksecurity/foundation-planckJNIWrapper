package foundation.pEp.jniadapter.test.framework;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

public class TestUtils {
    private static boolean stdoutHasBeenDisabled = false;
    private static PrintStream origSTDOUT;

    private static boolean stderrHasBeenDisabled = false;
    private static PrintStream origSTDERR;

    public static void standardOutErrDisable(boolean mute) {
        standardOutDisabled(mute);
        standardErrDisabled(mute);
    }

    public static void standardOutDisabled(boolean disable) {
        if (disable) {
            origSTDOUT = System.out;
            stdoutHasBeenDisabled = true;
            System.setOut(new PrintStream(new OutputStream() {
                public void write(int b) {
                    //DO NOTHING
                }
            }));
        } else {
            if (stdoutHasBeenDisabled) {
                System.setOut(origSTDOUT);
            }
        }
    }

    public static void standardErrDisabled(boolean disable) {
        if (disable) {
            origSTDOUT = System.err;
            stderrHasBeenDisabled = true;
            System.setErr(new PrintStream(new OutputStream() {
                public void write(int b) {
                    //DO NOTHING
                }
            }));
        } else {
            if (stderrHasBeenDisabled) {
                System.setErr(origSTDOUT);
            }
        }
    }


    /*
    Time Utils
     */
    public static void sleep(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException ex) {
            System.out.println("sleep got interrupted");
        }
    }

    /*
    String Utils
     */

    public enum Alignment {
        Left(0),
        Center(1),
        Right(2);

        public final int value;

        private static HashMap<Integer, Alignment> intMap;

        private Alignment(int value) {
            this.value = value;
        }

        public static Alignment getByInt(int value) {
            if (intMap == null) {
                intMap = new HashMap<Integer, Alignment>();
                for (Alignment s : Alignment.values()) {
                    intMap.put(s.value, s);
                }
            }
            if (intMap.containsKey(value)) {
                return intMap.get(value);
            }
            return null;
        }
    }

    public static String fixedWidthPaddedString(String str, String padChar, int len, Alignment alignment, String clipMsg) {
        String ret = "";
        String padStr = repeatString(padChar, len);
        padStr = padStr.substring(0, len);
        if (str.length() <= len) {
            if (alignment == Alignment.Left) {
                ret = str + repeatString(padChar, len - str.length());
            }
            if (alignment == Alignment.Right) {
                ret = repeatString(padChar, len - str.length()) + str;
            }
            if (alignment == Alignment.Center) {
                int padAmt = len - str.length();
                String pad = repeatString(padChar, (int) Math.ceil(padAmt / 2) + 1);
                ret = pad + str + pad;
                ret = clipString(ret, len, null);
            }
        } else {
            ret = clipString(str, len, clipMsg);
        }
        return ret;
    }

    public static String repeatString(String str, int times) {
        String ret = "";
        for (int i = 0; i < times; i++) {
            ret += str;
        }
        return ret;
    }

    public static String clipString(String str, int len, String clipMsg) {
        String ret = str;
        if (str.length() > len) {
            int effSpaceAvail = len; // max
            if (clipMsg != null) {
                effSpaceAvail = len - clipMsg.length();
                if (effSpaceAvail <= 0) {
                    clipMsg = null;
                    effSpaceAvail = len;
                }
                if (clipMsg.length() == 0) {
                    clipMsg = null;
                }
            }

            ret = str.substring(0, effSpaceAvail);
            if (clipMsg != null) {
                ret += clipMsg;
            }
        }
        return ret;
    }
}

