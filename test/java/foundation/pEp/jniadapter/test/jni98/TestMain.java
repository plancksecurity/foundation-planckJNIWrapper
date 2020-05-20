package foundation.pEp.jniadapter.test.jni98;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import static foundation.pEp.jniadapter.test.utils.AdapterTestUtils.*;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.function.Consumer;

/*
JNI-98 - "Factory function for generating incoming message from PGP text"

Problem:
There must be a static function in class Engine, which is generating an encrypted
version of a Message, which is structured like messages coming out from encrypt_message()
when being used with Message.EncFormat.PEP. Additionally, it should work with inline format, too.
The signature is expected to be:

public static Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat)

Solution:
First, we need to know how the Message Object coming out from encrypt_message() are looking.
Then, we try to get the same Message from "incomingMessageFromPGPText()"
 */

/*
The one person we know is called bob and his pubkey is like this
bob.address = bob@peptest.org
bob.FPR = F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1
*/
class TestEnv {
    public Sync.DefaultCallback cb = new Sync.DefaultCallback();
    public Identity alice = new Identity();
    public Identity bob = new Identity();
    public Message msgToSelf;
    public Message msgToBob;
    public Vector<Identity> vID = new Vector<Identity>();
    public Vector<String> vStr = new Vector<String>();
    public byte[] keyBobPub;
    private String fileNameKeyBobPub = "../resources/test_keys/bob-pub.asc";
    public Engine engine = new Engine();

    public TestEnv() throws Exception {
        // We are alice
        alice.address = "alice@peptest.org";
        alice.user_id = "23";
        alice.me = true;
        alice = engine.myself(alice);

        // We know Bob and his pubkey
        try {
            Path path = Paths.get(fileNameKeyBobPub);
            keyBobPub = Files.readAllBytes(path);
        } catch (Exception e) {
            log("Could not open key file:" + fileNameKeyBobPub);
            throw e;
        }
        engine.importKey(keyBobPub);
        bob.address = "bob@peptest.org";

        msgToSelf = makeNewMessage(alice, alice, Message.Direction.Outgoing);
        msgToBob = makeNewMessage(alice, bob, Message.Direction.Outgoing);

        vID.add(bob);
        vStr.add("");
    }

    public static Message makeNewMessage(Identity from, Identity to, Message.Direction dir) {
        Message msg = new Message();
        Vector<Identity> vID = new Vector<Identity>();
        vID.add(to);

        msg.setFrom(from);
        msg.setTo(vID);
        msg.setDir(dir);
        msg.setLongmsg("Hi i am the message longmsg");
        return msg;
    }
}

class TestUnit {
    TestEnv env;
    String testUnitName = "default test unit";
    Consumer<TestEnv> lambda;

    public TestUnit(String name, Consumer<TestEnv> consumer) throws Exception {
        testUnitName = name;
        lambda = consumer;
        env = new TestEnv();

    }

    public void run() {
        logH1(testUnitName);
        try {
            lambda.accept(env);
        } catch (Throwable e) {
            logH1("TestUnit FAILED: " + e.toString());
            return;
        }
        logH2("SUCCESS!");
    }
}


class TestMain {
    public static void main(String[] args) throws Exception {
        testRunNew();
    }

    public static void testRunNew() throws Exception {
        new TestUnit("JNI-98 - Message.EncFormat.PEP", env -> {
            // Make msg1 by encrypting msgToBob
            logH2("Create target Message");
            Message msg1 = env.engine.encrypt_message(env.msgToBob, null, Message.EncFormat.PEP);
            log("\n" + msgToString(msg1, false));
            log("EncPep:" + Message.EncFormat.PEP.value);

            // Lets get the pgpText of the msg1, and the EncFormat
            String pgpText = Engine.toUTF16(msg1.getAttachments().elementAt(1).data);
            Message.EncFormat ef = msg1.getEncFormat();
            log("EncPepReturn:" + ef.value);
            //TODO: setting encformat to 4 (PEP) getting back 3 (PGPMIME)

            // Create msg2 by using incomingMessageFromPGPText with the pgpText and EncFormat from msg1
            logH2("incomingMessageFromPGPText()");
            Message msg2 = env.engine.incomingMessageFromPGPText(pgpText, Message.EncFormat.PEP);
            log("\n" + msgToString(msg2, false));
            logH2("Verify msg2");
            Engine.decrypt_message_Return result = null;
            result = env.engine.decrypt_message(msg2, env.vStr, 0);
            log("\n" + msgToString(result.dst, false));
        }).run();

        new TestUnit("JNI-98 - Message.EncFormat.PEP_enc_inline_EA", env -> {
            // Make msg1 by encrypting msgToBob
            logH2("Create target Message");
            Message msg1 = env.engine.encrypt_message(env.msgToBob, null, Message.EncFormat.PEPEncInlineEA);
            log("\n" + msgToString(msg1, false));

            // Lets get the pgpText of the msg1, and the EncFormat
            String pgpText = msg1.getLongmsg();
            Message.EncFormat ef = msg1.getEncFormat();

            // Create msg2 by using incomingMessageFromPGPText with the pgpText and EncFormat from msg1
            logH2("incomingMessageFromPGPText()");
            Message msg2 = env.engine.incomingMessageFromPGPText(pgpText, ef);
            log("\n" + msgToString(msg2, false));

            // Cant be just simply decrypted again
            // And thats correct according to fdik
            //[21:29] <        heck> | Assertion failed: (value && size && mime_type && code && !code[0] && code_size), function decode_internal, file internal_format.c, line 113.
            //[21:31] <        fdik> | ja
            //[21:31] <        fdik> | auch das ist korrekt
            //[21:31] <        fdik> | wenn Du EA verwendest, dann geht es nicht, dass man die Nachricht so wie sie ist wieder decrypted
            //[21:31] <        fdik> | sondern das geht nur, wenn man sie zerlegt
            //[21:32] <        fdik> | dafür ist das Verfahren da
            //[21:34] <        fdik> | ich hab einen Test dafür geschrieben
            //[21:34] <        fdik> | pEpEngine/test/src/ElevatedAttachmentsTest.cc
            //[21:34] <        fdik> | in default
            //[21:35] <        fdik> | Doku hier https://dev.pep.foundation/Engine/ElevatedAttachments
            //[21:35] <        fdik> | siehe hier:
            //[21:35] <        fdik> | https://dev.pep.foundation/Engine/ElevatedAttachments#support-in-message-api
        }).run();
    }
}


