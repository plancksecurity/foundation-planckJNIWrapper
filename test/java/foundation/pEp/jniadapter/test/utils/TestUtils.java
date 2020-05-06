package foundation.pEp.jniadapter.test.utils;
import foundation.pEp.jniadapter.*;

import java.util.ArrayList;
import java.util.Vector;

public class TestUtils {


    public static void sleep(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException ex) {
            System.out.println("sleep got interrupted");
        }
    }

    public static String identityToString(Identity i, Boolean full) {
        String ret = "";
        if(full) {
            ret += "address: " + i.address + "\n";
            ret += "fpr: " + i.fpr + "\n";
            ret += "username: " + i.username + "\n";
            ret += "user_id: " + i.user_id + "\n";
            ret += "flags: " + i.flags + "\n";
            ret += "lang: " + i.lang + "\n";
            ret += "me: " + i.me + "\n";
            ret += "comm_type: " + i.comm_type + "\n";
        } else {
            ret += i.address + "/" + i.user_id + "/" + i.fpr;
        }
        ret = ret.trim();
        return ret;
    }

    public static String msgToString(Message msg) {
        String ret = "";
        ret += "getAttachments: ";
        try {
            ret += blobListToString(msg.getAttachments()) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "Id: ";
        try {
            ret += msg.getId() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getDir: ";
        try {
            ret += msg.getDir().toString() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getShortmsg: ";
        try {
            ret += msg.getShortmsg() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getLongmsg: ";
        try {
            ret += msg.getLongmsg() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getLongmsgFormatted: ";
        try {
            ret += msg.getLongmsgFormatted() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getFrom: ";
        try {
            ret += identityToString(msg.getFrom(), false) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getTo: ";
        try {
            ret += identityListToString(msg.getTo(), false) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getRecvBy: ";
        try {
            ret += identityToString(msg.getRecvBy(), false) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getCc: ";
        try {
            ret += identityListToString(msg.getCc(), false)+ "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getBcc: ";
        try {
            ret += identityListToString(msg.getBcc(), false) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getReplyTo: ";
        try {
            ret += identityListToString(msg.getReplyTo(), false) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getInReplyTo: ";
        try {
            ret += stringVectorToString(msg.getInReplyTo())  + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getReferences: ";
        try {
            ret += stringVectorToString(msg.getReferences())  + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getKeywords: ";
        try {
            ret += stringVectorToString(msg.getKeywords())  + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getComments: ";
        try {
            ret += msg.getComments() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getOptFields: ";
        try {
            ret += stringPairListToString(msg.getOptFields()) + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret += "getEncFormat: ";
        try {
            ret += msg.getEncFormat().toString() + "\n";
        } catch(Throwable e) {
            ret += e.toString() + "\n";
        }

        ret = ret.trim();
        return ret;
    }

    public static String stringVectorToString(Vector<String> vS) {
        String ret = "";
        for (String s : vS){
            ret += s + "\n";
        }
        ret = ret.trim();
        return ret;
    }

    public static String identityListToString(Vector<Identity> vI, Boolean full) {
        String ret = "";
        for(Identity i : vI) {
            ret += identityToString(i, full) + "\n";
        }
        ret = ret.trim();
        return ret;
    }

    public static String stringPairListToString(ArrayList<Pair<String, String>> opts) {
        String ret = "";
        for(Pair<String, String> el : opts) {
            ret += el.first + "=" + el.second + "\n";
        };
        ret = ret.trim();
        return ret;
    }

    public static String blobListToString(Vector<Blob> attachments) {
        String ret = "";
        ret += "Attachments count: " + attachments.size() + "\n";
        for( Blob a: attachments) {
            ret += "-----BEGIN Attachment index: " + attachments.indexOf(a) + "-----\n";
            ret += a.toString();
            ret += "-----END Attachment index: " + attachments.indexOf(a) + "-----\n";
        }
        ret = ret.trim();
        return  ret;
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
        log( getDecoratedString(msg, "="));
    }

    public static void logH2(String msg) {
        log( getDecoratedString(msg, "-"));
    }

    private static String getDecoratedString(String msg, String s) {
        int lineWidth = 80;
        String decorationChar = s;
        String decorationStr = "";
        for (int i = 0; i < Math.ceil((lineWidth - msg.length() + 2) / 2); i++) {
            decorationStr += decorationChar;
        }
        return decorationStr + " " + msg + " " + decorationStr;
    }

    public static void logSectEnd(String msg) {
        log(msg + "\n");
    }
}