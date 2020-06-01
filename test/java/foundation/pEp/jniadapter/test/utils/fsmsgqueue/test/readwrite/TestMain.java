package foundation.pEp.jniadapter.test.utils.fsmsgqueue.test.readwrite;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;
import static foundation.pEp.jniadapter.test.utils.fsmsgqueue.FsMsgQueue.*;

import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.test.utils.fsmsgqueue.*;

import java.io.File;
import java.util.ArrayList;
import java.util.NoSuchElementException;


class FsMsgQueueTestContext extends AbstractTestContext {
    String qDirPath = "../resources/fsmsgqueue-test/q1";
    int msgCount = 10;
    ArrayList<String> messages;

    FsMsgQueue queue;

    @Override
    public void init() throws Throwable {
        deleteQDir();
        messages = createTestMessages(msgCount);
    }

    public void deleteQDir() {
        File qDir = new File(qDirPath);
        if (qDir.exists()) {
            log("Deleting queue dir: " + qDirPath);
            deleteRecursively(qDir);
            if (qDir.exists()) throw new RuntimeException("Cant delete Dir:" + qDirPath);
        }
    }

    public ArrayList<String> createTestMessages(int count) {
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
        TestSuite.setVerbose(false);
        FsMsgQueueTestContext testCtx = new FsMsgQueueTestContext();

        new TestUnit<FsMsgQueueTestContext>("Constructor", testCtx, ctx -> {
            log("Creating queue obj on dir:" + ctx.qDirPath);
            ctx.queue = new FsMsgQueue(ctx.qDirPath);
        }).add();

        TestUnit isEmpty = new TestUnit<FsMsgQueueTestContext>("isEmpty", testCtx, ctx -> {
            log("Checking queue is empty");
            assert ctx.queue.isEmpty();
        }).add();

        TestUnit size0 = new TestUnit<FsMsgQueueTestContext>("Size == 0", testCtx, ctx -> {
            log("Checking queue size == 0");
            assert ctx.queue.size() == 0;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("write msg[0]", testCtx, ctx -> {
            String msg = ctx.messages.get(0);
            log("adding msg[0]:" + msg);
            ctx.queue.add(msg);
        }).add();

        TestUnit notEmpty = new TestUnit<FsMsgQueueTestContext>("Not empty", testCtx, ctx -> {
            log("Checking queue not empty");
            assert !ctx.queue.isEmpty();
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Size == 1", testCtx, ctx -> {
            log("Checking queue size == 1");
            assert ctx.queue.size() == 1;
        }).add();


        new TestUnit<FsMsgQueueTestContext>("read equals write (element)", testCtx, ctx -> {
            String msg = ctx.queue.element();
            log("Read:" + msg);
            assert msg.equals(ctx.messages.get(0));
        }).add();

        new TestUnit<FsMsgQueueTestContext>("read equals write (remove)", testCtx, ctx -> {
            String msg = ctx.queue.remove();
            log("Read:" + msg);
            assert msg.equals(ctx.messages.get(0));
        }).add();

        isEmpty.add();
        size0.add();

        TestUnit addAllMsgs = new TestUnit<FsMsgQueueTestContext>("Add " + testCtx.msgCount + " msgs", testCtx, ctx -> {
            for (String msg : ctx.messages) {
                log("Adding msg:" + msg);
                ctx.queue.add(msg);
            }
        }).add();

        TestUnit sizeFull = new TestUnit<FsMsgQueueTestContext>("Size == " + testCtx.msgCount, testCtx, ctx -> {
            int size = ctx.queue.size();
            log("Size: " + size);
            assert size == ctx.msgCount;
        }).add();

        notEmpty.add();

        new TestUnit<FsMsgQueueTestContext>("read all equals write", testCtx, ctx -> {
            int msgIndex = 0;
            while (!ctx.queue.isEmpty()) {
                String msg = ctx.queue.remove();
                String expected = ctx.messages.get(msgIndex);
                log("Expected:" + expected);
                log("Returned:" + msg);
                assert msg.equals(expected);
                msgIndex++;
            }
        }).add();

        addAllMsgs.add();
        notEmpty.add();
        sizeFull.add();

        TestUnit clear = new TestUnit<FsMsgQueueTestContext>("Clear", testCtx, ctx -> {
            ctx.queue.clear();
        }).add();

        isEmpty.add();
        size0.add();

        new TestUnit<FsMsgQueueTestContext>("Empty queue: element()", testCtx, ctx -> {
            try {
                ctx.queue.element();
            } catch (NoSuchElementException e) {
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Empty queue: peek()", testCtx, ctx -> {
            assert ctx.queue.peek() == null;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Empty queue: remove()", testCtx, ctx -> {
            try {
                ctx.queue.remove();
            } catch (NoSuchElementException e) {
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Empty queue: poll()", testCtx, ctx -> {
            assert ctx.queue.poll() == null;
        }).add();


        TestSuite.run();
    }
}



