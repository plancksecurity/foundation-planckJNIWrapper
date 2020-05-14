package foundation.pEp.jniadapter.test.framework;
import foundation.pEp.jniadapter.test.utils.TestUtils;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

public class TestContext {
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

    public TestContext() { }

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

        msgToSelf = TestUtils.makeNewTestMessage(alice, alice, Message.Direction.Outgoing);
        msgToBob = TestUtils.makeNewTestMessage(alice, bob, Message.Direction.Outgoing);

        vID.add(bob);
        vStr.add("StringItem");

        try {
            Path path = Paths.get(fileName);
            key = Files.readAllBytes(path);
        } catch (Exception e) {
            TestUtils.log("Could not open key file:" + fileName);
            throw e;
        }
    }
}