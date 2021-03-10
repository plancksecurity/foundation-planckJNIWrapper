package foundation.pEp.jniadapter.test.jni135;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.lang.ref.WeakReference;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;


class Jni135TestContext extends CTXBase {
    @Override
    public CTXBase init() throws Throwable {
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
//        TestUtils.readKey();
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
        CTXBase jni135Ctx = new Jni135TestContext();

        Engine.setDebugLogEnabled(false);

        new TestUnit<CTXBase>("setDir() == getDir() ", new Jni135TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            int cycles = 0;
            while (true) {
                Message msg1Plain = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                Blob bigBlob = AdapterTestUtils.makeNewTestBlob(10000000, "atti1", "text/plain");
                Vector<Blob> atts = new Vector<Blob>();
                atts.add(bigBlob);
                msg1Plain.setAttachments(atts);

                if (false) {
                    Message msg1Enc = ctx.engine.encrypt_message(msg1Plain, null, Message.EncFormat.PEP);
                    decrypt_message_Return decRet = ctx.engine.decrypt_message(msg1Enc, null, 0);

                    assert decRet != null : "could not decrypt message";
//                    decRet.dst.close();
//                    msg1Enc.close();
//                    gc();
//                    msg1Plain.close();
                }
                log("cycles: " + cycles++);
            }

        }).run();


//        TestSuite.getDefault().run();
    }
}


