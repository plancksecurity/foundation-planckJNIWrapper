package pEp.jniadapter;

import java.util.ArrayList;
import java.util.Vector;

import pEp.jniadapter.Sync.DefaultCallback;

import java.io.UnsupportedEncodingException;
import java.text.Normalizer;

abstract class AbstractEngine implements AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    private Sync.MessageToSendCallback messageToSendCallback;
    private Sync.NotifyHandshakeCallback notifyHandshakeCallback;
    private Sync.NeedsFastPollCallback needsFastPollCallback;

    private final static DefaultCallback defaultCallback = new DefaultCallback();

    private native void init();
    private native void release();

    public AbstractEngine() throws pEpException {
        synchronized (AbstractEngine.class) {
            init();
        }
    }

    final public void close() {
        synchronized (AbstractEngine.class){
            release();
        }
    }

    public native String getVersion();
    public native String getProtocolVersion();

    private long keyserverThread;
    private long keyserverQueue;

    public native void startKeyserverLookup();
    public native void stopKeyserverLookup();

    public native void startSync();
    public native void stopSync();

    public static byte[] toUTF8(String str) {
        if (str == null)
            return null;

        try {
            String _str = Normalizer.normalize(str, Normalizer.Form.NFC);
            byte _buf[] = _str.getBytes("UTF-8");
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
            result.add(toUTF8(list.get(i)));

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

    public void setMessageToSendCallback(Sync.MessageToSendCallback messageToSendCallback) {
        this.messageToSendCallback = messageToSendCallback;
    }

    public void setNotifyHandshakeCallback(Sync.NotifyHandshakeCallback notifyHandshakeCallback) {
        this.notifyHandshakeCallback = notifyHandshakeCallback;
    }

    public void setNeedsFastPollCallback(Sync.NeedsFastPollCallback needsFastPollCallback) {
        this.needsFastPollCallback = needsFastPollCallback;
    }

    public int needsFastPollCallFromC(boolean fast_poll_needed) {
        if (needsFastPollCallback != null) {
            needsFastPollCallback.needsFastPollCallFromC(fast_poll_needed);
        } else {
            defaultCallback.needsFastPollCallFromC(fast_poll_needed);
        }
        return 0;
    }

    public int notifyHandshakeCallFromC(_Identity _myself, _Identity _partner, SyncHandshakeSignal _signal) {
        Identity myself = new Identity(_myself);
        Identity partner = new Identity(_partner);

        if (notifyHandshakeCallback != null) {
            notifyHandshakeCallback.notifyHandshake(myself, partner, _signal);
        } else {
            defaultCallback.notifyHandshake(myself, partner, _signal);
        }
        return 0;
    }

    public int messageToSendCallFromC (Message message) {
        if (messageToSendCallback != null) {
            messageToSendCallback.messageToSend(message);
        } else {
            defaultCallback.messageToSend(message);
        }
        return 0;
    }
}

