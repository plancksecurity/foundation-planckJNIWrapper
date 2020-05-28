package foundation.pEp.jniadapter.test.framework;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import static foundation.pEp.jniadapter.test.framework.TestLogger.log;

public class TestUtils {
    private static boolean stdoutHasBeenDisabled = false;
    private static PrintStream origSTDOUT;

    private static boolean stderrHasBeenDisabled = false;
    private static PrintStream origSTDERR;

    public static void standardOutErrEnabled(boolean mute) {
        standardOutEnabled(mute);
        standardErrEnabled(mute);
    }

    public static void standardOutEnabled(boolean enable) {
        if (!enable) {
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

    public static void standardErrEnabled(boolean enable) {
        if (!enable) {
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

    public static String padOrClipString(String str, String padChar, int len, Alignment alignment, String clipMsg) {
        String ret = "";
        int strLen = str.length();
        len += (substringOccurencesCount(str, "\u001B") * 4);
        if (strLen <= len) {
            if (alignment == Alignment.Left) {
                ret = str + repeatString(padChar, len - strLen);
            }
            if (alignment == Alignment.Right) {
                ret = repeatString(padChar, len - strLen) + str;
            }
            if (alignment == Alignment.Center) {
                int padAmt = len - strLen;
                String pad = repeatString(padChar, (int) Math.ceil(padAmt / 2) + 1);
                ret = pad + str + pad;
                ret = clipString(ret, len, null);
            }
        } else {
            ret = clipString(str, len, clipMsg);
        }
        return ret;
    }

    public static int stringLenMinusEscSeq(String str) {
        int ret;
        int escSeqCount = substringOccurencesCount(str, "\u001B[");
        ret = str.length() -  (escSeqCount * 3);
        if(ret < 0) ret = 0;
        return ret;
    }

    public static int substringOccurencesCount(String str, String substring) {
        int ret = 0;
        int fromIndex = 0;

        while ((fromIndex = str.indexOf(substring, fromIndex)) != -1) {
            ret++;
            fromIndex++;
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

    public static String colorString(String str, TermColor color) {
        return color.toString() + str + TermColor.RESET;
    }

    public enum TermColor {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private final String text;

        TermColor(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}

