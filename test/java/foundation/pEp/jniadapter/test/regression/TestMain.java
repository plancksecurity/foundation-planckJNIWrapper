package foundation.pEp.jniadapter.test.regression;
import foundation.pEp.pitytest.*;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.*;

import java.util.ArrayList;
import java.util.Vector;

import static foundation.pEp.pitytest.TestLogger.log;

class CTXAlice extends CTXBase {
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
        engine.set_comm_partner_key(bob, "F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1");
        bob = engine.updateIdentity(bob);
        return this;
    }
}

// Almost exact copy of JNI1118Context, we need a shared context hierarchy
class CTXReEvaluateMessageRating extends CTXBase {
    public Message msgToBobEncrypted;
    public Message msgToBobDecrypted;
    public decrypt_message_Return msgToBobDecryptResult;

    @Override
    public CTXReEvaluateMessageRating init() throws Throwable {
        super.init();
        alice = engine.myself(alice);
        engine.importKey(keyBobPub);
        engine.set_comm_partner_key(bob, "F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1");

        Vector<Identity> msgToBobRcpts = new Vector<>();
        msgToBobRcpts.add(bob);
        msgAliceToBob.setTo(msgToBobRcpts);

        msgToBobEncrypted = engine.encrypt_message(msgAliceToBob, null, Message.EncFormat.PEP);
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
        
        new TestUnit<CTXBase>("Engine.myself", new CTXBase(), ctx -> {
            ctx.alice = ctx.engine.myself(ctx.alice);
        });

        new TestUnit<CTXBase>("Engine.encrypt_message", new CTXBase(), ctx -> {
            ctx.engine.encrypt_message(ctx.msgAliceToBob, null, Message.EncFormat.PEP);
        });

        new TestUnit<CTXAlice>("Engine.encrypt_message_and_add_priv_key", new CTXAlice(), ctx -> {
            ctx.engine.encrypt_message_and_add_priv_key(ctx.msgAliceToAlice, ctx.alice.fpr);
        });

        new TestUnit<CTXAlice>("Engine.encrypt_message_for_self", new CTXAlice(), ctx -> {
            ctx.engine.encrypt_message_for_self(ctx.alice, ctx.msgAliceToAlice, null);
        });

        new TestUnit<CTXBase>("Engine.decrypt_message", new CTXBase(), ctx -> {
            ctx.engine.decrypt_message(ctx.msgAliceToAlice, new Vector<String>(), 0);
        });

        new TestUnit<CTXReEvaluateMessageRating>("Engine.re_evaluate_message_rating", new CTXReEvaluateMessageRating(), ctx -> {
            ctx.engine.re_evaluate_message_rating(ctx.msgToBobDecrypted);
        });

        new TestUnit<CTXBase>("Engine.outgoing_message_rating", new CTXBase(), ctx -> {
            ctx.engine.outgoing_message_rating(ctx.msgAliceToBob);
        });

        new TestUnit<CTXBase>("Engine.outgoing_message_rating_preview", new CTXBase(), ctx -> {
            ctx.engine.outgoing_message_rating_preview(ctx.msgAliceToBob);
        });

        new TestUnit<CTXAlice>("Engine.get_identity", new CTXAlice(), ctx -> {
            ctx.engine.get_identity(ctx.alice.address, ctx.alice.user_id);
        });

        new TestUnit<CTXAlice>("Engine.identity_rating", new CTXAlice(), ctx -> {
            ctx.engine.identity_rating(ctx.alice);
        });

//        TODO: FAILS
//        new TestUnit<CTXAlice2>("Engine.own_message_private_key_details", new CTXAlice2(), ctx -> {
//            ctx.engine.own_message_private_key_details(ctx.msgToSelf);
//        });

        new TestUnit<CTXBase>("Engine.OpenPGP_list_keyinfo", new CTXBase(), ctx -> {
            ctx.engine.OpenPGP_list_keyinfo("");
        });

        new TestUnit<CTXAlice>("Engine.set_identity_flags", new CTXAlice(), ctx -> {
            ctx.engine.set_identity_flags(ctx.alice, 0);
        });

        new TestUnit<CTXAlice>("Engine.unset_identity_flags", new CTXAlice(), ctx -> {
            ctx.engine.unset_identity_flags(ctx.alice, 0);
        });

        new TestUnit<CTXBase>("Engine.own_identities_retrieve", new CTXBase(), ctx -> {
            ctx.engine.own_identities_retrieve();
        });

        new TestUnit<CTXAlice2>("Engine.get_trustwords", new CTXAlice2(), ctx -> {
            ctx.engine.get_trustwords(ctx.alice, ctx.bob, "en", false);
        });

        new TestUnit<CTXBase>("Engine.get_message_trustwords", new CTXBase(), ctx -> {
            ctx.engine.get_message_trustwords(ctx.msgAliceToBob, null, ctx.bob, "en", false);
        });

        new TestUnit<CTXBase>("Engine.get_languagelist", new CTXBase(), ctx -> {
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

        new TestUnit<CTXBase>("Engine.key_reset_all_own_keys", new CTXBase(), ctx -> {
            ctx.engine.key_reset_all_own_keys();
        });

//        TODO: FAILS
//        new TestUnit<CTXBase>("Engine.deliverHandshakeResult", new CTXBase(), ctx -> {
//            ctx.engine.deliverHandshakeResult(SyncHandshakeResult.SyncHandshakeCancel, ctx.vID);
//        });


//        TODO: FAILS
//        [17:51] <        heck> | this one fails since: 4665:f067c9e95455
//        [17:52] <        heck> | i confirmed it still works in the parent revision 4662:71147c43e31b
//        [17:52] <        heck> | the error i get is:
//        [17:53] <        heck> | *** send message KeySync Beacon service KeySync_fsm.c:234
//        [17:53] <        heck> | Assertion failed: (msg->from && msg->from->fpr), function attach_own_key, file message_api.c, line 1581.
//        new TestUnit<CTXBase>("Engine.leave_device_group", new CTXBase(), ctx -> {
//            ctx.engine.startSync();
//            ctx.engine.leave_device_group();
//        });

        new TestUnit<CTXAlice>("Engine.enable_identity_for_sync", new CTXAlice(), ctx -> {
            ctx.engine.enable_identity_for_sync(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.disable_identity_for_sync", new CTXAlice(), ctx -> {
            ctx.engine.disable_identity_for_sync(ctx.alice);
        });

        new TestUnit<CTXBase>("Engine.config_cipher_suite", new CTXBase(), ctx -> {
            ctx.engine.config_cipher_suite(CipherSuite.pEpCipherSuiteDefault);
        });

        new TestUnit<CTXAlice>("Engine.trustwords", new CTXAlice(), ctx -> {
            ctx.engine.trustwords(ctx.alice);
        });

        new TestUnit<CTXBase>("Engine.updateIdentity", new CTXBase(), ctx -> {
            ctx.engine.updateIdentity(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.setOwnKey", new CTXAlice(), ctx -> {
            ctx.engine.setOwnKey(ctx.alice, ctx.alice.fpr);
        });

        new TestUnit<CTXAlice>("Engine.keyMistrusted", new CTXAlice(), ctx -> {
            ctx.engine.keyMistrusted(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.keyResetTrust", new CTXAlice(), ctx -> {
            ctx.engine.keyResetTrust(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.trustPersonalKey", new CTXAlice(), ctx -> {
            ctx.engine.trustPersonalKey(ctx.alice);
        });

        new TestUnit<CTXAlice>("Engine.trustOwnKey", new CTXAlice(), ctx -> {
            ctx.engine.trustOwnKey(ctx.alice);
        });

        new TestUnit<CTXBase>("Engine.importKey", new CTXBase(), ctx -> {
            ctx.engine.importKey(ctx.keyBobPub);
        });

        new TestUnit<CTXBase>("Engine.set_comm_partner_key", new CTXBase(), ctx -> {
            ctx.engine.set_comm_partner_key(ctx.bob, "F804FBE1781F3E2F6158F9F709FB5BDA72BE51C1");
        });

        new TestUnit<CTXBase>("Engine.config_passive_mode", new CTXBase(), ctx -> {
            ctx.engine.config_passive_mode(false);
        });

        new TestUnit<CTXBase>("Engine.config_unencrypted_subject", new CTXBase(), ctx -> {
            ctx.engine.config_unencrypted_subject(false);
        });

        new TestUnit<CTXBase>("Engine.getCrashdumpLog", new CTXBase(), ctx -> {
            ctx.engine.getCrashdumpLog(0);
        });

        new TestUnit<CTXBase>("Engine.getUserDirectory", new CTXBase(), ctx -> {
            ctx.engine.getUserDirectory();
        });

        new TestUnit<CTXBase>("Engine.getMachineDirectory", new CTXBase(), ctx -> {
            ctx.engine.getMachineDirectory();
        });

        // AbstractEngine.java
        new TestUnit<CTXBase>("Engine.close", new CTXBase(), ctx -> {
            ctx.engine.close();
        });

        new TestUnit<CTXBase>("Engine.getVersion", new CTXBase(), ctx -> {
            ctx.engine.getVersion();
        });

        new TestUnit<CTXBase>("Engine.getProtocolVersion", new CTXBase(), ctx -> {
            ctx.engine.getProtocolVersion();
        });

        new TestUnit<CTXBase>("Engine.startSync", new CTXBase(), ctx -> {
            ctx.engine.startSync();
        });

        new TestUnit<CTXBase>("Engine.stopSync", new CTXBase(), ctx -> {
            ctx.engine.stopSync();
        });

        new TestUnit<CTXBase>("Engine.isSyncRunning", new CTXBase(), ctx -> {
            ctx.engine.isSyncRunning();
        });

        new TestUnit<CTXBase>("Engine.config_passphrase",new CTXBase() , ctx  -> {
            ctx.engine.config_passphrase("SUPERCOMPLICATEDPASSPHRASE");
        });

        new TestUnit<CTXBase>("Engine.config_passphrase_for_new_keys",new CTXBase() , ctx  -> {
            ctx.engine.config_passphrase_for_new_keys(true, "SUPERCOMPLICATEDPASSPHRASE");
        });

        new TestUnit<CTXBase>("Engine.setDebugLogEnabled", new CTXBase(), ctx -> {
            Engine.setDebugLogEnabled(true);
        });

        new TestUnit<CTXBase>("Engine.setDebugLogEnabled", new CTXBase(), ctx -> {
            Engine.getDebugLogEnabled();
        });


        TestSuite.getDefault().run();
    }
}


