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

    public TestModel<pEpTestIdentity,TestNode<pEpTestIdentity>> model = new TestModel(pEpTestIdentity::new,TestNode::new);

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
        alice = model.getIdent(Role.ALICE).pEpIdent;
        bob = model.getIdent(Role.BOB).pEpIdent;
        carol = model.getIdent(Role.CAROL).pEpIdent;

        keyAlicePub = model.getIdent(Role.ALICE).getDefaultKey(false).getKeyPub();
        keyAliceSec = model.getIdent(Role.ALICE).getDefaultKey(false).getKeySec();
        keyAlicePubPassphrase = model.getIdent(Role.ALICE).getDefaultKey(true).getKeyPub();
        keyAliceSecPassphrase = model.getIdent(Role.ALICE).getDefaultKey(true).getKeySec();
        keyBobPub = model.getIdent(Role.BOB).getDefaultKey(false).getKeyPub();
        keyBobSec = model.getIdent(Role.BOB).getDefaultKey(false).getKeySec();

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
        // Idents to Nodes (1-n)
        model.getNode(NodeName.NODE_A1).setDefaultRole(Role.ALICE);
        model.getNode(NodeName.NODE_B1).setDefaultRole(Role.BOB);
        model.getNode(NodeName.NODE_C1).setDefaultRole(Role.CAROL);
        model.getNode(NodeName.NODE_A2).setDefaultRole(Role.ALICE);
        model.getNode(NodeName.NODE_B2).setDefaultRole(Role.BOB);
        model.getNode(NodeName.NODE_C2).setDefaultRole(Role.CAROL);

        // Default Partner
        model.getIdent(Role.ALICE).setDefaultPartner(Role.BOB);
        model.getIdent(Role.BOB).setDefaultPartner(Role.CAROL);
        model.getIdent(Role.CAROL).setDefaultPartner(Role.ALICE);

        // Keys
        {
            String pathPub = "../resources/test_keys/alice-pub-DE5DF92A358DCE5F.asc";
            String pathSec = "../resources/test_keys/alice-sec-DE5DF92A358DCE5F.asc";
            String pathPubPP = "../resources/test_keys/alice-passphrase-pub-BCBAC48800026D6F.asc";
            String pathSecPP = "../resources/test_keys/alice-passphrase-sec-BCBAC48800026D6F.asc";
            new pEpTestKeyPair(model.getIdent(Role.ALICE), pathPub, pathSec, true);
            new pEpTestKeyPair(model.getIdent(Role.ALICE), pathPubPP, pathSecPP, "passphrase_alice", true);
        }
        {
            String pathPub = "../resources/test_keys/bob-pub.asc";
            String pathSec = "../resources/test_keys/bob-sec.asc";
            new pEpTestKeyPair(model.getIdent(Role.BOB), pathPub, pathSec, true);
        }

    }
}