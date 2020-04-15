package foundation.pEp.jniadapter.test.jni92;
import foundation.pEp.jniadapter.test.utils.TestUtils;
import foundation.pEp.jniadapter.*;

import java.lang.Thread;
import java.util.Vector;


/*
This test is trying to reproduce the problem described in JNI-91
https://pep.foundation/jira/browse/JNI-81

`engine.key_reset_identity` and `engine.setMessageToSendCallback`
*/



class TestThread extends Thread {
    private String threadName = "TestThread-1";
    private Engine engine = null;

    TestThread(String threadName) {
        this.threadName = threadName;
    }

    public void run() {
        TestUtils.logH1(threadName + ": Starting thread");

        try {
            // load engine
            try {
                engine = new Engine();
                SyncCallbacks callbacks = new SyncCallbacks();
                engine.setMessageToSendCallback(callbacks);
                engine.setNotifyHandshakeCallback(callbacks);
            } catch (pEpException ex) {
                TestUtils.log(threadName + ": cannot load");
                return;
            }
            TestUtils.log(threadName + ": Engine loaded");
            if(!engine.isSyncRunning()) {
                engine.startSync();
            }

        } catch (Exception e) {
            TestUtils.log("Exception in Thread " + threadName);
            TestUtils.log(e.toString());
        }
        TestUtils.sleep(20000);
        if(engine.isSyncRunning()) {
            engine.stopSync();
        }
        TestUtils.log(threadName + ": DONE");
    }
}



class TestMain {
    public static void main(String[] args) throws Exception {
        TestUtils.logH1("JNI-92 Starting");

        Vector<TestThread> tts = new Vector<TestThread>();
        int nrThreads = 3;
        for(int i = 0; i < nrThreads; i++){
            tts.add(new TestThread("TestThread-" + i));
        }

        tts.forEach( t -> {
            t.start();
            TestUtils.sleep(2000);
        });

        tts.forEach( t -> {
            try {
                t.join();
            } catch(Exception e ){
                TestUtils.log("Exception joining thread" + e.toString());
            }
        });
    }
}


