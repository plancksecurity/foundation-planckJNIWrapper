package foundation.pEp.jniadapter.test.speedtest;

import java.text.ParseException;
import java.util.regex.*;

/**
 * MT999 is a Free Format Message.
 */

public class MT999 extends SWIFTMsg {

    String trn;
    String rr;
    String narrative;

    /**
     * Construct MT999 message by parsing.
     *
     * @param txt text to parse
     * @throws ParseException if not a valid MT999 message
     */

    public MT999(String txt) throws ParseException {
        Matcher m = MTConstants.mt999_pattern.matcher(txt);
        if (!m.matches())
            throw new ParseException("not a valid MT999 message", 0);

        // retrieve Basic Header and Application Header fields
        retrieveHeader(m);

        // no User Header Block

        // Text Block
        trn = m.group("trn");
        rr = m.group("rr");
        narrative = m.group("narrative");
    }

    /**
     * Construct MT999 message.
     *
     * @param srcAddress is sender's address
     * @param dstAddress is receiver's address
     * @param dir        <code>"I"</code> for incoming message <code>"O"</code> for
     *                   outgoing message
     * @param id         transaction id
     * @param payload    freeform text
     * @param session    session number
     * @param sequence   sequence number
     */

    public MT999(String srcAddress, String dstAddress, String dir, String id, String relref, String payload,
                 String session, String sequence) {

        if (srcAddress.length() != 12)
            throw new IllegalArgumentException("srcAddress must be 12 characters");
        if (dstAddress.length() != 12)
            throw new IllegalArgumentException("dstAddress must be 12 characters");
        if (dir.compareTo("I") != 0 && dir.compareTo("O") != 0)
            throw new IllegalArgumentException("dir must be I or O");
        if (id.length() < 1 || id.length() > 16)
            throw new IllegalArgumentException("id must be between 1 and 12 characters");
        if (relref.length() > 16)
            throw new IllegalArgumentException("related reference must be at most 16 characters");
        if (payload.length() < 1)
            throw new IllegalArgumentException("payload must have a value");

        if (session.length() != 4)
            throw new IllegalArgumentException("session must be 4 digits");
        if (sequence.length() != 6)
            throw new IllegalArgumentException("sequence must be 6 digits");

        mt999();

        logicalTerminalAddress = srcAddress;
        destinationAddress = dstAddress;
        inputIdentifier = dir;

        trn = id;
        rr = relref;
        narrative = payload;

        sessionNumber = session;
        sequenceNumber = sequence;
    }

    /**
     * Construct MT999 message with defaults for session and sequence.
     *
     * @param srcAddress is sender's address
     * @param dstAddress is receiver's address
     * @param dir        <code>"I"</code> for incoming message <code>"O"</code> for
     *                   outgoing message
     * @param id         transaction id
     * @param payload    freeform text
     */

    public MT999(String srcAddress, String dstAddress, String dir, String id, String relref, String payload) {


        if (srcAddress.length() != 12)
            throw new IllegalArgumentException("srcAddress must be 12 characters");
        if (dstAddress.length() != 12)
            throw new IllegalArgumentException("dstAddress must be 12 characters");
        if (dir.compareTo("I") != 0 && dir.compareTo("O") != 0)
            throw new IllegalArgumentException("dir must be I or O");
        if (id.length() < 1 || id.length() > 16)
            throw new IllegalArgumentException("id must be between 1 and 12 characters");
        if (relref.length() > 16)
            throw new IllegalArgumentException("related reference must be at most 16 characters");
        if (payload.length() < 1)
            throw new IllegalArgumentException("payload must have a value");

        mt999();

        logicalTerminalAddress = srcAddress;
        destinationAddress = dstAddress;
        inputIdentifier = dir;

        trn = id;
        rr = relref;
        narrative = payload;

        sessionNumber = "0000";
        sequenceNumber = "000000";
    }

    /**
     * Convert MT999 message by composing.
     *
     * @param fmt rendering format
     * @return string representation of MT999 message
     */

    public String toString(MTConstants.Format fmt) {
        String result;
        // FIXME: rework to stringbuffer
        switch (fmt) {
            case MTFIN:
                // Basic Header Block
                result = "{1:";
                result += applicationIdentifier;
                result += serviceIdentifier;
                result += logicalTerminalAddress;
                result += sessionNumber;
                result += sequenceNumber;
                result += "}";

                // Application Header Block
                result += "{2:";
                result += inputIdentifier;
                result += messageType;
                result += destinationAddress;
                result += messagePriority;
                result += "}";

                // no User Header Block

                // Text Block
                result += "{4:\n";
                result += ":20:" + trn + "\n";
                if (rr != null && rr.length() > 0)
                    result += ":21:" + rr +"\n";
                result += ":79:\n" + narrative;
                result += "\n-}";
                return result;

            case MTXML:
                throw new UnsupportedOperationException("MTXML not yet implemented");
        }

        throw new AssertionError("this should never happen");
    }

    /**
     * Convert MT999 message by composing.
     *
     * @return string representation of MT999 message
     */

    public String toString() {
        return toString(MTConstants.Format.MTFIN);
    }

    // MT999 specifica

    private void mt999() {
        applicationIdentifier = "F"; // FIN
        serviceIdentifier = "01"; // FIN
        messageType = "999";
        messagePriority = "N";
    }

}
