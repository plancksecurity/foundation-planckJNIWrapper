import foundation.pEp.jniadapter.*;
import java.util.Vector;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.Thread;
import java.lang.InterruptedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/*
This test is trying to reproduce the problem described in JNI-88
https://pep.foundation/jira/browse/JNI-88

During the start up of the (banking-)connector we can see this behavior:
```
java: throw_pEp_exception.cc:268: jint pEp::JNIAdapter::throw_pEp_Exception(JNIEnv*, PEP_STATUS): Assertion `ex' failed.

Aborted (core dumped)
```
We try to set OwnId simultaneously from two threads(with two different engine instances). There is also high load on the system (pEp-unreleted).

Tuning tracing on in pgp_sequoia.c reveals this:
```
15:02 < jonas> cert_find: (339945131124ECCBD16D46104F470C66E44B21E4, 0)
15:02 < jonas> key_load: -> PEP_KEY_NOT_FOUND
15:02 < jonas> cert_find: Looking up 339945131124ECCBD16D46104F470C66E44B21E4: PEP_KEY_NOT_FOUND
15:02 < jonas> cert_find: (339945131124ECCBD16D46104F470C66E44B21E4, 0) -> PEP_KEY_NOT_FOUND
15:02 < jonas> cert_save: Saving certificate: database is locked: PEP_UNKNOWN_ERROR
15:02 < jonas> cert_save: (339945131124ECCBD16D46104F470C66E44B21E4) -> PEP_UNKNOWN_ERROR
15:02 < jonas> pgp_generate_keypair: saving TSK: PEP_CANNOT_CREATE_KEY
15:02 < jonas> pgp_generate_keypair: -> PEP_CANNOT_CREATE_KEY
15:02 < jonas> cert_save: (1863F66D52A7E79B69DBFCD84EB613332303DE33) -> PEP_STATUS_OK

[11:14] <       dietz> | Schreib' Dir einfach was, das aus ein paar threads zyklisch myself() ruft (für jeweils neue Identitäten).
[11:14] <       dietz> | Sollte nach wenigen Sekunden krachen.
[11:15] <       dietz> | Wobei fdik in #dev meinte, wir dürfen myself() nicht parallel rufen. Scherzkeks.
[11:15] <       dietz> | Spätestens beim plugin *müssen* wir aus zwei Prozessen auf die DB lesen und schreiben.
*/

class TestThread extends Thread {
    private String threadName = "Default thread name";
    private int numIter = Integer.MAX_VALUE;
    private Engine e = null;
    private Identity i1 = null;

    TestThread(String name, int iters, Engine engine) {
        threadName = name;
        numIter = iters;
        e = engine;
    }

    TestThread(String name, int iters) {
        threadName = name;
        numIter = iters;
        try {
            e = new Engine();
        }
        catch (pEpException ex) {
            System.out.println(threadName + ": cannot load");
            System.exit(0);
            return;
        }
        System.out.println(threadName + ": Engine loaded");
    }

    private void newIdentAndMyself(String name) {
        System.out.print(threadName + ": testMyselfWithName: \"" + name + "\" -> ");
        Identity i2 = new Identity();
        i2.me = true;
        i2.user_id = name;
        i2.username = name;
        i2.address = name + "@test.org";
        i2 = e.myself(i2);
        System.out.println("FPR: \"" + i2.fpr + "\"");
    }

    public void run() {
        System.out.println(threadName + ": Starting thread with numIters: " + numIter);
        try {
            for(int i = 0; i < numIter; i++) {
                newIdentAndMyself(threadName + "_iter-" + Integer.toString(i));
            }
        } catch (Exception e) {
            System.out.println("Exception in Thread " + threadName);
            System.out.println(e.toString());
            System.exit(0);
        }
        System.out.println(threadName + ": DONE");
    }
}

class JNI_88 {
    public static void main(String[] args) {
        // Test parameters
        boolean useSharedEngine = true;
        int numThreads = 2000;
        int numIters = 1000000000;

        Engine sharedEngine = null;
        Vector<Thread> vecT = new Vector<Thread>();
        System.out.println("Creating num threads: " + numThreads);

        if(useSharedEngine) {
            sharedEngine = new Engine();
        }

        for(int i=0; i < numThreads; i++) {
            String tName = "Thread-" + String.valueOf(i);
            System.out.println("Creating Thread: \"" + tName + "\"");
            if(useSharedEngine) {
                vecT.add(new TestThread(tName, numIters, sharedEngine));
            } else {
                vecT.add(new TestThread(tName, numIters));
            }
        }

        System.out.println("num threads created: " + vecT.size());

        for(Thread t : vecT) {
            System.out.println("Starting Thread: \"" + t.getName() + "\"");
            t.start();
        }
    }
}
