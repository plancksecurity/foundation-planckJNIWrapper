package foundation.pEp.jniadapter.test.jni91;
import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.exceptions.*;

import java.lang.Thread;


/*
This test is trying to reproduce the problem described in JNI-91
https://pep.foundation/jira/browse/JNI-81

`engine.key_reset_identity` and `engine.setMessageToSendCallback`
*/


class KeyResetThread extends Thread {
    private String threadName = "Key Reset Thread";
    private Engine engine = null;
    private Identity me = null;

    KeyResetThread() {
    }

    private Identity getOwnIdentity(String name) {
        System.out.print(threadName + ": getOwnIdentity: \"" + name + "\"");
        Identity i = new Identity();
        i.me = true;
        i.user_id = name;
        i.username = name;
        i.address = name + "@test.org";
        i = engine.myself(i);
        System.out.println("FPR: \"" + i.fpr + "\"");
        return i;
    }

    public void run() {
        System.out.println(threadName + ": Starting thread: ");

        try {
            // load engine
            try {
                engine = new Engine();
                SyncCallbacks callbacks = new SyncCallbacks();
                engine.setMessageToSendCallback(callbacks);
                engine.setNotifyHandshakeCallback(callbacks);
            }
            catch (pEpException ex) {
                System.out.println(threadName + ": cannot load");
                System.exit(0);
                return;
            }
            System.out.println(threadName + ": Engine loaded");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) { }

            // Get myself
            me = this.getOwnIdentity("BAlice");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) { }

//            // start sync
//            System.out.println(threadName + ": engine.startSync()");
//            engine.startSync();
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException ex) { }

            // key reset
            System.out.println(threadName + ": key_reset_identity()");
//            engine.key_reset_identity(me,null);
            engine.key_reset_all_own_keys();
            try {
                Thread.sleep(20000);
            } catch (InterruptedException ex) { }
        } catch (Exception e) {
            System.out.println("Exception in Thread " + threadName);
            System.out.println(e.toString());
            System.exit(0);
        }
        System.out.println(threadName + ": DONE");
    }
}

class SyncCallbacks implements Sync.MessageToSendCallback, Sync.NotifyHandshakeCallback {
    public void messageToSend(Message message)
    {
        System.out.println("================================");
        System.out.println("Message to send called");
        System.out.println("From: " + message.getFrom());
        System.out.println("To: " + message.getTo());
        System.out.println("Subject: " + message.getShortmsg());
        System.out.println("Attachement[0]: " + message.getAttachments().get(0).toString());
        System.out.println("================================");
    }

    public void notifyHandshake(Identity myself, Identity partner, SyncHandshakeSignal signal)
    {
        System.out.println("================================");
        System.out.println("Notify handshake called");
        System.out.println("Myself: " + myself);
        System.out.println("Partner: " + partner);
        System.out.println("Signal: " + signal);
        System.out.println("================================");
    }
}


class TestMain {
    public static void main(String[] args) {
        Thread kr = new KeyResetThread();

        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException ex) { }
        kr.start();
    }
}
