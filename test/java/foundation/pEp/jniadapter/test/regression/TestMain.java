package foundation.pEp.jniadapter.test.regression;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.*;

class RegTestContext extends AdapterBaseTestContext {
    // enhance the context

    @Override
    public void init() throws Throwable {
        super.init();
        // init the enhancements
    }
}

class TestMain {
    public static void main(String[] args) {
        TestSuite.setVerbose(false);
        
        new TestUnit<RegTestContext>("Engine.myself", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.encrypt_message", new RegTestContext(), ctx -> {
            ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
        }).add();

        new TestUnit<RegTestContext>("Engine.encrypt_message_and_add_priv_key", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.encrypt_message_and_add_priv_key(ctx.msgToSelf, ctx.alice.fpr);
        }).add();

        new TestUnit<RegTestContext>("Engine.encrypt_message_for_self", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.encrypt_message_for_self(ctx.alice, ctx.msgToSelf, null);
        }).add();

        new TestUnit<RegTestContext>("Engine.decrypt_message", new RegTestContext(), ctx -> {
            ctx.engine.decrypt_message(ctx.msgToSelf, ctx.vStr, 0);
        }).add();

        //TODO: Coredump
//        new TestUnit<RegTestContext>("Engine.re_evaluate_message_rating", new RegTestContext(), ctx -> {
//            ctx.alice = ctx.engine.myself(ctx.alice);
//            ctx.bob = ctx.engine.myself(ctx.bob);
//            Message msg = ctx.engine.encrypt_message(ctx.msgToBob,null, Message.EncFormat.PEP);
//            ctx.engine.re_evaluate_message_rating(msg);
//        }).add();

        new TestUnit<RegTestContext>("Engine.outgoing_message_rating", new RegTestContext(), ctx -> {
            ctx.engine.outgoing_message_rating(ctx.msgToBob);
        }).add();

        new TestUnit<RegTestContext>("Engine.outgoing_message_rating_preview", new RegTestContext(), ctx -> {
            ctx.engine.outgoing_message_rating_preview(ctx.msgToBob);
        }).add();

        new TestUnit<RegTestContext>("Engine.get_identity", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.get_identity(ctx.alice.address, ctx.alice.user_id);
        }).add();

        new TestUnit<RegTestContext>("Engine.identity_rating", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.identity_rating(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.blacklist_retrieve", new RegTestContext(), ctx -> {
            ctx.engine.blacklist_retrieve();
        }).add();

        //FAIL
        new TestUnit<RegTestContext>("Engine.own_message_private_key_details", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
            ctx.engine.own_message_private_key_details(ctx.msgToBob);
        }).add();

        new TestUnit<RegTestContext>("Engine.OpenPGP_list_keyinfo", new RegTestContext(), ctx -> {
            ctx.engine.OpenPGP_list_keyinfo("");
        }).add();

        new TestUnit<RegTestContext>("Engine.set_identity_flags", new RegTestContext(), ctx -> {
            ctx.engine.set_identity_flags(ctx.alice, 0);
        }).add();

        new TestUnit<RegTestContext>("Engine.unset_identity_flags", new RegTestContext(), ctx -> {
            ctx.engine.unset_identity_flags(ctx.alice, 0);
        }).add();

        new TestUnit<RegTestContext>("Engine.own_identities_retrieve", new RegTestContext(), ctx -> {
            ctx.engine.own_identities_retrieve();
        }).add();

        new TestUnit<RegTestContext>("Engine.get_trustwords", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);
            ctx.engine.get_trustwords(ctx.alice, ctx.bob, "en", false);
        }).add();

        new TestUnit<RegTestContext>("Engine.get_trustwords_for_fprs", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.bob = ctx.engine.myself(ctx.bob);

            ctx.engine.get_trustwords_for_fprs(ctx.alice.fpr, ctx.bob.fpr, "en", false);
        }).add();

        new TestUnit<RegTestContext>("Engine.get_message_trustwords", new RegTestContext(), ctx -> {
            ctx.engine.get_message_trustwords(ctx.msgToBob, null, ctx.bob, "en", false);
        }).add();

        new TestUnit<RegTestContext>("Engine.get_languagelist", new RegTestContext(), ctx -> {
            ctx.engine.get_languagelist();
        }).add();

        new TestUnit<RegTestContext>("Engine.key_reset_trust", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_trust(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.key_reset_identity", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_identity(ctx.alice, "");
        }).add();

        new TestUnit<RegTestContext>("Engine.key_reset_user", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.key_reset_user("fsdjugsh", ctx.alice.fpr);
        }).add();

        new TestUnit<RegTestContext>("Engine.key_reset_all_own_keys", new RegTestContext(), ctx -> {
            ctx.engine.key_reset_all_own_keys();
        }).add();

        new TestUnit<RegTestContext>("Engine.deliverHandshakeResult", new RegTestContext(), ctx -> {
            ctx.engine.deliverHandshakeResult(SyncHandshakeResult.SyncHandshakeCancel, ctx.vID);
        }).add();


        //[17:51] <        heck> | this one fails since: 4665:f067c9e95455
        //[17:52] <        heck> | i confirmed it still works in the parent revision 4662:71147c43e31b
        //[17:52] <        heck> | the error i get is:
        //[17:53] <        heck> | *** send message KeySync Beacon service KeySync_fsm.c:234
        //[17:53] <        heck> | Assertion failed: (msg->from && msg->from->fpr), function attach_own_key, file message_api.c, line 1581.

