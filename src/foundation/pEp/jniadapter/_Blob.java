package foundation.pEp.jniadapter;

public class _Blob {
    public byte[] data;
    public byte[] mime_type;
    public byte[] filename;

    _Blob() { }

    private native byte[] _toString() throws pEpException;;
    public String toString() {
        return AbstractEngine.toUTF16(_toString());
    }

    _Blob(Blob b) {
        data = b.data;
        mime_type = AbstractEngine.toUTF8(b.mime_type);
        filename = AbstractEngine.toUTF8(b.filename);
    }
}

