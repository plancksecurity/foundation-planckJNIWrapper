package com.pep.k9;

import android.content.res.AssetManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.pEp.jniadapter.Blob;
import org.pEp.jniadapter.Engine;
import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import org.pEp.jniadapter.Pair;
import org.pEp.jniadapter.pEpException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

public class UnitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);

        try {
            messageAfterDecryptionShouldBeTheSame();
            messageAfterDecryptionWithoutKeyShouldKeepBreaks();
            messageAfterDecryptionShouldKeepBreaks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void messageAfterDecryptionWithoutKeyShouldKeepBreaks() throws Exception {
        Engine engine;
        engine = new Engine();

        Identity alice = loadAliceFromEngine(engine);

        Identity bob = loadDummyBobFromEngine(engine);

        // message
        Message msg = new Message();
        msg.setFrom(alice);

        Vector<Identity> to = new Vector<>();
        to.add(bob);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("thisis\nastest");

        msg.setDir(Message.Direction.Outgoing);

        Vector<Identity> cc = new Vector<>();
        cc.add(alice);
        msg.setCc(cc);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        Vector<Blob> attachments = encryptedMessage.getAttachments();

        Engine.decrypt_message_Return result;
        result = decryptMessageOnEngine(engine, encryptedMessage);

        engine.close();

        if (!result.dst.getLongmsg().equals(msg.getLongmsg())) {
            throw new RuntimeException("FAILED: " +result.dst.getLongmsg()+" not equals to "+msg.getLongmsg());
        }
    }

    public void messageAfterDecryptionShouldKeepBreaks() throws Exception {
        Engine engine;
        engine = new Engine();

        Identity alice = loadAliceFromEngine(engine);

        Identity bob = loadBobFromEngine(engine);

        // message
        Message msg = new Message();
        msg.setFrom(alice);

        Vector<Identity> to = new Vector<>();
        to.add(bob);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("thisis\nastest");

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        Vector<Blob> attachments = encryptedMessage.getAttachments();

        Engine.decrypt_message_Return result;
        result = decryptMessageOnEngine(engine, encryptedMessage);

        engine.close();

        msg.setDir(Message.Direction.Outgoing);

        Vector<Identity> cc = new Vector<>();
        cc.add(alice);
        msg.setCc(cc);

        pairs.add(new Pair<>("Received", "in time"));
        if (!result.dst.getLongmsg().equals(msg.getLongmsg())) {
            throw new RuntimeException("FAILED: " +result.dst.getLongmsg()+" not equals to "+msg.getLongmsg());
        }
    }

    public void messageAfterDecryptionShouldBeTheSame() throws Exception {
        Engine engine;
        engine = new Engine();

        Identity alice = loadAliceFromEngine(engine);

        Identity bob = loadBobFromEngine(engine);

        // message
        Message msg = setupMessage(alice, bob);

        ArrayList<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>("Received", "in time"));
        pairs.add(new Pair<>("X-Foobaz", "of course"));
        msg.setOptFields(pairs);

        Message encryptedMessage;
        encryptedMessage = encryptMessageOnEngine(engine, msg);

        Vector<Blob> attachments = encryptedMessage.getAttachments();

        Engine.decrypt_message_Return result;
        result = decryptMessageOnEngine(engine, encryptedMessage);

        engine.close();

        if (!result.dst.getLongmsg().equals(msg.getLongmsg())) {
            throw new RuntimeException("FAILED: " +result.dst.getLongmsg()+" not equals to "+msg.getLongmsg());
        }
    }

    private Message setupMessage(Identity alice, Identity bob) {
        Message msg = new Message();
        msg.setFrom(alice);

        Vector<Identity> to = new Vector<>();
        to.add(bob);
        msg.setTo(to);

        msg.setShortmsg("hello, world");
        msg.setLongmsg("this is a test");

        msg.setDir(Message.Direction.Outgoing);

        Vector<Identity> cc = new Vector<>();
        cc.add(alice);
        msg.setCc(cc);

        return msg;
    }

    private Engine.decrypt_message_Return decryptMessageOnEngine(Engine engine, Message encriptedMessage) throws pEpException {
        long lastTime = System.currentTimeMillis();
        Engine.decrypt_message_Return decrypt_message_return = engine.decrypt_message(encriptedMessage);
        long time = System.currentTimeMillis() - lastTime;
        Log.d("time", " " + time);
        return decrypt_message_return;
    }

    private Message encryptMessageOnEngine(Engine engine, Message msg) throws pEpException {
        long lastTime = System.currentTimeMillis();
        Message message = engine.encrypt_message(msg, null);
        long time = System.currentTimeMillis() - lastTime;
        Log.d("time", " " + time);
        return message;
    }

    private Identity loadBobFromEngine(Engine engine) throws IOException {
        //
        // Other peers :
        // pEp Test Bob (test key, don't use) <pep.test.bob@pep-project.org> 
        //         C9C2EE39
        // 59BFF488C9C2EE39
        importKeyFromEngine(engine, "0xC9C2EE39.asc");

        Identity bob = new Identity();
        bob.username = "bob Test";
        bob.address = "pep.test.bob@pep-project.org";
        bob.user_id = "112";
        bob.fpr = "BFCDB7F301DEEEBBF947F29659BFF488C9C2EE39";

        updateIdentityOnEngine(engine, bob);
        return bob;
    }

    private Identity loadDummyBobFromEngine(Engine engine) throws IOException {
        //
        // Other peers :
        // pEp Test Bob (test key, don't use) <pep.test.bob@pep-project.org> 
        //         C9C2EE39
        // 59BFF488C9C2EE39

        Identity bob = new Identity();
        bob.username = "bob Test";
        bob.address = "pep.test.bob@pep-project.org";
        bob.user_id = "112";

        updateIdentityOnEngine(engine, bob);
        return bob;
    }

    private void updateIdentityOnEngine(Engine engine, Identity identity) {
        long lastTime = System.currentTimeMillis();
        engine.updateIdentity(identity);
        long time = System.currentTimeMillis() - lastTime;
        Log.d("time", " " + time);
    }

    @NonNull
    private Identity loadAliceFromEngine(Engine engine) throws IOException {
        // Our test user :
        // pEp Test Alice (test key don't use) <pep.test.alice@pep-project.org>
        //         6FF00E97
        // A9411D176FF00E97
        importKeyFromEngine(engine, "6FF00E97_sec.asc");

        Identity alice = new Identity();
        alice.username = "Alice Test";
        alice.address = "pep.test.alice@pep-project.org";
        alice.user_id = "pEp_own_userId";
        alice.fpr = "4ABE3AAF59AC32CFE4F86500A9411D176FF00E97";

        long lastTime = System.currentTimeMillis();
        myselfInEngine(engine, alice);
        long time = System.currentTimeMillis() - lastTime;
        Log.d("time", " " + time);

        return alice;
    }

    private void importKeyFromEngine(Engine engine, String filename) throws IOException {
        long lastTime = System.currentTimeMillis();
        engine.importKey(LoadAssetAsString(filename));
        long time = System.currentTimeMillis() - lastTime;
        Log.d("time", " " + time);
    }

    private String LoadAssetAsString(String fname) throws IOException {
        // byte buffer into a string
        return new String(LoadAssetAsBuffer(fname));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private byte[] LoadAssetAsBuffer(String fname) throws IOException {
        AssetManager assetManager = getAssets();
        InputStream input;

        input = assetManager.open(fname);

        int size = input.available();
        byte[] buffer = new byte[size];
        input.read(buffer);
        input.close();

        // byte buffer
        return buffer;

    }

    private Identity myselfInEngine(Engine engine, Identity identity) {
        return engine.myself(identity);
    }
}
