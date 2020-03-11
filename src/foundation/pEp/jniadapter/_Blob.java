package foundation.pEp.jniadapter;

public class _Blob {
    public byte[] data;
    public byte[] mime_type;
    public byte[] filename;

    _Blob() { }

    private native byte[] _dataToXER() throws pEpException;;
    public String dataToXER() {
        return AbstractEngine.toUTF16(_dataToXER());
    }

    _Blob(Blob b) {
        data = b.data;
        mime_type = AbstractEngine.toUTF8(b.mime_type);
        filename = AbstractEngine.toUTF8(b.filename);
    }
}

