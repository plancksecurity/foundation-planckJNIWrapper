package foundation.pEp.jniadapter.test.jni135;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.model.Role;
import foundation.pEp.jniadapter.test.utils.model.TestModel;
import foundation.pEp.jniadapter.test.utils.model.TestNode;
import foundation.pEp.jniadapter.test.utils.model.pEpTestIdentity;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

/*
Test for JNI-135 - Heavy memory consumption - memory leaks
relates to:
* JNI-148 - Mem-mgmt: Defined behaviour of Message.close()
* JNI-160 - Mem-mgmt: Resource Instrumentation for class Message

We are simply encrypting and decrypting in a cycles with 2 own identities alice and bob.
The idea is that you fire up your mem-monitoring tool of choice and convince yourself about the
reality of the amount leaked memory in relation to the number of cycles.

Since JNI-160, this test also prints the result of Message.getInstanceCount(). Please see ticket for more details.

This test suite proofs 3 things:
* If you ignore mem-mgmt, you WILL leak memory
* If you do mem-mgmt right, you DONT leak memory
* If you lost all references to an unreleased message obj, it will be unreleasable forever.

To experiment with this, you can run a single test only and make use of the options:
* repeatCount - how many iterations/cycles
* msgSizeMB - attachement size of one message
* EncFormat - the encryption format

 To run a single test only, comment out the last line:
"TestSuite.getDefault().run();"

and run the TestUnit directly using TestUnit.run():

new TestUnit<ctxtype>("bla", new ctxType(), ctx -> {
    // code
}).run();

 */


// FOR TEST CONFIG DO NOT TWEAK HERE, THESE ARE DEFAULTS
class Jni135TestContext extends AbstractTestContext {
    // Model
    public TestModel<pEpTestIdentity, TestNode<pEpTestIdentity>> model = new TestModel(pEpTestIdentity::new, TestNode::new);

    // Basic
    public Engine engine;

    // Identities
    public Identity alice;
    public Identity bob;

    // Test config defaults
    public int repeatCount = 2000;
    public int msgSizeMB = 1;
    public Message.EncFormat encFormat = Message.EncFormat.PEPEncInlineEA;

    public Jni135TestContext init() throws Throwable {
        engine = new Engine();
        // fetch data for the idents
        alice = model.getIdent(Role.ALICE).pEpIdent;
        bob = model.getIdent(Role.BOB).pEpIdent;

        // create keys etc..
        alice = engine.myself(alice);
        bob = engine.myself(bob);

        return this;
    }
}

class TestAlice {
    public static void proofLeakNonLeak(Jni135TestContext ctx, boolean wannaLeak) {
        int cycles = 0;
        while (cycles < ctx.repeatCount) {
            Message msg1Plain = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
            Blob bigBlob = AdapterTestUtils.makeNewTestBlob(ctx.msgSizeMB * 1024 * 1024, "atti1", "text/plain");
            Vector<Blob> atts = new Vector<Blob>();
            atts.add(bigBlob);
            msg1Plain.setAttachments(atts);

            Message msg1Enc = ctx.engine.encrypt_message(msg1Plain, null, ctx.encFormat);
            decrypt_message_Return decRet = ctx.engine.decrypt_message(msg1Enc, null, 0);
            assert decRet != null : "could not decrypt message";

            if (!wannaLeak) {
                decRet.dst.close();
                msg1Enc.close();
                msg1Plain.close();
                log("cycle nr: " + cycles++ + " / Message.getInstanceCount(): " + Message.getInstanceCount());
                assert Message.getInstanceCount().get() == 0 : "Leaking messages";
            } else {
                log("cycle nr: " + cycles++ + " / Message.getInstanceCount(): " + Message.getInstanceCount());
                assert Message.getInstanceCount().get() > 0 : "We should be leaking messages, actually";
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        {
            Jni135TestContext ctxDontLeak = new Jni135TestContext();
            // Config
            ctxDontLeak.repeatCount = 3;
            ctxDontLeak.msgSizeMB = 1;

            new TestUnit<Jni135TestContext>("Proof leaking messages ", ctxDontLeak, ctx -> {
                proofLeakNonLeak(ctx, false);
            });
        }

        {
            Jni135TestContext ctxDoLeak = new Jni135TestContext();
            // Config
            ctxDoLeak.repeatCount = 3;
            ctxDoLeak.msgSizeMB = 1;

            new TestUnit<Jni135TestContext>("Proof NOT leaking messages ", ctxDoLeak, ctx -> {
                proofLeakNonLeak(ctx, true);
            });
        }

        {
            Jni135TestContext ctxLostRefs = new Jni135TestContext();
            // Config
            ctxLostRefs.repeatCount = 3;
            ctxLostRefs.msgSizeMB = 1;

            new TestUnit<Jni135TestContext>("Lost refs cant be recovered", ctxLostRefs, ctx -> {
                try {
                    proofLeakNonLeak(ctx, false);
                    assert false : "Lost references to message objects should never be recoverable";
                } catch (Throwable t) {
                }
// To run one individual test only
            })//.run(); // comment this in
            ;
        }
// AND
        TestSuite.getDefault().run(); // comment this out
    }
}


