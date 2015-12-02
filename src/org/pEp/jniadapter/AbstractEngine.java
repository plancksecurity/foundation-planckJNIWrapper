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
    private long queueThread;

    public native void startKeyserverLookup();
    public native void stopKeyserverLookup();

    public static byte[] toUTF8(String str) {
        if (str == null)
            return null;

        try {
            String _str = Normalizer.normalize(str, Normalizer.Form.NFC);
            byte _buf[] = _str.getBytes("UTF-8");
            // F*ck you, Java !
            byte _cpy[] = new byte[_buf.length];
            System.arraycopy(_buf,0,_cpy,0,_buf.length);
            return _cpy;
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new byte[0];
        }
    }

    public static Vector<byte[]> toUTF8(Vector<String> list) {
        if (list == null)
            return null;

        Vector<byte[]> result = new Vector<byte[]>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF8(list.get(i)));

        return result;
    }

    public static Pair<byte[], byte[]> toUTF8(Pair<String, String> pair) {
        if (pair == null)
            return null;

        Pair<byte[], byte[]> result = new Pair<byte[], byte[]>();

        result.first = toUTF8(pair.first);
        result.second = toUTF8(pair.second);

        return result;
    }

    public static ArrayList<Pair<byte[], byte[]>> toUTF8(ArrayList<Pair<String, String>> list) {
        if (list == null)
            return null;

        ArrayList<Pair<byte[], byte[]>> result = new ArrayList<Pair<byte[], byte[]>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF8(list.get(i)));

        return result;
    }

    public static String toUTF16(byte[] utf8) {
        if (utf8 == null)
            return null;

        try {
            byte newUtf8[] = new byte[utf8.length];
            System.arraycopy(utf8,0,newUtf8,0,utf8.length);

            return new String(newUtf8, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            assert false;
            return new String();
        }
    }

    public static Vector<String> toUTF16(Vector<byte[]> list) {
        if (list == null)
            return null;

        Vector<String> result = new Vector<String>(list.size());

        for (int i=0; i<list.size(); i++)
            result.add(toUTF16(list.get(i)));

        return result;
    }

    public static Pair<String, String> toUTF16(Pair<byte[], byte[]> pair) {
        if (pair == null)
            return null;

        Pair<String, String> result = new Pair<String,String>();

        result.first = toUTF16(pair.first);
        result.second = toUTF16(pair.second);

        return result;
    }

    public static ArrayList<Pair<String, String>> toUTF16(ArrayList<Pair<byte[], byte[]>> list) {
        if (list == null)
            return null;

        ArrayList<Pair<String, String>> result = new ArrayList<Pair<String, String>>(list.size());

        for (int i=0; i<list.size(); i++)
            result.set(i, toUTF16(list.get(i)));

        return result;
    }
}

