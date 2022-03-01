package foundation.pEp.jniadapter.test.jni171;

import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.Pair;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.ArrayList;
import java.util.List;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-171 - Test: Opt Fields does trimming after en/decryption

Expected Behaviour:
Values of OptFields are NOT trimmed or otherwise altered in an encrypt/decrypt roundtrip
*/

class Jni171TestContext extends CTXBase {
    @Override
    public CTXBase init() throws Throwable {
        super.init();
        alice = engine.myself(alice);
        engine.importKey(keyBobPub);
        return this;
    }
}

class TestAlice {

    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        Jni171TestContext ctx171 = new Jni171TestContext();

        new TestUnit<CTXBase>("OptField values DONT get leading/trailing whitespace removed", ctx171, ctx -> {
            ArrayList<Pair<String, String>> optsIn = new ArrayList<>();
            optsIn.add(new Pair<>("six-spaces", "      "));
            optsIn.add(new Pair<>("pEp-surrounded-by-ws", " pEp "));
            ctx.msgAliceToBob.setOptFields(optsIn);
            log("ORIG: " + AdapterTestUtils.OptFieldsToString(ctx.msgAliceToBob.getOptFields()));
            // encrypt
            Message msgEnc = ctx.engine.encrypt_message(ctx.msgAliceToBob, null, Message.EncFormat.PEP);
            log("ENC: " + AdapterTestUtils.OptFieldsToString(msgEnc.getOptFields()));

            // decrypt
            decrypt_message_Return decRet = ctx.engine.decrypt_message(msgEnc, null, 0);
            log("DEC: " + AdapterTestUtils.OptFieldsToString(decRet.dst.getOptFields()));

            ArrayList<Pair<String, String>> optsOut = decRet.dst.getOptFields();
            assert AdapterTestUtils.optFieldValuesOfKey(optsOut, "six-spaces").get(0).equals("      ");
            assert AdapterTestUtils.optFieldValuesOfKey(optsOut, "pEp-surrounded-by-ws").get(0).equals(" pEp ");
        });

        TestSuite.getDefault().run();
    }
}


