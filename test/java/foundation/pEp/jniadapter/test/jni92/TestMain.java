package foundation.pEp.jniadapter.test.jni92;
import foundation.pEp.jniadapter.test.utils.TestUtils;
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
        TestUtils.logH1( "Thread Starting");
        TestMain.TestMainRun(nrEngines, useSharedEngines);
    }
}


class TestMain {
    static Vector<Engine> sharedEngines;

    public static Engine createNewEngine() throws pEpException {
        Engine e;
        TestUtils.logH2("Creating new Engine");
        e = new Engine();
        TestUtils.log("Engine created with java object ID: " + e.getId());
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
            TestUtils.logH2("engineConsumer: on engine java object ID: " + e.getId());
            ec.accept(e);
        });
    }

    public static void TestMainRun(int nrEngines, boolean useSharedEngines) {
        Consumer<Engine> c = (e) -> {
           Vector<Identity> v = e.own_identities_retrieve();

            TestUtils.log("own idents: " + v.size());
            v.forEach( i -> {
                TestUtils.log(TestUtils.identityToString(i, true));
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
        TestUtils.logH1("JNI-92 Starting");
        TestUtils.setLoggingEnabled(false);
        int nrTestruns = 1000;
        boolean multiThreaded = true;
        boolean useSharedEngines = true;
        int nrThreads = 100;
        int nrEnginesPerThread = 1;

        if(useSharedEngines) {
            sharedEngines = TestMain.createEngines(nrEnginesPerThread);
        }

        for (int run = 0; run < nrTestruns; run++ ) {
            TestUtils.logH1("Testrun Nr: " + run);
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
                        TestUtils.log("Exception joining thread" + e.toString());
                    }
                });
            }
            TestUtils.logH1("Testrun DONE" );
            System.gc();
//            TestUtils.sleep(2000);
        }
    }
}


