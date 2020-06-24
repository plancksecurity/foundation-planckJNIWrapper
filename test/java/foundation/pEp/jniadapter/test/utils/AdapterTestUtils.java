package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.jniadapter.*;

import java.util.ArrayList;
import java.util.Vector;

import static foundation.pEp.pitytest.utils.TestUtils.clipString;

public class AdapterTestUtils {
    public static String identityToString(Identity i, Boolean full) {
        String ret = "";
        if (full) {
            ret += "address: " + i.address + "\n";
            ret += "fpr: " + i.fpr + "\n";
            ret += "username: " + i.username + "\n";
            ret += "user_id: " + i.user_id + "\n";
            ret += "flags: " + i.flags + "\n";
            ret += "lang: " + i.lang + "\n";
            ret += "me: " + i.me + "\n";
            ret += "comm_type: " + i.comm_type + "\n";
        } else {
            ret += i.address + "/" + i.user_id + "/" + i.username + "/" + i.fpr;
        }
        ret = ret.trim();
        return ret;
    }


    public static String msgToString(Message msg, boolean full) {
        String ret = "";
        ArrayList<Pair<String, String>> kvs = new ArrayList<>();
        String key = "";
        String value = "";

        key = "getAttachments";
        try {
            value = blobListToString(msg.getAttachments(), full) + "\n";
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "Id";
        try {
            value = msg.getId();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getDir";
        try {
            value = msg.getDir().toString();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getShortmsg";
        try {
            value = msg.getShortmsg();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getLongmsg";
        try {
            value = msg.getLongmsg();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getLongmsgFormatted";
        try {
            value = msg.getLongmsgFormatted();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getFrom";
        try {
            value = identityToString(msg.getFrom(), full);
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getTo";
        try {
            value = identityListToString(msg.getTo(), full);
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getRecvBy";
        try {
            value = identityToString(msg.getRecvBy(), full);
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getCc";
        try {
            value = identityListToString(msg.getCc(), full) + "\n";
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getBcc";
        try {
            value = identityListToString(msg.getBcc(), full);
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getReplyTo";
        try {
            value = identityListToString(msg.getReplyTo(), full);
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getInReplyTo";
        try {
            value = stringVectorToString(msg.getInReplyTo());
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getReferences";
        try {
            value = stringVectorToString(msg.getReferences());
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getKeywords";
        try {
            value = stringVectorToString(msg.getKeywords());
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getComments";
        try {
            value = msg.getComments();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getOptFields";
        try {
            value = stringPairListToString(msg.getOptFields());
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<>(key, value));

        key = "getEncFormat";
        try {
            value = msg.getEncFormat().toString();
        } catch (Throwable e) {
            value = e.toString();
        }
        kvs.add(new Pair<String, String>(key, value));

        if (!full) {
            kvs = clipStrings(kvs, 200, "clipped...");
        }

        ret = stringPairListToString(kvs);

        ret = ret.trim();
        return ret;
    }

    public static String stringVectorToString(Vector<String> vS) {
        String ret = "";
        for (String s : vS) {
            ret += s + "\n";
        }
        ret = ret.trim();
        return ret;
    }

    public static String identityListToString(Vector<Identity> vI, Boolean full) {
        String ret = "";
        for (Identity i : vI) {
            ret += identityToString(i, full) + "\n";
        }
        ret = ret.trim();
        return ret;
    }

    public static String stringPairListToString(ArrayList<Pair<String, String>> spl) {
        String ret = "Invalid List: null object\n";
        if (spl != null) {
            ret = "";
            for (Pair<String, String> el : spl) {
                String item = "Invalid StringPair: null object\n";
                if (el != null) {
                    item = "";
                    String k = "Invalid String: null object";
                    String v = "Invalid String: null object";
                    if (el.first != null) {
                        k = el.first;
                    }
                    if (el.second != null) {
                        v = el.second;
                    }

                    String delimBegin = "'";
                    String delimEnd = "'";
                    String indent = "";
                    if (v.contains("\n") || (v.length() > 200)) {
                        delimBegin = " {\n";
                        delimEnd = "\n}";
                        delimEnd = String.format("%-100s", delimEnd);
                        indent = String.format("%4s", " ");
                        v = v.replace("\n", "\n" + indent);
                    }

                    item = k + " = " + delimBegin + indent + v + delimEnd + "\n";
                }
                ret += item;
            }
        }
        ret = ret.trim();
        return ret;
    }

    public static String blobListToString(Vector<Blob> attachments, boolean full) {
        String ret = "";
        ret += "Attachments count: " + attachments.size() + "\n";
        for (Blob a : attachments) {
            ret += "-----BEGIN Attachment index: " + attachments.indexOf(a) + "-----\n";
            String tmp = a.toString();
            if (!full) {
                tmp = clipString(tmp, 250, "clipped...");
            }
            ret += tmp + "\n";
            ret += "-----END Attachment index: " + attachments.indexOf(a) + "-----\n";
        }
        ret = ret.trim();
        return ret;
    }

    public static ArrayList<Pair<String, String>> clipStrings(ArrayList<Pair<String, String>> spv, int len, String clipMsg) {
        for (Pair<String, String> p : spv) {
            if (p != null) {
                if (p.first != null) {
                    p.first = clipString(p.first, len, clipMsg);
                }
                if (p.second != null) {
                    p.second = clipString(p.second, len, clipMsg);
                }
            }
        }
        return spv;
    }

    public static Message makeNewTestMessage(Identity from, Identity to, Message.Direction dir) {
        Message msg = new Message();
        Vector<Identity> vID = new Vector<Identity>();
        if (to != null) {
            vID.add(to);
        }

        msg.setFrom(from);
        msg.setTo(vID);
        msg.setDir(dir);
        msg.setShortmsg("Hi i am the shortMessage");
        msg.setLongmsg("Hi i am the longMessage");
        return msg;
    }
}