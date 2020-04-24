package foundation.pEp.jniadapter.test.regression;

import foundation.pEp.jniadapter.test.utils.TestUtils;
import foundation.pEp.jniadapter.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import java.util.function.Consumer;


/*
This test is just only checking for unsatisfiedLinkExceptions to make sure all the native calls are implemented
*/

class TestEnv {
    public Sync.DefaultCallback cb = new Sync.DefaultCallback();
    public Identity alice = new Identity();
    public Identity bob = new Identity();
    public Message msgToSelf;
    public Message msgToBob;
    public Vector<Identity> vID = new Vector<Identity>();
    public Vector<String> vStr = new Vector<String>();
    public byte[] key;
    private String fileName = "../resources/test_keys/pub/pep-test-alice-0x6FF00E97_pub.asc";
    public Engine engine = new Engine();

    public TestEnv() throws Exception {
        alice.user_id = "23";
        alice.address = "alice@peptest.org";
        alice.me = true;

        bob.user_id = "42";
        bob.address = "bob@peptest.org";

        msgToSelf = makeNewMessage(alice, alice, Message.Direction.Outgoing);
        msgToBob = makeNewMessage(alice, bob, Message.Direction.Outgoing);

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

    public static Message makeNewMessage(Identity from, Identity to, Message.Direction dir) {
        Message msg = new Message();
        Vector<Identity> vID = new Vector<Identity>();
        vID.add(to);

        msg.setFrom(from);
        msg.setTo(vID);
        msg.setDir(dir);
        msg.setLongmsg("Hi i am the message longmsg");
        return msg;
    }
}

class TestUnit {
    TestEnv env;
    String testUnitName = "default test unit";
    Consumer<TestEnv> lambda;

    public TestUnit(String name, Consumer<TestEnv> consumer) throws Exception {
        testUnitName = name;
        lambda = consumer;
        env = new TestEnv();

    }

    public void run() {
        TestUtils.logH1(testUnitName);
        try {
            lambda.accept(env);
        } catch (Throwable e) {
            TestUtils.logH1("TestUnit FAILED: " + e.toString());
            return;
        }
        TestUtils.logH2("SUCCESS!");
    }
}


class TestMain {

    public static void main(String[] args) throws Exception {
        testRunNew();
    }

    public static void testRunNew() throws Exception {
        new TestUnit("Engine.myself", env -> {
            env.alice = env.engine.myself(env.alice);
        }).run();

        new TestUnit("Engine.encrypt_message", env -> {
            env.engine.encrypt_message(env.msgToBob, null, Message.EncFormat.PEP);
        }).run();

        new TestUnit("Engine.encrypt_message_and_add_priv_key", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.encrypt_message_and_add_priv_key(env.msgToSelf, env.alice.fpr);
        }).run();

        new TestUnit("Engine.encrypt_message_for_self", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.encrypt_message_for_self(env.alice, env.msgToSelf, null);
        }).run();

        new TestUnit("Engine.decrypt_message", env -> {
            env.engine.decrypt_message(env.msgToSelf, env.vStr, 0);
        }).run();

        //TODO: Coredump
//        new TestUnit("Engine.re_evaluate_message_rating", env -> {
//            env.alice = env.engine.myself(env.alice);
//            env.bob = env.engine.myself(env.bob);
//            Message msg = env.engine.encrypt_message(env.msgToBob,null, Message.EncFormat.PEP);
//            env.engine.re_evaluate_message_rating(msg);
//        }).run();

        new TestUnit("Engine.outgoing_message_rating", env -> {
            env.engine.outgoing_message_rating(env.msgToBob);
        }).run();

        new TestUnit("Engine.outgoing_message_rating_preview", env -> {
            env.engine.outgoing_message_rating_preview(env.msgToBob);
        }).run();

