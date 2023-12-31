package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;
import foundation.pEp.pitytest.utils.RangeInt;
import foundation.pEp.pitytest.utils.TestUtils;

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

    // Factory methods to create test objects
    public static Vector<Blob> makeNewTestBlobList(int count) {
        Vector<Blob> blbList = new Vector<>();

        for (int i = 0; i < count; i++) {
            Blob blb = makeNewTestBlob("Attachement nr: " + i + " [TEST DATA]", "testfilename"+i+".txt", "text/plain" );
            blbList.add(blb);
        }
        return blbList;
    }

    // Factory methods to create test objects
    public static Vector<Blob> makeNewTestBlobList(int sizeBytes, String filename, String mimeType, int count) {
        Vector<Blob> blbList = new Vector<>();

        for (int i = 0; i < count; i++) {
            Blob blb = makeNewTestBlob(sizeBytes, filename + Integer.toString(i), mimeType);
            blbList.add(blb);
        }
        return blbList;
    }

    public static Blob makeNewTestBlob(String data, String filename, String mimeType) {
        return makeNewTestBlob(data.getBytes(),filename,mimeType);
    }

    public static Blob makeNewTestBlob(int sizeBytes, String filename, String mimeType) {
        byte bData[] = new byte[sizeBytes];
        for (int i = 0; i < sizeBytes; i++) {
            bData[i] = (byte) TestUtils.randomInt(new RangeInt(65, 90)); // All uppercase letters
        }
        return makeNewTestBlob(bData,filename,mimeType);
    }



    public static Blob makeNewTestBlob(byte[] data, String filename, String mimeType) {
        Blob blb = new Blob();
        blb.data = data;
        blb.filename = filename;
        if (mimeType != null) {
            blb.mime_type = mimeType;
        }
        return blb;
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

    public static Message newOutMessage(Identity from, Identity to, String longMessage) {
        Message ret = new Message();
        Vector<Identity> toList = new Vector<>();
        toList.add(to);
        ret.setTo(toList);
        ret.setFrom(from);
//        ret.setShortmsg();
        ret.setLongmsg(longMessage);
        ret.setDir(Message.Direction.Outgoing);
        return ret;
    }

//    public static Message newInMessage(Identity from, Identity to, String longMessage) {
//        Message ret = new Message();
//        Vector<Identity> toList = new Vector<>();
//        toList.add(to);
//        ret.setTo(toList);
//        ret.setFrom(from);
////        ret.setShortmsg();
//        ret.setLongmsg(longMessage);
//        ret.setDir(Message.Direction.Outgoing);
//        return ret;
//    }

    public static Message generateSourceMessage(Identity from, Identity to, long id, long longMsgLen) {
        Message msg = new Message();
        Vector<Identity> vID = new Vector<Identity>();
        if (to != null) {
            vID.add(to);
        }

        msg.setFrom(from);
        msg.setTo(vID);
        msg.setDir(Message.Direction.Outgoing);
        msg.setShortmsg(String.valueOf(id));

//        String
        msg.setLongmsg("Hi i am the longMessage");
        return msg;
    }


    public static DiffResult diff(byte[] left, byte[] right) {
        DiffResult ret = new DiffResult();
        String diffString = "";
        int diffCount = 0;
        int firstDiff = 0;
        boolean firstDiffHappened = false;
        for (int i = 0; i < left.length; i++) {
            byte bLeft = left[i];
            byte bRight = right[i];
            String diffIndicator = "";
            if (bLeft != bRight) {
                if(!firstDiffHappened) {
                    firstDiff = i;
                    firstDiffHappened = true;
                }
                diffCount++;
                diffString += "Byte[" + i + "]:\t\t " + bLeft + "\t" + bRight + "\t" + "\n";
            }
        }
        ret.setDiff(diffString);
        ret.setCount(diffCount);
        ret.setFirstDiffByte(firstDiff);
        return ret;
    }

    public static boolean optFieldsEqual(ArrayList<Pair<String,String>> left, ArrayList<Pair<String,String>> right){
        boolean equal = false;
        int nrFieldsExcessive = TestUtils.clip(right.size() - right.size(), 0, right.size());
        int nrFieldsMissing = 0;
        for (Pair pIn : right) {
            boolean found = false;
            for (Pair pOut : right) {
                if (pOut.first.equals(pIn.first)) {
                    if (pOut.second.equals(pIn.second)) {
                        found = true;
                    }
                }
            }
            if (!found) {
                nrFieldsMissing++;
            }
        }
        if((nrFieldsMissing == 0) && (nrFieldsExcessive ==0)) {
            equal = true;
        }
        return equal;
    }

    public static void addRatingToOptFields(Message msg, String ratingStr) {
        ArrayList<Pair<String, String>> opts = msg.getOptFields();
        opts.add(new Pair<String, String>("X-EncStatus",ratingStr));
        msg.setOptFields(opts);
    }

    public static void addRcptsToOptFields(Message msg, String fprs) {
        ArrayList<Pair<String, String>> opts = msg.getOptFields();
        opts.add(new Pair<String, String>("X-KeyList", fprs));
        msg.setOptFields(opts);
    }

}

