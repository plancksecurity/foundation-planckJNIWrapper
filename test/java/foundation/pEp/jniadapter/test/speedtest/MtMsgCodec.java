package foundation.pEp.jniadapter.test.speedtest;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;

/**
 * MTMsgCodec is the Codec class for encoding/decoding p≡p for SWIFT messages.
 *
 * <p>
 * See <a href=
 * "https://www.sepaforcorporates.com/swift-for-corporates/read-swift-message-structure/"
 * target="_blank">SWIFT message structure</a>, <a href=
 * "https://www.ibm.com/support/knowledgecenter/SSRH32_3.0.0_SWS/mt_msg_format.html"
 * target="_blank">MT message format</a>, <a href=
 * "http://www.iotafinance.com/en/SWIFT-ISO15022-Message-type-MT999.html" target
 * ="_blank">MT999</a>
 * </p>
 * <p>
 * Copyright 2019, <a href="https://pep.security" target="_blank">p≡p
 * Security</a>.
 * </p>
 *
 * @author Volker Birk
 * @version %I% %G%
 */

// FIXME: rework to stringbuffer

public class MTMsgCodec {
    public Engine pEp;

    /**
     * Constructs a MessageCodec using p≡p engine.
     *
     * @param pEp_engine p≡p engine to use
     */

    public MTMsgCodec(Engine pEp_engine) {
        pEp = pEp_engine;
    }

    private final static String magicKeys = "pEpKeys";
    private final static String magicEnc = "pEpMessage";

    private final static String pgp_regex = "(?ms)" + "-----BEGIN PGP (?<type>.*?)-----\n" + "(\\w:.*?$\n)*" + "\n"
            + "(?<block>.*?)" + "-----END PGP (.*?)-----.*";

    private static final Pattern pgp_pattern = Pattern.compile(pgp_regex);

    private static final String pgptypeMessage = "MESSAGE";
    private static final String pgptypePubkey = "PUBLIC KEY BLOCK";

    // FIXME: private final static String uri_regex = "payto://swift/(?<bic>\\w+)";
    private final static String uri_regex = "(?<bic>\\w+)@BIC";

    private final static Pattern uri_pattern = Pattern.compile(uri_regex);

    /**
     * Strips PGP header and footer from encryption or key data.
     *
     * @param pgp_text text to work on
     * @throws ParseException if text is not valid PGP data
     * @return <code>pgp_text</code> without PGP header
     */

    protected static String stripFromPGP(String pgp_text) throws ParseException {

        Matcher m = pgp_pattern.matcher(pgp_text);
        if (!m.matches())
            throw new ParseException("not a PGP block", 0);

        return m.group("block");
    }

    /**
     * Adds PGP header and footer.
     *
     * @param payload  text to decorate
     * @param pgp_type PGP data type
     * @return <code>payload</code> with added header and footer
     */

    protected static String addPGPHeader(String payload, String pgp_type) {
        // FIXME: rework to stringbuffer
        return "-----BEGIN PGP " + pgp_type + "-----\n\n" + payload + "\n-----END PGP " + pgp_type + "-----\n";
    }

    /**
     * Decodes a BIC from an URI.
     *
     * @param uri the URI to decode from
     * @throws ParseException if URI has not the correct form
     * @return decoded BIC
     */

    public static String decodeBIC(String uri) throws ParseException {
        Matcher m = uri_pattern.matcher(uri);
        if (!m.matches())
            throw new ParseException("not a valid URI", 0);

        return m.group("bic");
    }

    /**
     * Encodes a BIC into an URI.
     *
     * @param bic BIC to encode
     * @return encoded URI
     */

    public static String encodeBIC(String bic) {
        // return "payto://swift/" + bic
        return bic + "@BIC";
    }

    /**
     * Generates a list of transporting MT999 messages for a payload.
     *
     * @param from    source address
     * @param to      destination address
     * @param ii      message direction
     * @param trn     message id
     * @param payload payload to split
     * @param magic   magic string to mark as p≡p message
     *
     * @return array of String with MT999 messages
     */

