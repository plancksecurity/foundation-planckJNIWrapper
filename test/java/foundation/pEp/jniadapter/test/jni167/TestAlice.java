package foundation.pEp.jniadapter.test.jni167;

import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-167 - ASN1 Support

*/


class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        CTXBase ctxBase = new CTXBase();

        new TestUnit<CTXBase>("encodeASN1XER", ctxBase, ctx -> {
            log("fdfd");
            String tmp = ctx.msgAliceToAlice.encodeASN1XER();
            log(tmp);
        });

        TestSuite.getDefault().run();
    }
}


