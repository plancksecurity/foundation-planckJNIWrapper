package foundation.pEp.jniadapter;

import java.io.Serializable;

public class Blob implements BlobInterface, Serializable {
    public byte[] data;
    public String mime_type;
    public String filename;

    public Blob() {
        mime_type = "application/octet-stream";
    }

    Blob(_Blob b) {
        data = b.data;
        mime_type = Utils.toUTF16(b.mime_type);
        filename = Utils.toUTF16(b.filename);
    }

    /**
     * Human readable string representation of Blob.
     * The data field is ASN.1 XER decoded for mime_types:
     * "application/pEp.sync"
     * "application/pEp.keyreset"
     *
     * @return String Blob as String
     */
    public String toString() {
        _Blob _b = new _Blob(this);
        String ret = "";
        ret += "mime_type: \"" + mime_type + "\"\n";
        ret += "filename: \"" + filename + "\"\n";
        ret += "data plain: \"" + Utils.toUTF16(data) + "\"\n";
        ret += "data decoded: \"" + _b.dataToXER() + "\"\n";
        return ret;
    }

}