    protected String[] transportMT999(String from, String to, String ii, String trn, String rr, String payload,
                                      String magic) {

        Vector<String> result = new Vector<String>();
        payload = payload.trim();

        int j = 1, f = 0, t = 0;
        for (int i = payload.indexOf("\n"); i != -1; i = payload.indexOf("\n", i + 1), j++) {

            if (j % 34 == 0) {
                t = i + 1;
                String cont = t < payload.length() - 1 ? "." : "";
                MT999 mt999 = new MT999(from, to, ii, trn, rr, magic + "\n" + payload.substring(f, t) + cont);
                result.add(mt999.toString());
                f = i + 1;
            }
        }
        if (t < payload.length() - 1) {
            int z = payload.charAt(payload.length() - 1) == '\n' ? 1 : 0;
            MT999 mt999 = new MT999(from, to, ii, "23", // fixed trn
                    "", magic + "\n" + payload.substring(t, payload.length() - z));
            result.add(mt999.toString());
        }

        return result.toArray(new String[result.size()]);
    }

    /**
     * encodes a p≡p Message to a String with a serialized p≡p for SWIFT message.
     *
     * @param msg    p≡p Message to encode if from or to are not set they're taken
     *               by parsing the header of <code>longmsg</code>; in this case
     *               message direction is being taken from there, too
     * @param config properties with configuration
     * @return String with encoded p≡p for SWIFT message
     * @throws UnsupportedOperationException if <code>EncFormat</code> is other than
     *                                       <code>None</code>, <code>Inline</code>
     *                                       or <code>PEP</code>
     */

