package foundation.pEp.jniadapter;

import foundation.pEp.jniadapter.interfaces.BlobInterface;
import java.io.Serializable;
import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        boolean ret = false;
        Blob blob = (Blob) o;
        if (Arrays.equals(data, blob.data)) {
            if (this.mime_type.equals(((Blob) o).mime_type)) {
                if (Utils.URIEqual(this.filename, ((Blob) o).filename)) {
                    ret = true;
                }
            }
        }
        return ret;
    }

    @Override
    public int hashCode() {
        int result = mime_type == null ? 0 : mime_type.hashCode();
        result = 31 * result + Utils.URIHash(filename);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}

