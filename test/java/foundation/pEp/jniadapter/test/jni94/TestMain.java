package foundation.pEp.jniadapter.test.jni94;
import foundation.pEp.pitytest.*;
import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.test.utils.CTXBase;

/*
This test tries to use the feature described in in JNI-94
https://pep.foundation/jira/browse/JNI-94

`engine.getMachineDirectory()` and `engine.getUserDirectory()`
*/


class TestMain {
    public static void main(String[] args) {
        new TestUnit<CTXBase>("JNI-94", new CTXBase(), ctx -> {
            TestLogger.logH2("Creating new Engine");
            ctx.engine = new Engine();
            Sync.DefaultCallback callbacks = new Sync.DefaultCallback();
            ctx.engine.setMessageToSendCallback(callbacks);
            TestLogger.logH2("Machine directory: ");
            TestLogger.log(ctx.engine.getMachineDirectory());

            TestLogger.logH2("User directory:" );
            TestLogger.log(ctx.engine.getUserDirectory());
        }).run();
    }
}
