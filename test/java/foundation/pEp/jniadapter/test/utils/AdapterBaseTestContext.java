package foundation.pEp.jniadapter.test.utils;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class AdapterBaseTestContext implements AbstractTestContext {
    public Sync.DefaultCallback cb = new Sync.DefaultCallback();
    public Identity alice;
    public Identity bob;
    public Message msgToSelf;
    public Message msgToBob;
    public Vector<Identity> vID;
    public Vector<String> vStr;
    public byte[] key;
    private String fileName = "../resources/test_keys/pub/pep-test-alice-0x6FF00E97_pub.asc";
    public Engine engine;

    public AdapterBaseTestContext() { }

    public void init() throws Exception {
        vID = new Vector<Identity>();
        vStr = new Vector<String>();

        engine = new Engine();

        alice = new Identity();
        alice.user_id = "23";
        alice.address = "alice@peptest.org";
        alice.me = true;

        bob = new Identity();
        bob.user_id = "42";
        bob.address = "bob@peptest.org";

        msgToSelf = AdapterTestUtils.makeNewTestMessage(alice, alice, Message.Direction.Outgoing);
        msgToBob = AdapterTestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);

        vID.add(bob);
        vStr.add("StringItem");

        try {
            Path path = Paths.get(fileName);
            key = Files.readAllBytes(path);
        } catch (Exception e) {
            TestLogger.log("Could not open key file:" + fileName);
            throw e;
        }
    }
}


/*package foundation.pEp.jniadapter.test.framework;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class TestContext {
    public TestCallbacks cb = new TestCallbacks();
    public Identity alice;
    public Identity bob;
    public Message msgToSelf;
    public Message msgToBob;
    public Vector<Identity> vID;
    public Vector<String> vStr;
    public byte[] key;
    private String fileName = "../resources/test_keys/pub/pep-test-alice-0x6FF00E97_pub.asc";
    public Engine engine;

    public TestContext() {
    }

    public void init() throws Exception {
        this.vID = new Vector();
        this.vStr = new Vector();
        this.engine = new Engine();
        this.alice = new Identity();
        this.alice.user_id = "23";
        this.alice.address = "alice@peptest.org";
        this.alice.me = true;
        this.bob = new Identity();
        this.bob.user_id = "42";
        this.bob.address = "bob@peptest.org";
        this.msgToSelf = AdapterTestUtils.makeNewTestMessage(this.alice, this.alice, Message.Direction.Outgoing);
        this.msgToBob = AdapterTestUtils.makeNewTestMessage(this.alice, this.bob, Message.Direction.Outgoing);
        this.vID.add(this.bob);
        this.vStr.add("StringItem");

        try {
            Path var1 = Paths.get(this.fileName);
            this.key = Files.readAllBytes(var1);
        } catch (Exception var2) {
            TestLogger.log("Could not open key file:" + this.fileName);
            throw var2;
        }
    }
}
*/