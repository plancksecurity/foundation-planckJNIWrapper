package org.pEp.jniadapter;

import java.util.ArrayList;
import java.util.Vector;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;

abstract class AbstractEngine implements AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    private native void init() throws pEpException;
    private native void release();

    private long handle;

    final protected long getHandle() {
        return handle;
    }

    public AbstractEngine() throws pEpException {
        init();
    }

    final public void close() {
        release();
    }

    public static byte[] toUTF8(String str) {
        try {
            String _str = Normalizer.normalize(str, Normalizer.Form.NFC);
            return _str.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new byte[0];
        }
    }

    public static Vector<byte[]> toUTF8(Vector<String> list) {
        Vector<byte[]> result = new Vector<byte[]>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF8(list.get(i)));

        return result;
    }

    public static Pair<byte[], byte[]> toUTF8(Pair<String, String> pair) {
        Pair<byte[], byte[]> result = new Pair<byte[], byte[]>();

        result.first = toUTF8(pair.first);
        result.second = toUTF8(pair.second);

        return result;
    }

    public static ArrayList<Pair<byte[], byte[]>> toUTF8(ArrayList<Pair<String, String>> list) {
        ArrayList<Pair<byte[], byte[]>> result = new ArrayList<Pair<byte[], byte[]>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF8(list.get(i)));

        return result;
    }

    public static String toUTF16(byte[] utf8) {
        try {
            return new String(utf8, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new String();
        }
    }

    public static Vector<String> toUTF16(Vector<byte[]> list) {
        Vector<String> result = new Vector<String>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF16(list.get(i)));

        return result;
    }

    public static Pair<String, String> toUTF16(Pair<byte[], byte[]> pair) {
        Pair<String, String> result = new Pair<String,String>();

        result.first = toUTF16(pair.first);
        result.second = toUTF16(pair.second);

        return result;
    }

    public static ArrayList<Pair<String, String>> toUTF16(ArrayList<Pair<byte[], byte[]>> list) {
        ArrayList<Pair<String, String>> result = new ArrayList<Pair<String, String>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF16(list.get(i)));

        return result;
    }

    public class _Identity {
        public byte[] address;
        public byte[] fpr;
        public byte[] user_id;
        public byte[] username;
        CommType comm_type;
        public byte[] lang;
        public boolean me;

        public _Identity(Identity value) {
            address = toUTF8(value.address);
            fpr = toUTF8(value.fpr);
            user_id = toUTF8(value.user_id);
            username = toUTF8(value.username);
            comm_type = value.comm_type;
            lang = toUTF8(value.lang);
            me = value.me;
        }

        public Identity getIdentity() {
            Identity ident = new Identity(me);

            ident.address = toUTF16(address);
            ident.fpr = toUTF16(fpr);
            ident.user_id = toUTF16(user_id);
            ident.username = toUTF16(username);
            ident.comm_type = comm_type;
            ident.lang = toUTF16(lang);
            ident.me = me;

            return ident;
        }
    }
}

