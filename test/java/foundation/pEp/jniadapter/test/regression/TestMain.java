package foundation.pEp.jniadapter.test.regression;
import foundation.pEp.pitytest.*;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.*;

import java.util.ArrayList;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

class CTXAlice extends AdapterBaseTestContext {
    @Override
    public CTXAlice init() throws Throwable {
        super.init();
        alice = engine.myself(alice);
        return this;
    }
}

class CTXAlice2 extends CTXAlice {
    @Override
    public CTXAlice2 init() throws Throwable {
        super.init();
        engine.importKey(keyBobSec);
        bob = engine.updateIdentity(bob);
        return this;
    }
}

// Almost exact copy of JNI1118Context, we need a shared context hierarchy
class CTXReEvaluateMessageRating extends AdapterBaseTestContext {
    public Message msgToBobEncrypted;
    public Message msgToBobDecrypted;
    public decrypt_message_Return msgToBobDecryptResult;

    @Override
    public CTXReEvaluateMessageRating init() throws Throwable {
        super.init();
        alice = engine.myself(alice);
        engine.importKey(keyBobPub);

        Vector<Identity> msgToBobRcpts = new Vector<>();
        msgToBobRcpts.add(bob);
        msgToBob.setTo(msgToBobRcpts);

        msgToBobEncrypted = engine.encrypt_message(msgToBob, null, Message.EncFormat.PEP);
        msgToBobDecrypted = msgToBobEncrypted;
        msgToBobDecryptResult = engine.decrypt_message(msgToBobDecrypted, new Vector<String>(), 0);
        if (msgToBobEncrypted == null) {
            throw new RuntimeException("Context failure, error decrypting message");
        }

        addRatingToOptFields(msgToBobDecrypted, msgToBobDecryptResult.rating.getInternalStringValue());
        addRcptsToOptFields(msgToBobDecrypted, Identity.toXKeyList(msgToBobDecrypted.getTo()));
        return this;
    }

    public void addRatingToOptFields(Message msg, String ratingStr) {
        ArrayList<Pair<String, String>> opts = msg.getOptFields();
        opts.add(new Pair<String, String>("X-EncStatus",ratingStr));
        msg.setOptFields(opts);
    }

    public void addRcptsToOptFields(Message msg, String fprs) {
        ArrayList<Pair<String, String>> opts = msg.getOptFields();
        opts.add(new Pair<String, String>("X-KeyList", fprs));
        msg.setOptFields(opts);
    }

}

class TestMain {
    public static void main(String[] args) {
        TestSuite.getDefault().setVerbose(false);
        
        new TestUnit<AdapterBaseTestContext>("Engine.myself", new AdapterBaseTestContext(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.encrypt_message", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.encrypt_message(ctx.msgToBob, null, Message.EncFormat.PEP);
        });

        new TestUnit<CTXAlice>("Engine.encrypt_message_and_add_priv_key", new CTXAlice(), ctx -> {
            ctx.engine.encrypt_message_and_add_priv_key(ctx.msgToSelf, ctx.alice.fpr);
        });

        new TestUnit<CTXAlice>("Engine.encrypt_message_for_self", new CTXAlice(), ctx -> {
            ctx.engine.encrypt_message_for_self(ctx.alice, ctx.msgToSelf, null);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.decrypt_message", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.decrypt_message(ctx.msgToSelf, new Vector<String>(), 0);
        });

        new TestUnit<CTXReEvaluateMessageRating>("Engine.re_evaluate_message_rating", new CTXReEvaluateMessageRating(), ctx -> {
            ctx.engine.re_evaluate_message_rating(ctx.msgToBobDecrypted);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.outgoing_message_rating", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.outgoing_message_rating(ctx.msgToBob);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.outgoing_message_rating_preview", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.outgoing_message_rating_preview(ctx.msgToBob);
        });

        new TestUnit<CTXAlice>("Engine.get_identity", new CTXAlice(), ctx -> {
            ctx.engine.get_identity(ctx.alice.address, ctx.alice.user_id);
        });

        new TestUnit<CTXAlice>("Engine.identity_rating", new CTXAlice(), ctx -> {
            ctx.engine.identity_rating(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.blacklist_retrieve", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.blacklist_retrieve();
        });

//        TODO: FAILS
//        new TestUnit<CTXAlice2>("Engine.own_message_private_key_details", new CTXAlice2(), ctx -> {
//            ctx.engine.own_message_private_key_details(ctx.msgToSelf);
//        });

        new TestUnit<AdapterBaseTestContext>("Engine.OpenPGP_list_keyinfo", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.OpenPGP_list_keyinfo("");
        });

        new TestUnit<AdapterBaseTestContext>("Engine.set_identity_flags", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.set_identity_flags(ctx.alice, 0);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.unset_identity_flags", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.unset_identity_flags(ctx.alice, 0);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.own_identities_retrieve", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.own_identities_retrieve();
        });

        new TestUnit<CTXAlice2>("Engine.get_trustwords", new CTXAlice2(), ctx -> {
            ctx.engine.get_trustwords(ctx.alice, ctx.bob, "en", false);
        });

