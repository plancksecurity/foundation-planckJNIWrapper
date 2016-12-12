package com.pep.k9;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pEp.jniadapter.Engine;
import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;

import java.util.Vector;

/**
 * Created by arturo on 15/11/16.
 */
public class UnitaryTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void shouldDoSomeStuff() throws Exception {
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

        Message encriptedMessage = engine.encrypt_message(msg, null);
        Engine.decrypt_message_Return decrypt_message_return = engine.decrypt_message(encriptedMessage);

        Assert.assertTrue(decrypt_message_return.dst.getLongmsg().equals(message));
    }
}