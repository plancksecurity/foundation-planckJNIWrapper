package foundation.pEp.jniadapter;

public class _Blob {
    public byte[] data;
    public byte[] mime_type;
    public byte[] filename;

    _Blob() { }

    private native byte[] _dataToXER() throws pEpException;;
    public String dataToXER() {
        return Utils.toUTF16(_dataToXER());
    }

    _Blob(Blob b) {
        data = b.data;
        mime_type = Utils.toUTF8(b.mime_type);
        filename = Utils.toUTF8(b.filename);
    }
}