        new TestUnit<CTXAlice2>("Engine.get_trustwords_for_fprs", new CTXAlice2(), ctx -> {
            ctx.engine.get_trustwords_for_fprs(ctx.alice.fpr, ctx.bob.fpr, "en", false);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.get_message_trustwords", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.get_message_trustwords(ctx.msgToBob, null, ctx.bob, "en", false);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.get_languagelist", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.get_languagelist();
        });

        new TestUnit<CTXAlice>("Engine.key_reset_trust", new CTXAlice(), ctx -> {
            ctx.engine.key_reset_trust(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.key_reset_identity", new CTXAlice(), ctx -> {
            ctx.engine.key_reset_identity(ctx.alice, "");
        });

        new TestUnit<CTXAlice>("Engine.key_reset_user", new CTXAlice(), ctx -> {
            ctx.engine.key_reset_user("fsdjugsh", ctx.alice.fpr);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.key_reset_all_own_keys", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.key_reset_all_own_keys();
        });

//        TODO: FAILS
//        new TestUnit<AdapterBaseTestContext>("Engine.deliverHandshakeResult", new AdapterBaseTestContext(), ctx -> {
//            ctx.engine.deliverHandshakeResult(SyncHandshakeResult.SyncHandshakeCancel, ctx.vID);
//        });


//        TODO: FAILS
//        [17:51] <        heck> | this one fails since: 4665:f067c9e95455
//        [17:52] <        heck> | i confirmed it still works in the parent revision 4662:71147c43e31b
//        [17:52] <        heck> | the error i get is:
//        [17:53] <        heck> | *** send message KeySync Beacon service KeySync_fsm.c:234
//        [17:53] <        heck> | Assertion failed: (msg->from && msg->from->fpr), function attach_own_key, file message_api.c, line 1581.
//        new TestUnit<AdapterBaseTestContext>("Engine.leave_device_group", new AdapterBaseTestContext(), ctx -> {
//            ctx.engine.startSync();
//            ctx.engine.leave_device_group();
//        });

        new TestUnit<CTXAlice>("Engine.enable_identity_for_sync", new CTXAlice(), ctx -> {
            ctx.engine.enable_identity_for_sync(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.disable_identity_for_sync", new CTXAlice(), ctx -> {
            ctx.engine.disable_identity_for_sync(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.config_cipher_suite", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.config_cipher_suite(CipherSuite.pEpCipherSuiteDefault);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.trustwords", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.trustwords(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.updateIdentity", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.updateIdentity(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.setOwnKey", new CTXAlice(), ctx -> {
            ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
        });

        new TestUnit<CTXAlice>("Engine.keyMistrusted", new CTXAlice(), ctx -> {
            ctx.engine.keyMistrusted(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.keyResetTrust", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.keyResetTrust(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.trustPersonalKey", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.trustPersonalKey(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.trustOwnKey", new CTXAlice(), ctx -> {
            ctx.engine.trustOwnKey(ctx.alice);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.importKey", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.blacklist_add", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.blacklist_add("43");
        });

        new TestUnit<AdapterBaseTestContext>("Engine.blacklist_delete", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.blacklist_delete("43");
        });

        new TestUnit<AdapterBaseTestContext>("Engine.blacklist_is_listed", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.blacklist_is_listed("43");
        });

        new TestUnit<AdapterBaseTestContext>("Engine.config_passive_mode", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.config_passive_mode(false);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.config_unencrypted_subject", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.config_unencrypted_subject(false);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.getCrashdumpLog", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.getCrashdumpLog(0);
        });

        new TestUnit<AdapterBaseTestContext>("Engine.getUserDirectory", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.getUserDirectory();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.getMachineDirectory", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.getMachineDirectory();
        });

        // AbstractEngine.java
        new TestUnit<AdapterBaseTestContext>("Engine.close", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.close();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.getVersion", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.getVersion();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.getProtocolVersion", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.getProtocolVersion();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.startKeyserverLookup", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.startKeyserverLookup();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.startSync", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.startSync();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.stopSync", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.stopSync();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.isSyncRunning", new AdapterBaseTestContext(), ctx -> {
            ctx.engine.isSyncRunning();
        });

        new TestUnit<AdapterBaseTestContext>("Engine.config_passphrase",new AdapterBaseTestContext() , ctx  -> {
            ctx.engine.config_passphrase("SUPERCOMPLICATEDPASSPHRASE");
        });

        new TestUnit<AdapterBaseTestContext>("Engine.config_passphrase_for_new_keys",new AdapterBaseTestContext() , ctx  -> {
            ctx.engine.config_passphrase_for_new_keys(true, "SUPERCOMPLICATEDPASSPHRASE");
        });

        new TestUnit<RegTestContext>("Engine.setDebugLogEnabled", new RegTestContext(), ctx -> {
            Engine.setDebugLogEnabled(true);
        });

        new TestUnit<RegTestContext>("Engine.setDebugLogEnabled", new RegTestContext(), ctx -> {
            Engine.getDebugLogEnabled();
        });


        TestSuite.getDefault().run();
    }
}


