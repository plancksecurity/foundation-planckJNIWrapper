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

    private long keyserverThread;

    public native void startKeyserverLookup();
    public native void stopKeyserverLookup();

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
}

