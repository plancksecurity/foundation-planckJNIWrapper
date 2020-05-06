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
            ret += i.address + "/" + i.fpr;
        }
        ret = ret.trim();
        return ret;
    }

    public static String msgToString(Message msg) {
        String ret = "";
        ret += "Id: " + msg.getId() + "\n";
        ret += "getDir: " + msg.getDir().toString() + "\n";
        ret += "getShortmsg: " + msg.getShortmsg() + "\n";
        ret += "getLongmsg: " + msg.getLongmsg() + "\n";
        ret += "getLongmsgFormatted: " + msg.getLongmsgFormatted() + "\n";
        ret += "getAttachments: \n" + blobListToString(msg.getAttachments()) + "\n";
        ret += "getFrom: " + identityToString(msg.getFrom(), false) + "\n";
        ret += "getTo: " + identityListToString(msg.getTo(), false) + "\n";
// FIXME: They all throw NullPointerException
//        ret += "getRecvBy: " + identityToString(msg.getRecvBy(), false) + "\n";
//        ret += "getCc: " + identityListToString(msg.getCc(), false)+ "\n";
//        ret += "getBcc: " + identityListToString(msg.getBcc(), false) + "\n";
//        ret += "getReplyTo: " + identityListToString(msg.getReplyTo(), false) + "\n";
//        ret += "getInReplyTo: " + stringVectorToString(msg.getInReplyTo())  + "\n";
//        ret += "getReferences: " + stringVectorToString(msg.getReferences())  + "\n";
//        ret += "getKeywords: " + stringVectorToString(msg.getKeywords())  + "\n";
        ret += "getComments: " + msg.getComments() + "\n";
        ret += "getOptFields: " + stringPairListToString(msg.getOptFields()) + "\n";
        ret += "getEncFormat: " + msg.getEncFormat().toString() + "\n";
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