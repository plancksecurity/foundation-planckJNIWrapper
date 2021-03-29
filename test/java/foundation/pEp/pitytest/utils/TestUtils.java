package foundation.pEp.pitytest.utils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//import static foundation.pEp.pitytest.TestLogger.log;

public class TestUtils {
    // Pure static class
    private TestUtils() {
    }

    /*
    System
     */
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

    // Throws if var not defined in env
    public static String getEnvVar(String name) throws RuntimeException {
        String ret = null;
        if (envVarExists(name)) {
            ret = System.getenv(name);
        } else {
            throw new RuntimeException("ENV var does not exist");
        }
        return ret;
    }

    public static boolean envVarExists(String var) {
        boolean ret = false;
        if (System.getenv(var) != null) {
            ret = true;
        }
        return ret;
    }


    public static String readKey() {
        String ret = null;
        ret = System.console().readLine();
        return ret;
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
        len += (substringOccurencesCount(str, "\u001B") * 4.5);
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
        ret = str.length() - (escSeqCount * 3);
        if (ret < 0) ret = 0;
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



    /*
    FSUtils
     */

    // possibly returns an empty list
    public static List<File> filterbyFilename(List<File> files, String regex) {
        List<File> ret = null;
        Predicate<File> dotMsg = file -> file.getName().matches(regex);
        ret = files.stream().filter(dotMsg).collect(Collectors.toList());
        return ret;
    }

    // Possibly returns an empty ArrayList
    public static List<File> listFilesByMtime(File dir) {
        List<File> ret = new ArrayList<>();
        File[] listOfFiles = dir.listFiles();
        if (listOfFiles != null) {
            Collections.addAll(ret, listOfFiles);
            ret = sortFilesByMtime(ret);
        }
        return ret;
    }

    // null in null out
    private static List<File> sortFilesByMtime(List<File> files) {
        List<File> ret = null;
        if (files != null) {
            ret = new ArrayList(files);
            Collections.sort(ret, (o1, o2) -> {
                long ret1 = 0;
                ret1 = o1.lastModified() - o2.lastModified();
                return (int) clip(ret1, -1, 1);
            });
        }
        return ret;
    }

    public static String readFile(Path path, Charset decoding) throws IOException {
        String ret = null;
        byte[] encoded = Files.readAllBytes(path);
        ret = new String(encoded, decoding);
        if (ret == null) {
            throw new IOException("Error reading file: " + path);
        }
        return ret;
    }

    public static void writeFile(Path path, String msg, Charset encoding) throws IOException {
        Files.write(path, msg.getBytes(encoding));
    }

    public static boolean deleteRecursively(File dir) {
        deleteContentsRecursively(dir);
//        log("deleting: " + dir.getAbsolutePath());
        return dir.delete();
    }

    public static boolean deleteContentsRecursively(File dir) {
        boolean ret = false;
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                ret = deleteRecursively(file);
            }
        }
        return ret;
    }

    public static List<String> getAvailableCharsetNames() {
        List<String> ret = new ArrayList<>();
        for (String key : Charset.availableCharsets().keySet()) {
            Charset val = Charset.forName(key);
            ret.add(val.name());
        }
        return ret;
    }



    /*
    Math Utils
     */

    private static Random rand = new Random();

    public static int clip(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static long clip(long val, long min, long max) {
        return Math.max(min, Math.min(max, val));
    }

    /*
    Random data generators
     */

    public static int randomInt(RangeInt range) {
        int ret = 0;
            ret = rand.nextInt(range.getSize()) + range.effectiveMin();
        return ret;
    }


    public static String randomASCIIString(EASCIICharClassName charClass, int len) {
        byte[] array = new byte[len]; // length is bounded by 7
        int rangeMin = 0;
        int rangeMax = 0;

        switch (charClass) {
            case All: {
                rangeMin = 0;
                rangeMax = 255;
                break;
            }
            case Alpha: {
                rangeMin = 65;
                rangeMax = 122;
                break;
            }
            case Numeric: {
                rangeMin = 48;
                rangeMax = 57;
                break;
            }
            case Alphanumeric: {
                rangeMin = 48;
                rangeMax = 122;
                break;
            }
        }
        new Random().nextBytes(array);
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) clip(randomInt(new RangeInt(rangeMin, rangeMax)), 0, 255);
        }
        String generatedString = new String(array, Charset.forName("UTF-8"));
        return generatedString;
    }

    public enum EASCIICharClassName {
        All(0) {
            @Override
            public String toString() {
                return "All";
            }
        },
        Alpha(1) {
            @Override
            public String toString() {
                return "Alpha";
            }
        },
        Numeric(2) {
            @Override
            public String toString() {
                return "Numeric";
            }
        },
        Alphanumeric(3) {
            @Override
            public String toString() {
                return "Alphanumeric";
            }
        };

        public final int value;

        private static HashMap<Integer, EASCIICharClassName> intMap;

        private EASCIICharClassName(int value) {
            this.value = value;
        }

        public static EASCIICharClassName getByInt(int value) {
            if (intMap == null) {
                intMap = new HashMap<Integer, EASCIICharClassName>();
                for (EASCIICharClassName s : EASCIICharClassName.values()) {
                    intMap.put(s.value, s);
                }
            }
            if (intMap.containsKey(value)) {
                return intMap.get(value);
            }
            return null;
        }
    }
}

