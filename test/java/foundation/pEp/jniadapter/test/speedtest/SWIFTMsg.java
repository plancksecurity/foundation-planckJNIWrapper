package foundation.pEp.jniadapter.test.speedtest;

import java.text.ParseException;
import java.util.regex.*;

public class SWIFTMsg {
    // Basic Header Block
    public String applicationIdentifier;
    public String serviceIdentifier;
    public String logicalTerminalAddress;
    public String sessionNumber;
    public String sequenceNumber;

    // Application Header Block
    public String inputIdentifier;
    public String messageType;
    public String destinationAddress;
    public String messagePriority;

    /**
     * retrieve header fields of Basic Header Block and Application Header
     * Block
     *
     * @param m             Matcher object of a regex result
     */

    public void retrieveHeader(Matcher m) {
        // Basic Header Block
        applicationIdentifier = m.group("ai");
        serviceIdentifier = m.group("si");
        logicalTerminalAddress = m.group("lta");
        sessionNumber = m.group("sn");
        sequenceNumber = m.group("sqn");

        // Application Header Block
        inputIdentifier = m.group("ii");
        messageType = m.group("mt");
        destinationAddress = m.group("da");
        messagePriority = m.group("mp");
    }

    /**
     * parse MTFIN header and retrieve header fields into variables.
     *
     * @param txt               MTxxx message to parse
     * @throws ParseException   if not a valid MTxxx message
     */

    public void parseHeader(String txt) throws ParseException {
//            String header = txt.substring(0, 50);

        Matcher m = MTConstants.mt_pattern.matcher(txt);
        if (!m.matches())
            throw new ParseException("not a valid MTxxx message", 0);

        retrieveHeader(m);
    }
}
