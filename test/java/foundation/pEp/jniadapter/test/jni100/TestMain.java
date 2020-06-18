package foundation.pEp.jniadapter.test.jni100;
import  foundation.pEp.jniadapter.test.pitytest.*;
import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.test.utils.*;


class TestMain {
    public static void main(String[] args) throws Exception {
        new TestUnit<AdapterBaseTestContext>("JNI-100",new AdapterBaseTestContext() , ctx -> {
            TestCallbacks cb = new TestCallbacks();

            ctx.engine.setMessageToSendCallback(cb);
            ctx.engine.setNotifyHandshakeCallback(cb);

            ctx.alice = ctx.engine.myself(ctx.alice);
            TestLogger.log(AdapterTestUtils.identityToString(ctx.alice, true));

            Message msg1 = ctx.engine.encrypt_message(ctx.msgToBob, ctx.vStr, Message.EncFormat.PEP);

            ctx.engine.key_reset_all_own_keys();

            TestLogger.log(AdapterTestUtils.identityToString(ctx.alice, true));
        }).run();
    }
}


