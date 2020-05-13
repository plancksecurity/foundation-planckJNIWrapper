package foundation.pEp.jniadapter.test.regression;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.*;

class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit("Engine.myself", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
        }).run();

        new TestUnit("Engine.encrypt_message", new TestContext(), ctx -> {
            ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
        }).run();

        new TestUnit("Engine.encrypt_message_and_add_priv_key", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.encrypt_message_and_add_priv_key(ctx.msgToSelf, ctx.alice.fpr);
        }).run();

        new TestUnit("Engine.encrypt_message_for_self", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.encrypt_message_for_self(ctx.alice, ctx.msgToSelf, null);
        }).run();

        new TestUnit("Engine.decrypt_message", new TestContext(), ctx -> {
            ctx.engine.decrypt_message(ctx.msgToSelf, ctx.vStr, 0);
        }).run();

        //TODO: Coredump
//        new TestUnit("Engine.re_evaluate_message_rating", new TestContext(), ctx -> {
//            ctx.alice = ctx.engine.myself(ctx.alice);
//            ctx.bob = ctx.engine.myself(ctx.bob);
//            Message msg = ctx.engine.encrypt_message(ctx.msgToBob,null, Message.EncFormat.PEP);
//            ctx.engine.re_evaluate_message_rating(msg);
//        }).run();

        new TestUnit("Engine.outgoing_message_rating", new TestContext(), ctx -> {
            ctx.engine.outgoing_message_rating(ctx.msgToBob);
        }).run();

        new TestUnit("Engine.outgoing_message_rating_preview", new TestContext(), ctx -> {
            ctx.engine.outgoing_message_rating_preview(ctx.msgToBob);
        }).run();

        new TestUnit("Engine.get_identity", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.get_identity(ctx.alice.address, ctx.alice.user_id);
        }).run();

        new TestUnit("Engine.identity_rating", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.identity_rating(ctx.alice);
        }).run();

        new TestUnit("Engine.blacklist_retrieve", new TestContext(), ctx -> {
            ctx.engine.blacklist_retrieve();
        }).run();

        //FAIL
        new TestUnit("Engine.own_message_private_key_details", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
            ctx.engine.own_message_private_key_details(ctx.msgToBob);
        }).run();

        new TestUnit("Engine.OpenPGP_list_keyinfo", new TestContext(), ctx -> {
            ctx.engine.OpenPGP_list_keyinfo("");
        }).run();

        new TestUnit("Engine.set_identity_flags", new TestContext(), ctx -> {
            ctx.engine.set_identity_flags(ctx.alice, 0);
        }).run();

        new TestUnit("Engine.unset_identity_flags", new TestContext(), ctx -> {
            ctx.engine.unset_identity_flags(ctx.alice, 0);
        }).run();

        new TestUnit("Engine.own_identities_retrieve", new TestContext(), ctx -> {
            ctx.engine.own_identities_retrieve();
        }).run();

        new TestUnit("Engine.get_trustwords", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);
            ctx.engine.get_trustwords(ctx.alice, ctx.bob, "en", false);
        }).run();

        new TestUnit("Engine.get_trustwords_for_fprs", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            ctx.engine.get_trustwords_for_fprs(ctx.alice.fpr, ctx.bob.fpr, "en", false);
        }).run();

        new TestUnit("Engine.get_message_trustwords", new TestContext(), ctx -> {
            ctx.engine.get_message_trustwords(ctx.msgToBob, null, ctx.bob, "en", false);
        }).run();

        new TestUnit("Engine.get_languagelist", new TestContext(), ctx -> {
            ctx.engine.get_languagelist();
        }).run();

        new TestUnit("Engine.key_reset_trust", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_trust(ctx.alice);
        }).run();

        new TestUnit("Engine.key_reset_identity", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_identity(ctx.alice, "");
        }).run();

        new TestUnit("Engine.key_reset_user", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_user("fsdjugsh", ctx.alice.fpr);
        }).run();

        new TestUnit("Engine.key_reset_all_own_keys", new TestContext(), ctx -> {
            ctx.engine.key_reset_all_own_keys();
        }).run();

        new TestUnit("Engine.deliverHandshakeResult", new TestContext(), ctx -> {
            ctx.engine.deliverHandshakeResult(SyncHandshakeResult.SyncHandshakeCancel, ctx.vID);
        }).run();


        //[17:51] <        heck> | this one fails since: 4665:f067c9e95455
        //[17:52] <        heck> | i confirmed it still works in the parent revision 4662:71147c43e31b
        //[17:52] <        heck> | the error i get is:
        //[17:53] <        heck> | *** send message KeySync Beacon service KeySync_fsm.c:234
        //[17:53] <        heck> | Assertion failed: (msg->from && msg->from->fpr), function attach_own_key, file message_api.c, line 1581.

