package foundation.pEp.jniadapter.test.templateAliceBob;


import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.TestCallbacks;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx.FsMQManagerTestContext;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;


class MultiPeerCTX extends FsMQManagerTestContext {
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
    private String filenameAlicePub = "../resources/test_keys/alice-pub.asc";

    public byte[] keyAliceSec;
    private String filenameAliceSec = "../resources/test_keys/alice-sec.asc";

    // Messages
    public Message msgToSelf;
    public Message msgToBob;

    // Misc
    public Vector<Identity> vID;
    public Vector<String> vStr;

    public MultiPeerCTX(String selfAddress) {
        super(selfAddress);
    }

    public MultiPeerCTX init() throws Throwable {
        super.init();
        vID = new Vector<Identity>();
        vStr = new Vector<String>();

        callbacks = new TestCallbacks();
        engine = new Engine();
        engine.setMessageToSendCallback(callbacks);
        engine.setNotifyHandshakeCallback(callbacks);

        alice = new Identity();
        alice.address = "alice@peptest.org";
        alice.username = "alice";
        alice.user_id = "23";

        bob = new Identity();
        bob.address = "bob@peptest.org";
        bob.username = "bob";
        bob.user_id = "42";

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

        log("Clearing transport queue...");
        qm.clearOwnQueue();

        log("pEp PER_MACHINE_DIR: " + engine.getMachineDirectory());
        log("pEp PER_USER_DIR   : " + engine.getUserDirectory());
        log("pEp ProtocolVersion: " + engine.getProtocolVersion());
        log("pEp Version        : " + engine.getVersion());
        return this;
    }
}
