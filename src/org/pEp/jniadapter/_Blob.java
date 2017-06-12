package org.pEp.jniadapter;

public class _Blob {
    public byte[] data;
    public byte[] mime_type;
    public byte[] filename;
    public byte[] content_id;

    _Blob() { }

    _Blob(Blob b) {
        data = b.data;
        mime_type = AbstractEngine.toUTF8(b.mime_type);
        filename = AbstractEngine.toUTF8(b.filename);
        content_id = AbstractEngine.toUTF8(b.content_id);
    }
}

