package foundation.pEp.jniadapter.test.jni118;

import static foundation.pEp.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.RangeInt;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;

import java.util.Vector;

// re_evaluate_message_rating(Message m)
// needs a msg that holds the OptFields:
// * X-EncStatus - containing the pEpEngine internal string value of the rating
// * X-KeyList - containing the FPR's of all the receivers of the msg
//
// Handling of PEP_Rating
// When app needs to add optional fields like X-EncStatus, the value has to be the string rep for a rating.
// These internal string representations can be obtained with the method:
// * Rating.getInternalStringValue() - returning the pEpEngine internal string value
// For human readable description of all the enums, use:
// * Rating.toString()
//
// A convenience method Message.addRatingToOptFields(Rating r) would be desirable
//
// X-KeyList
// New method to generate X-Keylist formatted FPR list:
// * String Identity.toXKeyList(List<Identity> ids)
// 
// A convenience method for adding X-KeyList for a message would be desirable
// * method Message.addIdentitiesToOptFields()

// Test objectives
// re_evaluate_message_rating() equal to decrypt_message_result.rating when Message has correct OptFields
// re_evaluate_message_rating() equal to decrypt_message_result.rating when Message has random rating string on XEncStatus



class JNI1118Context extends CTXBase {
    public Message msgToBobEncrypted;
    public Message msgToBobDecrypted;
    public decrypt_message_Return msgToBobDecryptResult;

    @Override
    public JNI1118Context init() throws Throwable {
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
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Throwable {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<JNI1118Context>("re_evaluate_message_rating() equal to decrypt_message_result.rating when Message has correct OptFields", new JNI1118Context(), ctx -> {
            AdapterTestUtils.addRatingToOptFields(ctx.msgToBobDecrypted,ctx.msgToBobDecryptResult.rating.getInternalStringValue());
            AdapterTestUtils.addRcptsToOptFields(ctx.msgToBobDecrypted,Identity.toXKeyList(ctx.msgToBobDecrypted.getTo()));
            log("running re_evaluate_message_rating() on:\n" + AdapterTestUtils.msgToString(ctx.msgToBobDecrypted, false));
            Rating rat = ctx.engine.re_evaluate_message_rating(ctx.msgToBobDecrypted);
            log("re_evaluate_message_rating() result: " + rat.toString());
            assert rat == ctx.msgToBobDecryptResult.rating : "Rating is " + rat.toString() + ",but should be " + ctx.msgToBobDecryptResult.rating.toString();
        });

        new TestUnit<JNI1118Context>("re_evaluate_message_rating() equal to decrypt_message_result.rating when Message has random rating string on XEncStatus", new JNI1118Context(), ctx -> {
            AdapterTestUtils.addRatingToOptFields(ctx.msgToBobDecrypted, TestUtils.randomASCIIString(TestUtils.EASCIICharClassName.All, TestUtils.randomInt(new RangeInt(0,42))));
            AdapterTestUtils.addRcptsToOptFields(ctx.msgToBobDecrypted,Identity.toXKeyList(ctx.msgToBobDecrypted.getTo()));
            log("running re_evaluate_message_rating() on:\n" + AdapterTestUtils.msgToString(ctx.msgToBobDecrypted, false));
            Rating rat = ctx.engine.re_evaluate_message_rating(ctx.msgToBobDecrypted);
            log("re_evaluate_message_rating() result: " + rat.toString());
            assert rat == ctx.msgToBobDecryptResult.rating : "Rating is " + rat.toString() + ",but should be " + ctx.msgToBobDecryptResult.rating.toString();
        });



        TestSuite.getDefault().run();
    }
}