//        new TestUnit("Engine.leave_device_group", new TestContext(), ctx -> {
//            ctx.engine.startSync();
//            ctx.engine.leave_device_group();
//        }).run();

        new TestUnit("Engine.enable_identity_for_sync", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.enable_identity_for_sync(ctx.alice);
        }).run();

        new TestUnit("Engine.disable_identity_for_sync", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.disable_identity_for_sync(ctx.alice);
        }).run();

        // TODO: class not found: foundation/pEp/jniadapter/Message$CipherSuite
//        new TestUnit("Engine.config_cipher_suite", new TestContext(), ctx -> {
//            ctx.engine.config_cipher_suite(CipherSuite.pEpCipherSuiteDefault);
//        }).run();

        new TestUnit("Engine.trustwords", new TestContext(), ctx -> {
            ctx.engine.trustwords(ctx.alice);
        }).run();

        new TestUnit("Engine.updateIdentity", new TestContext(), ctx -> {
            ctx.engine.updateIdentity(ctx.alice);
        }).run();

        new TestUnit("Engine.setOwnKey", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
        }).run();

        new TestUnit("Engine.keyMistrusted", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.keyMistrusted(ctx.alice);
        }).run();

        new TestUnit("Engine.keyResetTrust", new TestContext(), ctx -> {
            ctx.engine.keyResetTrust(ctx.alice);
        }).run();

        new TestUnit("Engine.trustPersonalKey", new TestContext(), ctx -> {
            ctx.engine.trustPersonalKey(ctx.alice);
        }).run();

        new TestUnit("Engine.trustOwnKey", new TestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.trustOwnKey(ctx.alice);
        }).run();

        new TestUnit("Engine.importKey", new TestContext(), ctx -> {
            ctx.engine.importKey(ctx.key);
        }).run();

        new TestUnit("Engine.blacklist_add", new TestContext(), ctx -> {
            ctx.engine.blacklist_add("43");
        }).run();

        new TestUnit("Engine.blacklist_delete", new TestContext(), ctx -> {
            ctx.engine.blacklist_delete("43");
        }).run();

        new TestUnit("Engine.blacklist_is_listed", new TestContext(), ctx -> {
            ctx.engine.blacklist_is_listed("43");
        }).run();

        new TestUnit("Engine.config_passive_mode", new TestContext(), ctx -> {
            ctx.engine.config_passive_mode(false);
        }).run();

        new TestUnit("Engine.config_unencrypted_subject", new TestContext(), ctx -> {
            ctx.engine.config_unencrypted_subject(false);
        }).run();

        new TestUnit("Engine.getCrashdumpLog", new TestContext(), ctx -> {
            ctx.engine.getCrashdumpLog(0);
        }).run();

        new TestUnit("Engine.getUserDirectory", new TestContext(), ctx -> {
            ctx.engine.getUserDirectory();
        }).run();

        new TestUnit("Engine.getMachineDirectory", new TestContext(), ctx -> {
            ctx.engine.getMachineDirectory();
        }).run();

        // AbstractEngine.java
        new TestUnit("Engine.close", new TestContext(), ctx -> {
            ctx.engine.close();
        }).run();

        new TestUnit("Engine.getVersion", new TestContext(), ctx -> {
            ctx.engine.getVersion();
        }).run();

        new TestUnit("Engine.getProtocolVersion", new TestContext(), ctx -> {
            ctx.engine.getProtocolVersion();
        }).run();

        new TestUnit("Engine.startKeyserverLookup", new TestContext(), ctx -> {
            ctx.engine.startKeyserverLookup();
        }).run();

        new TestUnit("Engine.startSync", new TestContext(), ctx -> {
            ctx.engine.startSync();
        }).run();

        new TestUnit("Engine.stopSync", new TestContext(), ctx -> {
            ctx.engine.stopSync();
        }).run();

        new TestUnit("Engine.isSyncRunning", new TestContext(), ctx -> {
            ctx.engine.isSyncRunning();
        }).run();
    }
}


