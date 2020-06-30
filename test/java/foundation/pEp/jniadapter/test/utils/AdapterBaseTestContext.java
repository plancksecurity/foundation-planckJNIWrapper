package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.pitytest.*;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


//public class ABAliceTestContext extends AdapterBaseTestContext {
//    FsMQManager transport;
//
//    @Override
//    public void init() throws Throwable {
//        super.init();
//        transport = new FsMQManager(alice.address, "../resources/per-user-dir/alice/inbox");
//        transport.clearOwnQueue();
//        transport.addPeer(bob.address, "../resources/per-user-dir/bob/inbox");
//
//        transport.broadcastSigOnline();
//        transport.waitForPeerOnline(bob.address);
//    }
//
//}


public class AdapterBaseTestContext extends AbstractTestContext {
    // Basic
    public Engine engine;
    public TestCallbacks callbacks;

    // Identities
    public Identity alice;
    public Identity bob;
    public Identity carol;

    // Keys
    public byte[] keyBobSec;
    private String filenameBobSec = "../resources/test_keys/bob-sec.asc";

    public byte[] keyBobPub;
    private String filenameBobPub = "../resources/test_keys/bob-pub.asc";

    public byte[] keyAlicePub;
    private String filenameAlicePub = "../resources/test_keys/alicenew-pub.asc";

    public byte[] keyAliceSec;
    private String filenameAliceSec = "../resources/test_keys/alicenew-sec.asc";

    // Messages
    public Message msgToSelf;
    public Message msgToBob;

    // Misc
    public Vector<Identity> vID;
    public Vector<String> vStr;

    public void init() throws Throwable {
        vID = new Vector<Identity>();
        vStr = new Vector<String>();

        callbacks = new TestCallbacks();
        engine = new Engine();
        engine.setMessageToSendCallback(callbacks);
        engine.setNotifyHandshakeCallback(callbacks);

        alice = new Identity();
        alice.user_id = "23";
        alice.address = "alice@peptest.org";
        alice.me = true;

        bob = new Identity();
        bob.username = "pEp Test Bob";
        bob.user_id = "42";
        bob.address = "bob@peptest.org";

        msgToSelf = AdapterTestUtils.makeNewTestMessage(alice, alice, Message.Direction.Outgoing);
        msgToBob = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);

        vID.add(bob);
        vStr.add("StringItem");

        Path path;
        path = Paths.get(filenameBobPub);
        keyBobPub = Files.readAllBytes(path);

        path = Paths.get(filenameBobSec);
        keyBobSec = Files.readAllBytes(path);

        path = Paths.get(filenameAlicePub);
        keyAlicePub = Files.readAllBytes(path);

        path = Paths.get(filenameAliceSec);
        keyAliceSec = Files.readAllBytes(path);
    }

}