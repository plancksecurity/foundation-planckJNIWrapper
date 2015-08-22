package org.pEp.jniadapter;

public class _Blob {
    public byte[] data;
    public byte[] mime_type;
    public byte[] filename;

    _Blob() { }

    _Blob(Blob b) {
        data = b.data;
        if (b.mime_type != null)
            mime_type = AbstractEngine.toUTF8(b.mime_type);
        if (b.filename != null)
            filename = AbstractEngine.toUTF8(b.filename);
    }
}

