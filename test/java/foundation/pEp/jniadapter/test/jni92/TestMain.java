package foundation.pEp.jniadapter.test.jni92;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.jniadapter.*;

import java.lang.Thread;
import java.util.Vector;
import java.util.function.Consumer;


/*
This test is trying to reproduce the problem described in JNI-91
https://pep.foundation/jira/browse/JNI-81

`engine.key_reset_identity` and `engine.setMessageToSendCallback`
*/

class TestThread extends Thread {
    int nrEngines = 1;
    boolean useSharedEngines = false;
    TestThread(String threadName, int nrEngines, boolean useSharedEngines) {
        Thread.currentThread().setName(threadName);
        this.nrEngines = nrEngines;
        this.useSharedEngines = useSharedEngines;
    }

    public void run() {
        TestLogger.logH1( "Thread Starting");
        TestMain.TestMainRun(nrEngines, useSharedEngines);
    }
}


class TestMain {
    static Vector<Engine> sharedEngines;

    public static Engine createNewEngine() throws pEpException {
        Engine e;
        TestLogger.logH2("Creating new Engine");
        e = new Engine();
        TestLogger.log("Engine created with java object ID: " + e.getId());
        return e;
    }

    public static Vector<Engine> createEngines(int nrEngines) throws pEpException {
        Vector<Engine> ev = new Vector<Engine>();
        for(int i = 0; i < nrEngines; i++) {
            ev.add(createNewEngine());
        }
        return ev;
    }

    public static void engineConsumer(Vector<Engine> ev, Consumer<Engine> ec) {
        ev.forEach(e -> {
            TestLogger.logH2("engineConsumer: on engine java object ID: " + e.getId());
            ec.accept(e);
        });
    }

    public static void TestMainRun(int nrEngines, boolean useSharedEngines) {
        Consumer<Engine> c = (e) -> {
           Vector<Identity> v = e.own_identities_retrieve();

            TestLogger.log("own idents: " + v.size());
            v.forEach( i -> {
                TestLogger.log(AdapterTestUtils.identityToString(i, true));
            });
            e.getVersion();
            e.OpenPGP_list_keyinfo("");
        };

        if(useSharedEngines) {
            TestMain.engineConsumer(sharedEngines, c);
        } else {
            Vector<Engine> threadLocalEngines = TestMain.createEngines(nrEngines);
            TestMain.engineConsumer(threadLocalEngines, c);
        }
    }

    public static void main(String[] args) {
        TestLogger.logH1("JNI-92 Starting");
        TestLogger.setLoggingEnabled(false);
        int nrTestruns = 1000;
        boolean multiThreaded = true;
        boolean useSharedEngines = true;
        int nrThreads = 100;
        int nrEnginesPerThread = 1;

        if(useSharedEngines) {
            sharedEngines = TestMain.createEngines(nrEnginesPerThread);
        }

        for (int run = 0; run < nrTestruns; run++ ) {
            TestLogger.logH1("Testrun Nr: " + run);
            if (!multiThreaded) {
                // Single Threaded
                TestMainRun(nrEnginesPerThread, useSharedEngines);
            } else {
                // Mutli Threaded
                Vector<TestThread> tts = new Vector<TestThread>();
                for (int i = 0; i < nrThreads; i++) {
                    tts.add(new TestThread("TestThread-" + i, nrEnginesPerThread, useSharedEngines));
                }

                tts.forEach(t -> {
                    t.start();
                });

                tts.forEach(t -> {
                    try {
                        t.join();
                    } catch (Exception e) {
                        TestLogger.log("Exception joining thread" + e.toString());
                    }
                });
            }
            TestLogger.logH1("Testrun DONE" );
            System.gc();
//            TestUtils.sleep(2000);
        }
    }
}


