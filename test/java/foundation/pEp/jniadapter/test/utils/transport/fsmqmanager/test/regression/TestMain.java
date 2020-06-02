package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.regression;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import static foundation.pEp.jniadapter.test.framework.TestUtils.deleteRecursively;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmsgqueue.FsMsgQueue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;


class FsMsgQueueTestContext extends AbstractTestContext {
    String ownAddress = "Alice";
    String ownQDir = "../resources/fsmsgqueue-test/alice";
    String bobAddress = "Bob";
    String bobQDir = "../resources/fsmsgqueue-test/bob";

    int msgCount = 10;
    ArrayList<String> messages;

    FsMQManager qm;
    FsMQIdentity self = null;
    FsMQIdentity bob = null;

    @Override
    public void init() throws Throwable {
        messages = createTestMessages(msgCount);
    }

    public java.util.ArrayList<String> createTestMessages(int count) {
        log("Creating Test messages");
        ArrayList<String> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String msg = "TestMessage " + i;
            //  msg += "\nLine 2 of " + msg;
            messages.add(msg);
            log("Creating msg: " + msg);
        }
        return messages;
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(true);
        FsMsgQueueTestContext testCtx = new FsMsgQueueTestContext();

        new TestUnit<FsMsgQueueTestContext>("Create own ident: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.self = new FsMQIdentity(ctx.ownAddress, ctx.ownQDir);
            assert ctx.self != null : "null";
            assert ctx.self.getAddress().equals(ctx.ownAddress): "Address mismatch";
            assert ctx.self.getqDir().equals(ctx.ownQDir): "qDir mismatch";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Constructor with: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.qm = new FsMQManager(ctx.self);
            assert ctx.qm != null : "null";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Ident known: " + testCtx.ownAddress, testCtx, ctx -> {
            FsMQIdentity self = ctx.qm.getIdentityForAddress(ctx.ownAddress);
            assert self != null : "null";
            assert self.equals(ctx.self) : "Obj mismatch";
            log("Address: " + self.getAddress());
            log("qDir: " + self.getqDir());
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Create ident " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.bob = new FsMQIdentity(ctx.bobAddress, ctx.bobQDir);
            assert ctx.bob != null : "null";
            assert ctx.bob.getAddress().equals(ctx.bobAddress) : "Address mismatch";
            assert ctx.bob.getqDir().equals(ctx.bobQDir) : "qDir mismatch";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Ident unknown: " + testCtx.bobAddress, testCtx, ctx -> {
            try {
                FsMQIdentity self = ctx.qm.getIdentityForAddress(ctx.bobAddress);
            } catch (UnknownIdentityException e) {
                return;
            }
            assert false : "Ident is known but shouldnt";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Add ident " + testCtx.bobAddress, testCtx, ctx -> {
            assert ctx.qm.addOrUpdateIdentity(ctx.bob) : "Identity updated but should have been added";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Ident known: " + testCtx.bobAddress, testCtx, ctx -> {
            FsMQIdentity bob = ctx.qm.getIdentityForAddress(ctx.bobAddress);
            assert bob.equals(ctx.bob) : "Obj mismatch";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Update ident " + testCtx.bobAddress, testCtx, ctx -> {
            assert !ctx.qm.addOrUpdateIdentity(ctx.bob) : "Ident got added but should have been updated";
        }).add();

        new TestUnit<FsMsgQueueTestContext>("ClearOwnQueue: " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.qm.clearOwnQueue();
        }).add();

        new TestUnit<FsMsgQueueTestContext>("waitForMsg timeout", testCtx, ctx -> {
            log("waitForMessage with timeout...");
            try {
                 ctx.qm.waitForMsg(3);
            } catch(IOException e) {
                throw new RuntimeException(e.toString());
            } catch(ClassNotFoundException e) {
                throw new RuntimeException(e.toString());
            } catch (TimeoutException e) {
                return;
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("sendMsgTo self " + testCtx.ownAddress, testCtx, ctx -> {
            String msg = ctx.messages.get(0);
            log("TX MSG: " + msg);
            try {
                ctx.qm.sendMsgToIdentity(ctx.self, msg);
            } catch(IOException e) {
                throw new RuntimeException(e.toString());
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("waitForMsg", testCtx, ctx -> {
            String msg = null;
            try {
                msg = ctx.qm.waitForMsg(10);
            } catch(Exception e) {
                throw new RuntimeException(e.toString());
            }
            log("RX MSG: " + msg);
            assert msg.equals(ctx.messages.get(0));
        }).add();


        TestSuite.run();
    }
}



