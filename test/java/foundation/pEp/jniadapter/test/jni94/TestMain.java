package foundation.pEp.jniadapter.test.jni94;
import foundation.pEp.jniadapter.*;
import foundation.pEp.jniadapter.test.utils.TestUtils;

import java.lang.Thread;


/*
This test tries to use the feature described in in JNI-94
https://pep.foundation/jira/browse/JNI-94

`engine.getMachineDirectory()` and `engine.getUserDirectory()`
*/


class TestMain {
    public static void main(String[] args) {
        Engine engine;
        try {
            TestUtils.logH2("Creating new Engine");
            engine = new Engine();
            Sync.DefaultCallback callbacks = new Sync.DefaultCallback();
            engine.setMessageToSendCallback(callbacks);
            TestUtils.logH2("Machine directory: ");
            TestUtils.log(engine.getMachineDirectory());
    
            TestUtils.logH2("User directory:" );
            TestUtils.log(engine.getUserDirectory());
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            return;
        }
    }
}