        new TestUnit("Engine.get_identity", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.get_identity(env.alice.address, env.alice.user_id);
        }).run();

        new TestUnit("Engine.identity_rating", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.identity_rating(env.alice);
        }).run();

        new TestUnit("Engine.blacklist_retrieve", env -> {
            env.engine.blacklist_retrieve();
        }).run();

        //FAIL
        new TestUnit("Engine.own_message_private_key_details", env -> {
            env.alice = env.engine.myself(env.alice);
            env.bob = env.engine.myself(env.bob);

            env.engine.encrypt_message(env.msgToBob,null, Message.EncFormat.PEP);
            env.engine.own_message_private_key_details(env.msgToBob);
        }).run();

        new TestUnit("Engine.OpenPGP_list_keyinfo", env -> {
            env.engine.OpenPGP_list_keyinfo("");
        }).run();

        new TestUnit("Engine.set_identity_flags", env -> {
            env.engine.set_identity_flags(env.alice, 0);
        }).run();

        new TestUnit("Engine.unset_identity_flags", env -> {
            env.engine.unset_identity_flags(env.alice, 0);
        }).run();

        new TestUnit("Engine.own_identities_retrieve", env -> {
            env.engine.own_identities_retrieve();
        }).run();

        new TestUnit("Engine.get_trustwords", env -> {
            env.alice = env.engine.myself(env.alice);
            env.bob = env.engine.myself(env.bob);
            env.engine.get_trustwords(env.alice, env.bob, "en", false);
        }).run();

        new TestUnit("Engine.get_trustwords_for_fprs", env -> {
            env.alice = env.engine.myself(env.alice);
            env.bob = env.engine.myself(env.bob);

            env.engine.get_trustwords_for_fprs(env.alice.fpr, env.bob.fpr, "en", false);
        }).run();

        new TestUnit("Engine.get_message_trustwords", env -> {
            env.engine.get_message_trustwords(env.msgToBob, null, env.bob, "en", false);
        }).run();

        new TestUnit("Engine.get_languagelist", env -> {
            env.engine.get_languagelist();
        }).run();

        new TestUnit("Engine.key_reset_trust", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.key_reset_trust(env.alice);
        }).run();

        new TestUnit("Engine.key_reset_identity", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.key_reset_identity(env.alice, "");
        }).run();

        new TestUnit("Engine.key_reset_user", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.key_reset_user("fsdjugsh", env.alice.fpr);
        }).run();

        new TestUnit("Engine.key_reset_all_own_keys", env -> {
            env.engine.key_reset_all_own_keys();
        }).run();

        new TestUnit("Engine.deliverHandshakeResult", env -> {
            env.engine.deliverHandshakeResult(SyncHandshakeResult.SyncHandshakeCancel, env.vID);
        }).run();

        new TestUnit("Engine.leave_device_group", env -> {
            env.engine.startSync();
            env.engine.leave_device_group();
        }).run();

        new TestUnit("Engine.enable_identity_for_sync", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.enable_identity_for_sync(env.alice);
        }).run();

        new TestUnit("Engine.disable_identity_for_sync", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.disable_identity_for_sync(env.alice);
        }).run();

        // TODO: class not found: foundation/pEp/jniadapter/Message$CipherSuite
//        new TestUnit("Engine.config_cipher_suite", env -> {
//            env.engine.config_cipher_suite(CipherSuite.pEpCipherSuiteDefault);
//        }).run();

        new TestUnit("Engine.trustwords", env -> {
            env.engine.trustwords(env.alice);
        }).run();

        new TestUnit("Engine.updateIdentity", env -> {
            env.engine.updateIdentity(env.alice);
        }).run();

        new TestUnit("Engine.setOwnKey", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.setOwnKey(env.alice, env.alice.fpr);
        }).run();

        new TestUnit("Engine.keyMistrusted", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.keyMistrusted(env.alice);
        }).run();

        new TestUnit("Engine.keyResetTrust", env -> {
            env.engine.keyResetTrust(env.alice);
        }).run();

        new TestUnit("Engine.trustPersonalKey", env -> {
            env.engine.trustPersonalKey(env.alice);
        }).run();

        new TestUnit("Engine.trustOwnKey", env -> {
            env.alice = env.engine.myself(env.alice);
            env.engine.trustOwnKey(env.alice);
        }).run();

        new TestUnit("Engine.importKey", env -> {
            env.engine.importKey(env.key);
        }).run();

        new TestUnit("Engine.blacklist_add", env -> {
            env.engine.blacklist_add("43");
        }).run();

        new TestUnit("Engine.blacklist_delete", env -> {
            env.engine.blacklist_delete("43");
        }).run();

        new TestUnit("Engine.blacklist_is_listed", env -> {
            env.engine.blacklist_is_listed("43");
        }).run();

        new TestUnit("Engine.config_passive_mode", env -> {
            env.engine.config_passive_mode(false);
        }).run();

        new TestUnit("Engine.config_unencrypted_subject", env -> {
            env.engine.config_unencrypted_subject(false);
        }).run();

        new TestUnit("Engine.getCrashdumpLog", env -> {
            env.engine.getCrashdumpLog(0);
        }).run();

//        new TestUnit("Engine.getUserDirectory", env -> {
//            env.engine.getUserDirectory();
//        }).run();

//        new TestUnit("Engine.getMachineDirectory", env -> {
//            env.engine.getMachineDirectory();
//        }).run();

        // AbstractEngine.java
        new TestUnit("Engine.close", env -> {
            env.engine.close();
        }).run();

        new TestUnit("Engine.getVersion", env -> {
            env.engine.getVersion();
        }).run();

        new TestUnit("Engine.getProtocolVersion", env -> {
            env.engine.getProtocolVersion();
        }).run();

        new TestUnit("Engine.startKeyserverLookup", env -> {
            env.engine.startKeyserverLookup();
        }).run();

        new TestUnit("Engine.startSync", env -> {
            env.engine.startSync();
        }).run();

        new TestUnit("Engine.stopSync", env -> {
            env.engine.stopSync();
        }).run();

        new TestUnit("Engine.isSyncRunning", env -> {
            env.engine.isSyncRunning();
        }).run();

    }
}


