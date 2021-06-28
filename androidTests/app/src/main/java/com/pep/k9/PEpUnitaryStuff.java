package com.pep.k9;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.decrypt_message_Return;

import java.util.Vector;

public class PEpUnitaryStuff {
    public static void main (String args[]) {
        try {
            shouldDoSomeStuff();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void shouldDoSomeStuff() throws Exception {
        Engine engine;
        engine = new Engine();

        Message msg = new Message();
        msg.setFrom(new Identity());

        Vector<Identity> to = new Vector<>();
        to.add(new Identity());
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        String message = "this is a test";
        msg.setLongmsg(message);

        msg.setDir(Message.Direction.Outgoing);

        Vector<Identity> cc = new Vector<>();
        cc.add(new Identity());
        msg.setCc(cc);

        Message encriptedMessage = engine.encrypt_message(msg, null, Message.EncFormat.PEP);
        decrypt_message_Return decrypt_message_return = engine.decrypt_message(encriptedMessage, new Vector<>(), 0);

        if (!decrypt_message_return.dst.getLongmsg().equals(message)) {
            throw new RuntimeException("FAILED");
        }
    }
}
