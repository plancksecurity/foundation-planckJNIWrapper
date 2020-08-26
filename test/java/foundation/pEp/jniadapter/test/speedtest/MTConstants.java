package foundation.pEp.jniadapter.test.speedtest;

import java.util.regex.Pattern;

public class MTConstants {

    /**
     * Rendering Format of a SWIFT message.
     */

    enum Format {
        MTFIN, MTXML
    }

    /**
     * SWIFTMessages have a Basic Header Block and an Application Header Block
     */

    static final String mt_regex = "(?ms)"
            // Basic Header Block
            + "\\{1:"                  //
            + "(?<ai>\\w)"             //
            + "(?<si>\\d{2})"          //
            + "(?<lta>\\w{12})"        //
            + "(?<sn>\\d{4})"          //
            + "(?<sqn>\\d{5,6})"         //
            + "\\}"

            // Application Header Block
            + "\\{2:"                  //
            + "(?<ii>I|O)"             //
            + "(?<mt>\\d{3})"          //
            + "(\\d{10})?"          //
            + "(?<da>\\w{12})"         //
            + "(.*)?"                  //
            + "(?<mp>U|N|S)"           //
            + "\\}"

            // FIXME: User Header Block (the wrong way). The trailing .* is to make it useable for basic swift and mt999 ...
            + "(\\{3:?(.*)\\})?(.*)"    //
            ;

    static final Pattern mt_pattern = Pattern.compile(mt_regex);

    static final String mt999_regex = mt_regex
            // Text Block
            + "\\{4:\n"                      //
            + ":20:(?<trn>\\w{1,16})\n"     //
            + "(:21:(?<rr>.{1,16})\n)?"  //
            + ":79:(?<narrative>.*?)"      //
            + "\n-\\}"                     //
            // trailer
            + ".*"

            ;

    static final Pattern mt999_pattern = Pattern.compile(mt999_regex);

}

