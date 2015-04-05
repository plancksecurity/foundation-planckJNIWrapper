package org.pEp.jniadapter;

abstract class AbstractEngine implements AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    protected native void init() throws pEpException;
    protected native void release();

    private long handle;
    protected long getHandle() { return handle; }

    public AbstractEngine() throws pEpException {
        init();
    }

    public void close() {
        release();
    }
}