    public String encode(Message msg, Properties config) {
        if (msg.getLongmsg() == null || msg.getLongmsg().length() < 1)
            throw new IllegalArgumentException("longmsg must contain the message");

        String result = "";

        String msgid = msg.getId() != null && msg.getId().length() > 0 ? msg.getId() : "23";
        String dir = msg.getDir() == Message.Direction.Incoming ? "I" : "O";

        if (msgid.length() > 12) {
            if (msgid.substring(0, 3).compareTo("pEp.") == 0 && msgid.length() >= 15)
                msgid = msgid.substring(4, 15);
            else
                msgid = msgid.substring(0, 11);
        }

        String from = msg.getFrom() != null && msg.getFrom().address.length() > 0 ? msg.getFrom().address : null;

        String to = msg.getTo() != null && msg.getTo().size() > 0 && msg.getTo().elementAt(0).address.length() > 0
                ? msg.getTo().elementAt(0).address
                : null;

        if (from != null) {
            try {
                from = decodeBIC(from);
                if (from.length() != 12)
                    throw new IllegalArgumentException("from address must be URI with BIC12");
            } catch (ParseException ex) {
                throw new IllegalArgumentException("from address must be URI with BIC12");
            }
        }

        if (to != null) {
            try {
                to = decodeBIC(to);
                if (to.length() != 12)
                    throw new IllegalArgumentException("to address must be URI with BIC12");
            } catch (ParseException ex) {
                throw new IllegalArgumentException("to address must be URI with BIC12");
            }
        }

        switch (msg.getEncFormat()) {
            case None:
                result = msg.getLongmsg(); // we send the message unmodified
                if (result.substring(0, 3).compareTo("{1:") == 0) {
                    // probably SWIFT MTFIN

                    if (from == null || to == null || msgid == null) {
                        // parse SWIFT header
                        SWIFTMsg mh = new SWIFTMsg();

                        try {
                            mh.parseHeader(result);
                        } catch (ParseException ex) {
                            throw new UnsupportedOperationException("unsupported message format");
                        }

                        if (from == null)
                            from = mh.logicalTerminalAddress;
                        if (to == null)
                            to = mh.destinationAddress;
                        dir = mh.inputIdentifier;
                    }
                } else if (result.substring(0, 1).compareTo("<") == 0) {
                    // probably XML

                    // FIXME: let's do the job for MTXML, too
                    return result;
                } else { // we don't know this format, so let it be
                    return result;
                }

                if(msg.getAttachments() != null)
                    for (int i = 0; i < msg.getAttachments().size(); ++i) { // FIXME: can attachments become null?
                        Blob attach = msg.getAttachments().elementAt(i);
                        String magic;

                        if (attach.mime_type.compareToIgnoreCase("application/pgp-keys") == 0)
                            magic = magicKeys;
                        else
                            break; // don't know this MIME type

                        // we send an MT999 with the keys
                        String payload;
                        try {
                            payload = stripFromPGP(new String(attach.data, StandardCharsets.UTF_8));
                        } catch (ParseException ex) {
                            // cannot parse this
                            break;
                        }

                        String[] msgs = transportMT999(from, to, dir, msgid, "", payload, magic);

                        for (int j = 0; j < msgs.length; ++j) {
                            if (j > 0)
                                result += "\n";
                            result += msgs[j].toString();
                        }
                    }
                break;

            case PEP:
            case PGPMIME:
            case Inline:
                if (from == null || to == null)
                    throw new IllegalArgumentException("from and to must be set to URIs with BIC12");

                String pgp_txt;
                Vector<Blob> attachments = null;

                if (msg.getEncFormat() == Message.EncFormat.PEP || msg.getEncFormat() == Message.EncFormat.PGPMIME) {
                    if (msg.getAttachments() == null || msg.getAttachments().size() != 2)
                        throw new IllegalArgumentException("no valid message format");
                    Blob attach = msg.getAttachments().elementAt(1);
                    pgp_txt = new String(attach.data, StandardCharsets.UTF_8);
                } else /* Inline */ {
                    pgp_txt = msg.getLongmsg();
                }

                String payload;
                try {
                    payload = stripFromPGP(pgp_txt);
                } catch (ParseException ex) {
                    // cannot parse this
                    throw new IllegalArgumentException("illegal encryption text");
                }
                String[] msgs = transportMT999(from, to, dir, msgid, "", payload, magicEnc);

                for (int j = 0; j < msgs.length; ++j) {
                    if (j > 0)
                        result += "\n";
                    result += msgs[j].toString();
                }

                if (msg.getEncFormat() == Message.EncFormat.Inline) {
                    String[] attached_key = null;
                    attachments = msg.getAttachments();

                    for (int i = 0; attachments != null && i < attachments.size(); ++i) {
                        Blob attach = attachments.elementAt(i);
                        if (attach.mime_type.compareTo("application/pgp-keys") == 0) {
                            // we send an MT999 with the keys
                            try {
                                payload = stripFromPGP(new String(attach.data, StandardCharsets.UTF_8));
                            } catch (ParseException ex) {
                                // cannot parse this
                                break;
                            }
                            msgs = transportMT999(from, to, dir, msgid, "", payload, magicKeys);

                            for (int j = 0; j < msgs.length; ++j) {
                                if (j > 0)
                                    result += "\n";
                                result += msgs[j].toString();
                            }
                        } else {
                            throw new UnsupportedOperationException(
                                    "only application/pgp-keys is supported with Inline but got " + attach.mime_type);
                        }
                    }
                }
                break;

            default:
                throw new UnsupportedOperationException("unsupported encryption format");
        }

        return result;
    }

    /**
     * Creates p≡p message from MTFIN or MTXML using SWIFT header info
     *
     * @param header MTFIN header structure
     * @param txt    MTFIN message text
     * @return p≡p message
     */

