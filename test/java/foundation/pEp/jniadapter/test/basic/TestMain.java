package foundation.pEp.jniadapter.test.basic;
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

class TestMain {
    public void printClassPath() {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }
    }

    public static void main(String[] args) {
        Engine engine;
        // load
        try {
            engine = new Engine();
            SyncCallbacks callbacks = new SyncCallbacks();
//            e.setNotifyHandshakeCallback(callbacks);
            engine.setMessageToSendCallback(callbacks);
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            return;
        }
        System.out.println("Test loaded");

        // Keygen
        System.out.println("Generating keys: ");
        Identity user = new Identity();
        user.user_id = "pEp_own_userId";
        user.me = true;
        user.username = "Test User";
        user.address = "jniTestUser@peptest.ch";
        user = engine.myself(user);
        System.out.print("Keys generated: ");
        System.out.println(user.fpr);

        // Import key
        try {
            Path path = Paths.get("../resources/test_keys/pub/pep-test-alice-0x6FF00E97_pub.asc");
            byte[] key = Files.readAllBytes(path);
            engine.importKey(key);
        } catch (IOException exception) {
            System.out.println("Could not import key");
            exception.printStackTrace();

        }
        // trustwords
        Identity alice = new Identity();
        alice.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";
        String t = engine.trustwords(alice);
        System.out.print("Trustwords: ");
        System.out.println(t);

        // message
        Message msg = new Message();

        msg.setFrom(user);

        Vector<Identity> to = new Vector<Identity>();
        Identity to1 = new Identity();
        to1.username = "pEp Test Alice (test key don't use)";
        to1.address = "pep.test.alice@pep-project.org";
        to1.user_id = "42";
        to.add(to1);
        //to.add(user);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");
        msg.setDir(Message.Direction.Outgoing);

        // Test setAttachements() with nrAttachemnts
        int nrAttachemnts = 3;
        {
            System.out.print("Adding " + nrAttachemnts + " attachements [");
            Vector<Blob> attachments = new Vector<>();

            for (int i = 0; i < nrAttachemnts; i++) {
                Blob blb = new Blob();
                String dataString = "Attachement nr: " + i + " [TEST DATA]";
                blb.data = dataString.getBytes();
                blb.filename = "testfilename.txt";
                attachments.add(blb);
                System.out.print(".");
            }
            msg.setAttachments(attachments);
            System.out.println("]");
        }

        Message enc = null;
        try {
            enc = engine.encrypt_message(msg, null, Message.EncFormat.PEP);
            System.out.println("encrypted");
        }
        catch (pEpException ex) {
            System.out.println("cannot encrypt");
            ex.printStackTrace();
        }

        System.out.println(enc.getLongmsg());

        try {
            System.out.println("Rating preview: " + engine.outgoing_message_rating_preview(msg));
            System.out.println("Rating" + engine.outgoing_message_rating(msg));
        }
        catch (pEpException ex) {
            System.out.println("cannot measure outgoing message rating");
        }

        Engine.decrypt_message_Return result = null;
        try {
            result = engine.decrypt_message(enc, new Vector<>(), 0);
            System.out.println("decrypted");
        }
        catch (pEpException ex) {
            System.out.println("cannot decrypt");
            ex.printStackTrace();
        }


        System.out.println(result.dst.getShortmsg());
        System.out.println(result.dst.getLongmsg());

        // Test getAttachments()
        {
            Vector<Blob> attachments = result.dst.getAttachments();

            System.out.println("get attachement data");
            System.out.println("Attachement count: " + attachments.size());
            for( Blob a: attachments) {
                System.out.println("Attachement nr: " + attachments.indexOf(a));
                System.out.println("[");
                System.out.println(a.toString());
                System.out.println("]");
            }
        }

        System.out.println("TEST DONE - FINISHED");

        try {
            engine.key_reset_all_own_keys();
        }
        catch (pEpException ex) {
            System.out.println("cannot reset all own keys");
            ex.printStackTrace();
        }

        System.out.println("Testing.java: e.StartSync()");
        engine.startSync();

        // Keygen
        System.out.println("Generating keys: ");
        Identity user2 = new Identity();
        user2.user_id = "pEp_own_userId";
        user2.me = true;
        user2.username = "Test User 2";
        user2.address = "jniTestUser2@peptest.ch";
        user2 = engine.myself(user2);
        System.out.print("Keys generated: ");
        System.out.println(user2.fpr);

        // it's not necessary - you can just shutdown Sync and that's it
        // but for this test give sync a chance to process all messages
         try {
             Thread.sleep(2000);
         }
         catch (InterruptedException ex) { }

        System.out.println("STOP SYNC");
        engine.stopSync();

        System.exit(0);
    }
}

