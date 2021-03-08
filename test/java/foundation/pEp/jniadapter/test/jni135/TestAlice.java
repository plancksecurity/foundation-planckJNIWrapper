package foundation.pEp.jniadapter.test.jni135;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.AdapterBaseTestContext;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;
import static foundation.pEp.pitytest.TestLogger.logH2;

class Jni135TestContext extends AdapterBaseTestContext {
    @Override
    public AdapterBaseTestContext init() throws Throwable {
        super.init();
        return this;
    }
}

class TestAlice {
    public static void gc() {
//        log("gc start");
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while (ref.get() != null) {
            System.gc();
        }
//        log("gc end");
    }

    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
//        TestUtils.readKey();
        AdapterBaseTestContext jni135Ctx = new Jni135TestContext();

        new TestUnit<AdapterBaseTestContext>("setDir() == getDir() ", new Jni135TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            while (true) {
                Message msg1Plain = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                Blob bigBlob = AdapterTestUtils.makeNewTestBlob(100000, "atti1", "text/plain");
                Vector atts = new Vector<Blob>();
                atts.add(bigBlob);
                msg1Plain.setAttachments(atts);

//                log(new String(bigBlob.data));
                logH2("msg plain", TestUtils.TermColor.CYAN);
                log(AdapterTestUtils.msgToString(msg1Plain, false));
//                log("plain:     " + msg1Plain.getId());
                Message msg1Enc = ctx.engine.encrypt_message(msg1Plain, null, Message.EncFormat.PEP);

//                logH2("msg encrypted", TestUtils.TermColor.CYAN);
//                log(AdapterTestUtils.msgToString(msg1Enc, false));
//                log("encrypted: " + msg1Enc.getId());

                decrypt_message_Return decRet = ctx.engine.decrypt_message(msg1Enc, null, 0);
                assert decRet != null : "could not decrypt message";
                if (decRet != null) {
                    logH2("msg decrypted", TestUtils.TermColor.CYAN);
                    log(AdapterTestUtils.msgToString(decRet.dst, false));
//                    log("decrypted: " + decRet.dst.getId());
//                    assert new String(decRet.dst.getAttachments().get(0).data).equals(new String(bigBlob.data)) : "attachments decrypted dont equal original";
                    assert decRet.dst.getLongmsg().equals(msg1Plain.getLongmsg()) : "attachments decrypted dont equal original";
                }

                msg1Plain = null;
                msg1Enc = null;
                decRet = null;
                gc();
            }
        });

        TestSuite.getDefault().run();
    }
}


