package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestLogger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


//public class ABAliceTestContext extends CTXBase {
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



public class CTXBase extends AbstractTestContext {
    // Basic
    public Engine engine;
    public TestCallbacks callbacks;

    // Identities
    public Identity alice;
    public Identity bob;
    public Identity carol;

    // Keys
    public byte[] keyAlicePub;
    private String filenameAlicePub = "../resources/test_keys/alice-pub-DE5DF92A358DCE5F.asc";

    public byte[] keyAliceSec;
    private String filenameAliceSec = "../resources/test_keys/alice-sec-DE5DF92A358DCE5F.asc";

    public byte[] keyAlicePubPassphrase;
    private String filenameAlicePubPassphrase = "../resources/test_keys/alice-passphrase-pub-BCBAC48800026D6F.asc";

    public byte[] keyAliceSecPassphrase;
    private String filenameAliceSecPassphrase = "../resources/test_keys/alice-passphrase-sec-BCBAC48800026D6F.asc";

    public byte[] keyBobPub;
    private String filenameBobPub = "../resources/test_keys/bob-pub.asc";

    public byte[] keyBobSec;
    private String filenameBobSec = "../resources/test_keys/bob-sec.asc";

    // Messages
    public Message msgAliceToAlice;
    public Message msgAliceToBob;

    // Message types
    public Message.Direction msgDirOutgoing = Message.Direction.Outgoing;
    public Blob attachmentTiny;
    public Blob attachment1KB;
    public AttachmentList attachmentList = new AttachmentList(3,10000);

    public CTXBase init() throws Throwable {
        callbacks = new TestCallbacks();
        engine = new Engine();
        engine.setMessageToSendCallback(callbacks);
        engine.setNotifyHandshakeCallback(callbacks);

        TestLogger.logH2("Machine directory: ");
        TestLogger.log(engine.getMachineDirectory());
        TestLogger.logH2("User directory:");
        TestLogger.log(engine.getUserDirectory());

        attachmentTiny = AdapterTestUtils.makeNewTestBlob("attachment1", "attachment1.txt", "text/plain");
        attachment1KB = AdapterTestUtils.makeNewTestBlob(1000, "att with size 1KB", null);

        alice = new Identity();
        alice.user_id = "23";
        alice.address = "alice@peptest.org";
        alice.me = true;

        bob = new Identity();
        bob.username = "pEp Test Bob";
        bob.user_id = "42";
        bob.address = "bob@peptest.org";

        msgAliceToAlice = AdapterTestUtils.makeNewTestMessage(alice, alice, Message.Direction.Outgoing);
        msgAliceToBob = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);

        Path path;
        path = Paths.get(filenameBobPub);
        try {
            keyBobPub = Files.readAllBytes(path);

            path = Paths.get(filenameBobSec);
            keyBobSec = Files.readAllBytes(path);

            path = Paths.get(filenameAlicePub);
            keyAlicePub = Files.readAllBytes(path);

            path = Paths.get(filenameAliceSec);
            keyAliceSec = Files.readAllBytes(path);

            path = Paths.get(filenameAlicePubPassphrase);
            keyAlicePubPassphrase = Files.readAllBytes(path);

            path = Paths.get(filenameAliceSecPassphrase);
            keyAliceSecPassphrase = Files.readAllBytes(path);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        return this;
    }

}