package foundation.pEp.jniadapter;

import foundation.pEp.jniadapter.interfaces.IdentityInterface;
import java.io.Serializable;
import java.util.List;

public class Identity implements IdentityInterface, Serializable {
    public String address;
    public String fpr;
    public String user_id;
    public String username;
    public CommType comm_type;
    public String lang;
    public boolean me;
    public int flags;

    /**
     * The major version of this identity.
     * <p>
     *     Please note that the original type in C is <code>unsigned int</code>,
     *     but the assumption is that no version will ever be so big
     *     as to cause trouble.
     * </p>
     */
    public int major_ver;

    /**
     * The minor version of this identity.
     * <p>
     *     Please note that the original type in C is <code>unsigned int</code>,
     *     but the assumption is that no version will ever be so big
     *     as to cause trouble.
     * </p>
     */
    public int minor_ver;

    public EncryptionFormat enc_format;

    public Identity() {
        this.me = false;
        comm_type = CommType.PEP_ct_unknown;
        enc_format = EncryptionFormat.PlanckEncNone;
    }

    public Identity(boolean me) {
        this.me = me;
        comm_type = CommType.PEP_ct_unknown;
        enc_format = EncryptionFormat.PlanckEncNone;
    }

    public Identity(_Identity i) {
        address = Utils.toUTF16(i.address);
        fpr = Utils.toUTF16(i.fpr);
        user_id = Utils.toUTF16(i.user_id);
        username = Utils.toUTF16(i.username);
        comm_type = CommType.Management.tag.get(i.comm_type);
        lang = Utils.toUTF16(i.lang);
        me = i.me;
        flags = i.flags;
        major_ver = i.major_ver;
        minor_ver = i.minor_ver;
        enc_format = EncryptionFormat.getByInt(i.enc_format);
    }

    public Rating getRating() {
        return Rating.getByInt(_getRating(comm_type.value));
    }

    private native int _getRating(int commType);

    public static String toXKeyList(List<Identity> ids) {
        String ret = "";
        if (ids.size() > 0) {
            for (Identity id : ids) {
                ret += id.fpr;
                ret += ",";
            }
            ret = ret.substring(0, ret.length() - 1);
        }
        return ret;
    }

    @Override
    public String toString() {
        return address + "::" + user_id + "::" + username + "::" + fpr;
    }
}

