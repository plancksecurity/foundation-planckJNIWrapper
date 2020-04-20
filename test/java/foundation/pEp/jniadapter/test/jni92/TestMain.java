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
    TestThread(String threadName, int nrEngines) {
        Thread.currentThread().setName(threadName);
        this.nrEngines = nrEngines;
    }

    public void run() {
        TestUtils.logH1( "Thread Starting");
        TestMain.TestMainRun(nrEngines);
    }
}


class TestMain {
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

    public static void TestMainRun(int nrEngines) {
        Vector<Engine> engineVector = TestMain.createEngines(nrEngines);
        Consumer<Engine> c = (e) -> {
           Vector<Identity> v = e.own_identities_retrieve();

            TestUtils.log("own idents: " + v.size());
            v.forEach( i -> {
                TestUtils.log(TestUtils.identityToString(i));
            });
            e.getVersion();
            e.OpenPGP_list_keyinfo("");
        };
        TestMain.engineConsumer(engineVector, c);
    }

    public static void main(String[] args) {
        TestUtils.logH1("JNI-92 Starting");

        int nrTestruns = 100;
        boolean multiThreaded = true;
        int nrThreads = 200;
        int nrEnginesPerThread = 100;

        for (int run = 0; run < nrTestruns; run++ ) {
            TestUtils.logH1("Testrun Nr: " + run);
            if (!multiThreaded) {
                // Single Threaded
                TestMainRun(nrEnginesPerThread);
            } else {
                // Mutli Threaded
                Vector<TestThread> tts = new Vector<TestThread>();
                for (int i = 0; i < nrThreads; i++) {
                    tts.add(new TestThread("TestThread-" + i, nrEnginesPerThread));
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


