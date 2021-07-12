package foundation.pEp.jniadapter.test.jni161;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.Message;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.model.Role;
import foundation.pEp.jniadapter.test.utils.model.TestModel;
import foundation.pEp.jniadapter.test.utils.model.TestNode;
import foundation.pEp.jniadapter.test.utils.model.pEpTestIdentity;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static foundation.pEp.pitytest.TestLogger.log;


class Jni161TestContext extends AbstractTestContext {
    // Model
    public TestModel<pEpTestIdentity, TestNode<pEpTestIdentity>> model = new TestModel(pEpTestIdentity::new, TestNode::new);

    // Basic
    public Engine engine;

    // Identities
    public Identity alice;
    public Identity bob;

    public int blobSizeMB = 1;
    public int blobCount = 3;

    public Vector<Message> messages = new Vector<>();

    public Jni161TestContext init() throws Throwable {
        engine = new Engine();
        // fetch data for the idents
        alice = model.getIdent(Role.ALICE).pEpIdent;
        bob = model.getIdent(Role.BOB).pEpIdent;

        // create keys etc..
        alice = engine.myself(alice);
        bob = engine.myself(bob);

        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        Jni161TestContext ctx161 = new Jni161TestContext();

        new TestUnit<Jni161TestContext>("Proof leaking messages ", ctx161, ctx -> {
            while (true) {
                log("Thread: " + Thread.currentThread().getId() + " - Alloc");
                Message msg = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                msg.setAttachments(AdapterTestUtils.makeNewTestBlobList(ctx.blobSizeMB * 1024 * 1024, "dummyblob", "text/plain", ctx.blobCount));
                msg.setLongmsg(TestUtils.randomASCIIString(TestUtils.EASCIICharClassName.Alpha, ctx.blobSizeMB * 1024 * 1024));
//                msg.close();
                TestUtils.sleep(10);
            }
        });

        new TestUnit<Jni161TestContext>("Proof leaking messages ", ctx161, ctx -> {
            new Thread(() -> {
                while (true) {
                    log("Thread: " + Thread.currentThread().getId() + " - Alloc");
                    Message msg = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                    msg.setAttachments(AdapterTestUtils.makeNewTestBlobList(ctx.blobSizeMB * 1024 * 1024, "dummyblob", "text/plain", ctx.blobCount));
                    msg.setLongmsg(TestUtils.randomASCIIString(TestUtils.EASCIICharClassName.Alpha, ctx.blobSizeMB * 1024 * 1024));
                    ctx.messages.add(msg);
                }
            }).start();

            new Thread(() -> {
                while (true) {
//                    log("Thread: " + Thread.currentThread().getId() + " - Dealloc");
                    if(ctx.messages.size() > 0) {
                        log("Messages: " + ctx.messages.size());
                        log("InstanceCount: " + Message.getInstanceCount());
                        ctx.messages.remove(0).close();
                    }
                }
            }).start();
        }).run();


        new TestUnit<Jni161TestContext>("Proof leaking messages ", ctx161, ctx -> {
            ExecutorService allocPool = Executors.newFixedThreadPool(10);
            ExecutorService deallocPool = Executors.newFixedThreadPool(10);
            while (true) {
                ThreadPoolExecutor allocExec = (ThreadPoolExecutor) allocPool;
                if (allocExec.getActiveCount() < allocExec.getMaximumPoolSize()) {
                    allocPool.submit(() -> {
                        log("Thread: " + Thread.currentThread().getId() + " - Alloc");
                        Message msg = AdapterTestUtils.makeNewTestMessage(ctx.alice, ctx.bob, Message.Direction.Outgoing);
                        msg.setAttachments(AdapterTestUtils.makeNewTestBlobList(ctx.blobSizeMB * 1024 * 1024, "dummyblob", "text/plain", ctx.blobCount));
                        msg.setLongmsg(TestUtils.randomASCIIString(TestUtils.EASCIICharClassName.Alpha, ctx.blobSizeMB * 1024 * 1024));
                        ctx.messages.add(msg);
                        log("MessagesADD: " + ctx.messages.size());
                    });
                }

                if (ctx.messages.size() > 1) {
                    ThreadPoolExecutor deallocExec = (ThreadPoolExecutor) deallocPool;
                    if (deallocExec.getActiveCount() < deallocExec.getMaximumPoolSize()) {
                        deallocPool.submit(() -> {
                            log("Thread: " + Thread.currentThread().getId() + " - Dealloc");
                            log("Messages: " + ctx.messages.size());
                            log("InstanceCount: " + Message.getInstanceCount());
                            for (Message msg : ctx.messages) {
                                msg.close();
                            }
                        });
                    }
                }
                TestUtils.sleep(10);
            }
        });
//        TestSuite.getDefault().run(); // comment this out
    }
}


