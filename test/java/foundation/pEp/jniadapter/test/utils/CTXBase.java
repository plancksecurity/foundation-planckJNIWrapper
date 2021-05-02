package foundation.pEp.jniadapter.test.utils;

import foundation.pEp.jniadapter.Blob;
import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.model.*;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestLogger;


public class CTXBase extends AbstractTestContext {
    // Basic
    public Engine engine;
    public TestCallbacks callbacks;

    public TestModel model = new TestModel();

    // Identities
    public Identity alice;
    public Identity bob;
    public Identity carol;

    // Keys
    public byte[] keyAlicePub;
    public byte[] keyAliceSec;
    public byte[] keyAlicePubPassphrase;
    public byte[] keyAliceSecPassphrase;
    public byte[] keyBobPub;
    public byte[] keyBobSec;

    // Messages
    public Message msgAliceToAlice;
    public Message msgAliceToBob;

    // Message types
    public Message.Direction msgDirOutgoing = Message.Direction.Outgoing;
    public Blob attachmentTiny;
    public Blob attachment1KB;
    public AttachmentList attachmentList = new AttachmentList(3, 10000);

    public CTXBase init() throws Throwable {
        callbacks = new TestCallbacks();
        engine = new Engine();
        engine.setMessageToSendCallback(callbacks);
        engine.setNotifyHandshakeCallback(callbacks);

        setupModel();
        alice = model.get(Role.ALICE).pEpIdent;
        bob = model.get(Role.BOB).pEpIdent;
        carol = model.get(Role.CAROL).pEpIdent;

        keyAlicePub = model.get(Role.ALICE).getDefaultKey(false).getKeyPub();
        keyAliceSec = model.get(Role.ALICE).getDefaultKey(false).getKeySec();
        keyAlicePubPassphrase = model.get(Role.ALICE).getDefaultKey(true).getKeyPub();
        keyAliceSecPassphrase = model.get(Role.ALICE).getDefaultKey(true).getKeySec();
        keyBobPub = model.get(Role.BOB).getDefaultKey(false).getKeyPub();
        keyBobSec = model.get(Role.BOB).getDefaultKey(false).getKeySec();

        TestLogger.logH2("Machine directory: ");
        TestLogger.log(engine.getMachineDirectory());
        TestLogger.logH2("User directory:");
        TestLogger.log(engine.getUserDirectory());

        attachmentTiny = AdapterTestUtils.makeNewTestBlob("attachment1", "attachment1.txt", "text/plain");
        attachment1KB = AdapterTestUtils.makeNewTestBlob(1000, "att with size 1KB", null);
        msgAliceToAlice = AdapterTestUtils.makeNewTestMessage(alice, alice, Message.Direction.Outgoing);
        msgAliceToBob = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);

        return this;
    }

    private void setupModel() {
        // Idents
        {
            model.add(new TestIdentity(Role.ALICE));
            model.add(new TestIdentity(Role.BOB));
            model.add(new TestIdentity(Role.CAROL));
        }
        // Nodes
        {
            TestNode tn = new TestNode(Node.NODE_A1);
            tn.addOwnIdent(model.get(Role.ALICE));
            model.add(tn);
        }
        {
            TestNode tn = new TestNode(Node.NODE_B1);
            tn.addOwnIdent(model.get(Role.BOB));
            model.add(tn);
        }
        {
            TestNode tn = new TestNode(Node.NODE_C1);
            tn.addOwnIdent(model.get(Role.CAROL));
            model.add(tn);
        }
        {
            TestNode tn = new TestNode(Node.NODE_A2);
            tn.addOwnIdent(model.get(Role.ALICE));
            model.add(tn);
        }
        {
            TestNode tn = new TestNode(Node.NODE_B2);
            tn.addOwnIdent(model.get(Role.BOB));
            model.add(tn);
        }
        {
            TestNode tn = new TestNode(Node.NODE_C2);
            tn.addOwnIdent(model.get(Role.CAROL));
            model.add(tn);
        }

        // Keys
        {
            String pathPub = "../resources/test_keys/alice-pub-DE5DF92A358DCE5F.asc";
            String pathSec = "../resources/test_keys/alice-sec-DE5DF92A358DCE5F.asc";
            String pathPubPP = "../resources/test_keys/alice-passphrase-pub-BCBAC48800026D6F.asc";
            String pathSecPP = "../resources/test_keys/alice-passphrase-sec-BCBAC48800026D6F.asc";
            new TestKeyPair(model.get(Role.ALICE), pathPub, pathSec, true);
            new TestKeyPair(model.get(Role.ALICE), pathPubPP, pathSecPP, "passphrase_alice", true);
        }
        {
            String pathPub = "../resources/test_keys/bob-pub.asc";
            String pathSec = "../resources/test_keys/bob-sec.asc";
            new TestKeyPair(model.get(Role.BOB), pathPub, pathSec, true);
        }

    }
}