package foundation.pEp.jniadapter.test.jni143;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.StopWatch;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.SlowFilteredNoise;
import foundation.pEp.pitytest.utils.RangeInt;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Arrays;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;
import static foundation.pEp.pitytest.TestLogger.logH2;


/*
Test: Add Attachments Enc/Dec Tests
Verifies equality of attachments before encryption and after decryption.
Generates attachment data from random A-Z letters.
Starts with attachment size 1 byte.
For each encrypt/decrypt cycle, doubles the size of the attachment data.
assert dataIn == dataOut
Logs the count of differing bytes.

*/

class CTXJNI143 extends CTXBase {
    @Override
    public CTXBase init() throws Throwable {
        super.init();
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
//        TestUtils.readKey();

        new TestUnit<CTXBase>("Attachement sizes", new CTXJNI143(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            int attachmentSizeBytes = 1;
            while (attachmentSizeBytes <= 10000000) {
                Message msg1Plain = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                Blob origBlob = AdapterTestUtils.makeNewTestBlob(attachmentSizeBytes, "atti1", null);
                Vector<Blob> atts = new Vector<Blob>();
                atts.add(origBlob);
                msg1Plain.setAttachments(atts);

                logH2("attachment size: " + attachmentSizeBytes);
                Message msg1Enc = ctx.engine.encrypt_message(msg1Plain, null, Message.EncFormat.PEP);

                decrypt_message_Return decRet = ctx.engine.decrypt_message(msg1Enc, null, 0);
                assert decRet != null : "could not decrypt message";
                if (decRet != null) {
                    assert decRet.dst.getAttachments().size() == 1 : "more than 1 attachment";
                    byte[] decBlobData = decRet.dst.getAttachments().get(0).data;
                    boolean attachmentsDiffer = !Arrays.equals(origBlob.data, decBlobData);
                    if (attachmentsDiffer) {
//                        log(new String(decBlobData));
                        log("attachments decrypted dont equal original");
                        log(AdapterTestUtils.diff(origBlob.data, decBlobData).toString());
                    }
                    assert !attachmentsDiffer : "attachments decrypted dont equal original";
                    assert decRet.dst.getLongmsg().equals(msg1Plain.getLongmsg()) : "LongMessage decrypted dont equal original";
                }

                attachmentSizeBytes *= 2;
            }
        }).run();

        // TODO:
        new TestUnit<CTXBase>("Attachement sizes", new CTXBase(), ctx -> {
            SlowFilteredNoise sfn = new SlowFilteredNoise();
            sfn.addSelection(new RangeInt(0,10));
            sfn.addSelection(new RangeInt(50,100));
            sfn.addFilter(new RangeInt(60,89));
            log(sfn.toString());
        });

//        TestSuite.getDefault().run();
    }
}


