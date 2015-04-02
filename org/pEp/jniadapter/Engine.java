package org.pEp.jniadapter;

public class Engine implements AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    protected native void init() throws pEpException;
    protected native void release();

    private long handle;

    public Engine() throws pEpException {
        init();
    }

    public void close() {
        release();
    }
}

