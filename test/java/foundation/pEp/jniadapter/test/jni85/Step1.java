package foundation.pEp.jniadapter.test.jni85;
import foundation.pEp.jniadapter.*;
import java.util.Vector;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.Thread;
import java.lang.InterruptedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class Step1 {

    public static void main(String[] args) {
        new Thread(() -> {
            Engine e = null;

        // load
            try {
                e = new Engine();
                SyncCallbacks callbacks = new SyncCallbacks();
                //e.setNotifyHandshakeCallback(callbacks);
                e.setMessageToSendCallback(callbacks);
            }
            catch (pEpException ex) {
                System.out.println("Cannot load");
                System.exit(-1);
            }
            System.out.println("Test loaded");
    
            
            // Keygen
            Engine en = new Engine();
            System.out.println("Generating keys: ");
            Identity user2 = new Identity();
            user2.user_id = "pEp_own_userId";
            user2.me = true;
            user2.username = "Test User 2";
            user2.address = "jniTestUser2@peptest.ch";
            user2 = en.myself(user2);
            System.out.print("Keys generated: ");
            System.out.println(user2.fpr);

            // it's not necessary - you can just shutdown Sync and that's it
            // but for this test give sync a chance to process all messages
            try {
                Thread.sleep(200);
                System.out.println("End wait");
            }
            catch (InterruptedException ex) { }

            }).start();

        throw new RuntimeException();
    }
}
