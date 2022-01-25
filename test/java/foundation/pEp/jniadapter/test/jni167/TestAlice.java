package foundation.pEp.jniadapter.test.jni167;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-167 - ASN1 Support

TODO:
just spot test that the encoding/decoding generally works. Dont test the whole codec.

*/


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXBase ctxBase = new CTXBase();

        new TestUnit<CTXBase>("toXER", ctxBase, ctx -> {
            log("fdfd");
            String tmp = ctx.msgAliceToAlice.toXER();
            log(tmp);
        });


        new TestUnit<CTXBase>("decodeASN1XER", ctxBase, ctx -> {
            log("fdfd");
            String msgXER = "<ASN1Message>\n" +
                    "      <from>\n" +
                    "          <address>alice@peptest.org</address>\n" +
                    "          <user-id></user-id>\n" +
                    "          <username>alice</username>\n" +
                    "          <comm-type>0</comm-type>\n" +
                    "          <lang>en</lang>\n" +
                    "      </from>\n" +
                    "      <to>\n" +
                    "          <PIdentity>\n" +
                    "              <address>bob@peptest.org</address>\n" +
                    "              <user-id></user-id>\n" +
                    "              <username>bob</username>\n" +
                    "              <comm-type>0</comm-type>\n" +
                    "              <lang>en</lang>\n" +
                    "          </PIdentity>\n" +
                    "      </to>\n" +
                    "      <shortmsg>Hi i am the shortMessage</shortmsg>\n" +
                    "      <longmsg>Hi i am the longMessage</longmsg>\n" +
                    "</ASN1Message>\n";

            Message m = Message.fromXER(msgXER);
            log(AdapterTestUtils.msgToString(m, false));
        });


        TestSuite.getDefault().run();
    }
}


