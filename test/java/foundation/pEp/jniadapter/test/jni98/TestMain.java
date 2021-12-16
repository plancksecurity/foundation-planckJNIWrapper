package foundation.pEp.jniadapter.test.jni98;

import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.jniadapter.test.utils.CTXBase;

import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;
import static foundation.pEp.pitytest.TestLogger.logH2;
import static foundation.pEp.jniadapter.test.utils.AdapterTestUtils.msgToString;

/*
JNI-98 - "Factory function for generating incoming message from PGP text"

Problem:
There must be a static function in class Engine, which is generating an encrypted
version of a Message, which is structured like messages coming out from encrypt_message()
when being used with Message.EncFormat.PEP. Additionally, it should work with inline format, too.
The signature is expected to be:

public static Message incomingMessageFromPGPText(String pgpText, Message.EncFormat encFormat)

Please see https://pep.foundation/jira/browse/JNI-98 for further discussion
*/


class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<CTXBase>("JNI-98 - Message.EncFormat.PEP", new CTXBase(), ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
            ctx.engine.set_comm_partner_key(ctx.bob, "F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1");
            // Make msg1 by encrypting msgToBob
            logH2("Create target Message");
            Message msg1 = ctx.engine.encrypt_message(ctx.msgAliceToBob, null, Message.EncFormat.PEP);
            log("\n" + msgToString(msg1, false));

            // Lets get the pgpText of the msg1, and the EncFormat
            String pgpText = Utils.toUTF16(msg1.getAttachments().elementAt(1).data);
            Message.EncFormat ef = msg1.getEncFormat();
            //TODO: setting encformat to 4 (PEP) but getting back 3 (PGPMIME)

            // Create msg2 by using incomingMessageFromPGPText with the pgpText and EncFormat from msg1
            logH2("incomingMessageFromPGPText()");
            Message msg2 = ctx.engine.incomingMessageFromPGPText(pgpText, Message.EncFormat.PEP);
            log("\n" + msgToString(msg2, false));

            logH2("Verify msg2");
            decrypt_message_Return result = null;
            result = ctx.engine.decrypt_message(msg2, new Vector<String>(), 0);
            log("\n" + msgToString(result.dst, false));
        }).run();

        new TestUnit<CTXBase>("JNI-98 - Message.EncFormat.PEP_enc_inline_EA", new CTXBase(), ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
            ctx.engine.set_comm_partner_key(ctx.bob, "F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1");
            // Make msg1 by encrypting msgToBob
            logH2("Create target Message");
            Message msg1 = ctx.engine.encrypt_message(ctx.msgAliceToBob, null, Message.EncFormat.PEPEncInlineEA);
            log("\n" + msgToString(msg1, false));

            // Lets get the pgpText of the msg1, and the EncFormat
            String pgpText = msg1.getLongmsg();
            Message.EncFormat ef = msg1.getEncFormat();

            // Create msg2 by using incomingMessageFromPGPText with the pgpText and EncFormat from msg1
            logH2("incomingMessageFromPGPText()");
            Message msg2 = ctx.engine.incomingMessageFromPGPText(pgpText, ef);
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


