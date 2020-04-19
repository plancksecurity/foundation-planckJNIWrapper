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
    TestThread(String threadName) {
        Thread.currentThread().setName(threadName);
    }

    public void run() {
        TestUtils.logH1( "Thread Starting");
        TestMain.TestMainRun(2);
    }
}


class TestMain {

    public static Engine createNewEngine() throws pEpException {
        Engine e;
        TestUtils.logH2("Creating new Engine");
        e = new Engine();
        TestUtils.log("Engine created\n");
        return e;
    }

    public static Vector<Engine> createEngines(int nrEngines) throws pEpException {
        Vector<Engine> ev = new Vector<Engine>();
        for(int i = 0; i < nrEngines; i++) {
            ev.add(createNewEngine());
        }
        return ev;
    }

    public static void own_identities_retrieve_on_EngineVector(Vector<Engine> ev) {
        ev.forEach(e -> {
            TestUtils.logH2("own_identities_retrieve()");
            e.own_identities_retrieve();
            TestUtils.log("\n");
        });
    }



    public static void TestMainRun(int nrEngines) {
        Vector<Engine> engineVector = TestMain.createEngines(nrEngines);
//        TestUtils.sleep(200);
        TestMain.own_identities_retrieve_on_EngineVector(engineVector);
    }

    public static void main(String[] args) throws Exception {
        TestUtils.logH1("JNI-92 Starting");
        boolean multiThreaded = true;
        int nrEngines = 3;

        if (!multiThreaded) {
            // Single Threaded
            TestMainRun(nrEngines);
        } else {
            // Mutli Threaded
            Vector<TestThread> tts = new Vector<TestThread>();
            int nrThreads = nrEngines;
            for (int i = 0; i < nrThreads; i++) {
                tts.add(new TestThread("TestThread-" + i));
//                TestUtils.sleep(200);
            }

            tts.forEach(t -> {
                t.start();
//                TestUtils.sleep(2000);
            });

            tts.forEach(t -> {
                try {
                    t.join();
                } catch (Exception e) {
                    TestUtils.log("Exception joining thread" + e.toString());
                }
            });
        }
    }
}


