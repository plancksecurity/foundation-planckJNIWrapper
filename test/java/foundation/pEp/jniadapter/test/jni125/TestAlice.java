package foundation.pEp.jniadapter.test.jni125;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(false);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<CTXBase>("enum Message.EncFormat verify .value", new CTXBase(), ctx -> {
            //TODO: This is stupid, we need a PityAssert()
            assert Message.EncFormat.None.value == 0 : "Message.EncFormat.None == " + Message.EncFormat.None.value + "; expected " + 0;
            assert Message.EncFormat.Pieces.value == 1 : "Message.Pieces.None  == " + Message.EncFormat.Pieces.value + "; expected " + 1;
            assert Message.EncFormat.Inline.value == 1 : "Message.Inline.None  == " + Message.EncFormat.Inline.value + "; expected " + 2;
            assert Message.EncFormat.SMIME.value == 2 : "Message.SMIME.None  == " + Message.EncFormat.SMIME.value + "; expected " + 3;
            assert Message.EncFormat.PGPMIME.value == 3 : "Message.PGPMIME.None  == " + Message.EncFormat.PGPMIME.value + "; expected " + 3;
            assert Message.EncFormat.PEP.value == 4 : "Message.PEP.None  == " + Message.EncFormat.PEP.value + "; expected " + 4;
            assert Message.EncFormat.PGPMIMEOutlook1.value == 5 : "Message.PGPMIMEOutlook1.None  == " + Message.EncFormat.PGPMIMEOutlook1.value + "; expected " + 5;
            assert Message.EncFormat.PEPEncInlineEA.value == 6 : "Message.PEPEncInlineEA.None  == " + Message.EncFormat.PEPEncInlineEA.value + "; expected " + 6;
            assert Message.EncFormat.PEPEncAuto.value == 255 : "Message.PEPEncAuto.None  == " + Message.EncFormat.PEPEncAuto.value + "; expected " + 255;
        });

        new TestUnit<CTXBase>("enum Message.EncFormat verify getByInt(0)", new CTXBase(), ctx -> {
            assert Message.EncFormat.getByInt(0) == Message.EncFormat.None;
            assert Message.EncFormat.getByInt(1) == Message.EncFormat.Inline;
            assert Message.EncFormat.getByInt(2) == Message.EncFormat.SMIME;
            assert Message.EncFormat.getByInt(3) == Message.EncFormat.PGPMIME;
            assert Message.EncFormat.getByInt(4) == Message.EncFormat.PEP;
            assert Message.EncFormat.getByInt(5) == Message.EncFormat.PGPMIMEOutlook1;
            assert Message.EncFormat.getByInt(6) == Message.EncFormat.PEPEncInlineEA;
            assert Message.EncFormat.getByInt(255) == Message.EncFormat.PEPEncAuto;
        });

        TestSuite.getDefault().run();
    }
}