    protected Message pEpMessageFromSWIFTMessage(SWIFTMsg header, String txt) {
        Message m = new Message();

        Identity from = new Identity();
        from.address = encodeBIC(header.logicalTerminalAddress);
        m.setFrom(from);

        Identity to = new Identity();
        to.address = encodeBIC(header.destinationAddress);
        Vector<Identity> _to = new Vector<Identity>();
        _to.add(to);
        m.setTo(_to);

        m.setDir(header.inputIdentifier.compareTo("I") == 0 ? Message.Direction.Incoming : Message.Direction.Outgoing);

        m.setLongmsg(txt);

        ArrayList<Pair<String, String>> al = new ArrayList<Pair<String, String>>();
        Pair<String, String> field = new Pair<String, String>("X-pEp-Version", pEp.getProtocolVersion());
        al.add(field);
        m.setOptFields(al);
        return m;
    }

    /**
     * decodes p≡p Messages from a String with serialized p≡p for SWIFT messages.
     *
     * @param txt String to decode from
     * @throws ParseException if the String does not contain SWIFT messages only
     * @return array with p≡p Messages
     *
     */

    public Message[] decode(String txt) throws ParseException {
        Vector<Message> result = new Vector<Message>();

        if (txt.substring(0, 3).compareTo("{1:") == 0) {
            // probably SWIFT MTFIN

            int f = 0;
            int t = txt.indexOf("{1:", 3);
            if (t == -1)
                t = txt.length();

            String key_payload = "";
            String enc_payload = "";
            while (f < txt.length()) {
                String _txt = txt.substring(f, t);

                f = t;
                t = txt.indexOf("{1:", f + 3);
                if (t == -1)
                    t = txt.length();

                Message last = null;

                SWIFTMsg m = new SWIFTMsg();
                m.parseHeader(_txt);

                boolean done = false;
                if (m.messageType.compareTo("999") == 0) {
                    MT999 _m = new MT999(_txt);
                    String narrative = _m.narrative.trim();
                    if (narrative.substring(0, magicKeys.length()).compareTo(magicKeys) == 0) {
                        key_payload += narrative.substring(magicKeys.length()).trim();
                        if (key_payload.substring(key_payload.length()).compareTo(".") == 0) {
                            key_payload = key_payload.substring(0, key_payload.length() - 2);
                        } else {
                            // p≡p keys
                            String keydata = addPGPHeader(key_payload, pgptypePubkey);
                            key_payload = "";

                            if (last == null)
                                last = pEpMessageFromSWIFTMessage(m, "p≡p keys");

                            Vector<Blob> a = new Vector<Blob>();
                            Blob b = new Blob();
                            b.data = keydata.getBytes(StandardCharsets.UTF_8);
                            b.mime_type = "application/pgp-keys";
                            b.filename = "pEpKeys.asc";
                            a.add(b);
                            last.setAttachments(a);

                            result.add(last);
                            last = null;

                        }
                        done = true;
                    } else if (narrative.substring(0, magicEnc.length()).compareTo(magicEnc) == 0) {
                        // p≡p encrypted data
                        enc_payload += narrative.substring(magicEnc.length()).trim();
                        if (enc_payload.substring(enc_payload.length()).compareTo(".") == 0) {
                            enc_payload = enc_payload.substring(0, enc_payload.length() - 2);
                        } else {
                            // p≡p encryption
                            String encdata = addPGPHeader(enc_payload, pgptypeMessage);
                            Message r = pEpMessageFromSWIFTMessage(m, encdata);
                            r.setEncFormat(Message.EncFormat.Inline);
                            result.add(r);
                        }
                        done = true;
                    }
                }

                if (!done) {
                    last = pEpMessageFromSWIFTMessage(m, _txt);
                    result.add(last);
                }
            }
        } else if (txt.substring(0, 1).compareTo("<") == 0) {
            // probably XML

            // FIXME: let's do the job for MTXML, too
            throw new UnsupportedOperationException("XML not yet implemented");
        } else { // we don't know this format
            throw new ParseException("not a valid SWIFT message", 0);
        }

        Message[] _result = new Message[result.size()];
        return result.toArray(_result);
    }
}