//        new TestUnit<RegTestContext>("Engine.leave_device_group", new RegTestContext(), ctx -> {
//            ctx.engine.startSync();
//            ctx.engine.leave_device_group();
//        }).add();

        new TestUnit<RegTestContext>("Engine.enable_identity_for_sync", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.enable_identity_for_sync(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.disable_identity_for_sync", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.disable_identity_for_sync(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.config_cipher_suite", new RegTestContext(), ctx -> {
            ctx.engine.config_cipher_suite(CipherSuite.pEpCipherSuiteDefault);
        }).add();

        new TestUnit<RegTestContext>("Engine.trustwords", new RegTestContext(), ctx -> {
            ctx.engine.trustwords(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.updateIdentity", new RegTestContext(), ctx -> {
            ctx.engine.updateIdentity(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.setOwnKey", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
        }).add();

        new TestUnit<RegTestContext>("Engine.keyMistrusted", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.keyMistrusted(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.keyResetTrust", new RegTestContext(), ctx -> {
            ctx.engine.keyResetTrust(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.trustPersonalKey", new RegTestContext(), ctx -> {
            ctx.engine.trustPersonalKey(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.trustOwnKey", new RegTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
            ctx.engine.trustOwnKey(ctx.alice);
        }).add();

        new TestUnit<RegTestContext>("Engine.importKey", new RegTestContext(), ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        }).add();

        new TestUnit<RegTestContext>("Engine.blacklist_add", new RegTestContext(), ctx -> {
            ctx.engine.blacklist_add("43");
        }).add();

        new TestUnit<RegTestContext>("Engine.blacklist_delete", new RegTestContext(), ctx -> {
            ctx.engine.blacklist_delete("43");
        }).add();

        new TestUnit<RegTestContext>("Engine.blacklist_is_listed", new RegTestContext(), ctx -> {
            ctx.engine.blacklist_is_listed("43");
        }).add();

        new TestUnit<RegTestContext>("Engine.config_passive_mode", new RegTestContext(), ctx -> {
            ctx.engine.config_passive_mode(false);
        }).add();

        new TestUnit<RegTestContext>("Engine.config_unencrypted_subject", new RegTestContext(), ctx -> {
            ctx.engine.config_unencrypted_subject(false);
        }).add();

        new TestUnit<RegTestContext>("Engine.getCrashdumpLog", new RegTestContext(), ctx -> {
            ctx.engine.getCrashdumpLog(0);
        }).add();

        new TestUnit<RegTestContext>("Engine.getUserDirectory", new RegTestContext(), ctx -> {
            ctx.engine.getUserDirectory();
        }).add();

        new TestUnit<RegTestContext>("Engine.getMachineDirectory", new RegTestContext(), ctx -> {
            ctx.engine.getMachineDirectory();
        }).add();

        // AbstractEngine.java
        new TestUnit<RegTestContext>("Engine.close", new RegTestContext(), ctx -> {
            ctx.engine.close();
        }).add();

        new TestUnit<RegTestContext>("Engine.getVersion", new RegTestContext(), ctx -> {
            ctx.engine.getVersion();
        }).add();

        new TestUnit<RegTestContext>("Engine.getProtocolVersion", new RegTestContext(), ctx -> {
            ctx.engine.getProtocolVersion();
        }).add();

        new TestUnit<RegTestContext>("Engine.startKeyserverLookup", new RegTestContext(), ctx -> {
            ctx.engine.startKeyserverLookup();
        }).add();

        new TestUnit<RegTestContext>("Engine.startSync", new RegTestContext(), ctx -> {
            ctx.engine.startSync();
        }).add();

        new TestUnit<RegTestContext>("Engine.stopSync", new RegTestContext(), ctx -> {
            ctx.engine.stopSync();
        }).add();

        new TestUnit<RegTestContext>("Engine.isSyncRunning", new RegTestContext(), ctx -> {
            ctx.engine.isSyncRunning();
        }).add();

        TestSuite.run();
    }
}


