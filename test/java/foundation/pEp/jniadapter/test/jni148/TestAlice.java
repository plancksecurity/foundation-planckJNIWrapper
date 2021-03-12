package foundation.pEp.jniadapter.test.jni148;


import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-148 - Mem-mgmt: Defined behaviour of Message.close()

Required behaviour of Message.close()
* Free the memory of the c-struct Message (not proven here)
* Idempotent (double free safe)
* Accessing Message after close() results in a IllegalStateException

*/

class CTX148 extends CTXBase {

    Message msgAliceToBobEnc;
    Message msgAliceToBobEncDec;

    @Override
    public CTXBase init() throws Throwable {
        super.init();
        alice = engine.myself(alice);
        bob = engine.myself(bob);

        msgAliceToBob.setAttachments(attachmentList.getAttachments());
        msgAliceToBob.setFrom(alice);
        Vector<Identity> to = new Vector<>();
        to.add(bob);
        msgAliceToBob.setTo(to);

        msgAliceToBobEnc = engine.encrypt_message(msgAliceToBob, null, Message.EncFormat.PEP);
        decrypt_message_Return decRet = engine.decrypt_message(msgAliceToBobEnc, null, 0);
        if (decRet.dst != null) {
            msgAliceToBobEncDec = decRet.dst;
        }
        log(AdapterTestUtils.msgToString(msgAliceToBobEncDec, false));
        return this;
    }

}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<CTX148>("close() idempotent / double free safe ", new CTX148(), ctx -> {
            assert ctx.msgAliceToBobEncDec.getAttachments() != null;
            ctx.msgAliceToBobEncDec.close();
            ctx.msgAliceToBobEncDec.close();
            ctx.msgAliceToBobEncDec.close();
            ctx.msgAliceToBobEncDec.close();
            ctx.msgAliceToBobEncDec.close();
        });

        new TestUnit<CTX148>("after close(): exception on access", new CTX148(), ctx -> {
            assert ctx.msgAliceToBobEncDec.getAttachments() != null;
            ctx.msgAliceToBobEncDec.close();
            boolean exceptionThrown = false;
            try {
                assert ctx.msgAliceToBobEncDec.getAttachments() != null;
            } catch (IllegalStateException e) {
                exceptionThrown = true;
            }
            assert exceptionThrown: "no exception thrown";
        });

        TestSuite.getDefault().run();
    }
}


