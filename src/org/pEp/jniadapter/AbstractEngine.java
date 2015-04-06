package org.pEp.jniadapter;

abstract class AbstractEngine implements AutoCloseable {
    static {
        System.loadLibrary("pEpJNI");
    }

    private native void init() throws pEpException;
    private native void release();

    private long handle;
    final protected long getHandle() { return handle; }

    public AbstractEngine() throws pEpException {
        init();
    }

    final public void close() {
        release();
    }
}

