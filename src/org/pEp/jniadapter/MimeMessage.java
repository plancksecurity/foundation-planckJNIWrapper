package org.pEp.jniadapter;

import java.util.Date;

public class MimeMessage {
    public enum TextFormat {
        plain (0),
        html (1),
        other (255);

        public final int value;

        TextFormat(int value) {
            this.value = value;
        }
    }

    public enum Direction {
        incoming (0),
        outgoing (1);

        public final int value;

        Direction(int value) {
            this.value = value;
        }
    }

    public enum EncFormat {
        none (0),
        pieces (1),
        S_MIME (2),
        PGP_MIME (3),
        PEP (4);

        public final int value;

        EncFormat(int value) {
            this.value = value;
        }
    }

    public native Direction dir();
    public native void dir(Direction value);
    
    public native String id();
    public native void id(String value);

    public native String shortmsg();
    public native void shortmsg(String value);

    public native String longmsg();
    public native void longmsg(String value);

    public native String longmsg_formatted();
    public native void longmsg_formatted(String value);

    public native Blob[] attachments();
    public native void attachments(Blob[] value);

    public native Date sent();
    public native void sent(Date value);

    public native Date recv();
    public native void recv(Date value);

    public native Identity from();
    public native void from(Identity value);

    public native Identity[] to();
    public native void to(Identity[] value);

    public native Identity recv_by();
    public native void recv_by(Identity value);

    public native Identity[] cc();
    public native void cc(Identity[] value);

    public native Identity[] bcc();
    public native void bcc(Identity[] value);

    public native Identity[] reply_to();
    public native void reply_to(Identity[] value);

    public native String[] in_reply_to();
    public native void in_reply_to(String[] value);

    public native String[] references();
    public native void references(String[] value);

    public native String[] keywords();
    public native void keywords(String[] value);

    public native String comments();
    public native void comments(String value);

    public native Pair<String, String>[] opt_fields();
    public native void opt_fields(Pair<String, String>[] value);

    public native EncFormat enc_format();
    public native void enc_format(EncFormat value);
}

