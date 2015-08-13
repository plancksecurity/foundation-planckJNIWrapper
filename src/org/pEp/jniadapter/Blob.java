package org.pEp.jniadapter;

public class Blob {
    public byte[] data;
    public String mime_type;
    public String filename;

    Blob() {
        mime_type = "application/octet-stream";
    }

    Blob(_Blob b) {
        data = b.data;
        mime_type = AbstractEngine.toUTF16(b.mime_type);
        filename = AbstractEngine.toUTF16(b.filename);
    }
}

