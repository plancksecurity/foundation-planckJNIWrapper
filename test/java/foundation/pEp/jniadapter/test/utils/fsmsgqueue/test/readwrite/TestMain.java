package foundation.pEp.jniadapter.test.utils.fsmsgqueue.test.readwrite;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.*;
import foundation.pEp.jniadapter.test.utils.fsmsgqueue.*;

import java.io.File;
import java.util.ArrayList;


class FsMsgQueueTestContext extends AbstractTestContext {
    String qDirPath = "../resources/fsmsgqueue-test/q1";
    int msgCount = 4;
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
            log("Deleting Queue Dir: " + qDirPath);
            deleteRecursively(qDir);
            if (qDir.exists()) throw new RuntimeException("Cant delete Dir:" + qDirPath);
        }
    }

    public ArrayList<String> createTestMessages(int count) {
        log("Creating Test messages");
        ArrayList<String> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String msg = "TestMessage " + i;
            msg += "\nLine 2 of " + msg;
            messages.add(msg);
            log("Creating msg: " + msg);
        }
        return messages;
    }

    // FileUtils

    boolean deleteRecursively(File dir) {
        File[] allContents = dir.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteRecursively(file);
            }
        }
        return dir.delete();
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.setVerbose(false);
        FsMsgQueueTestContext testCtx = new FsMsgQueueTestContext();

        new TestUnit<FsMsgQueueTestContext>("Constructor", testCtx, ctx -> {
            ctx.queue = new FsMsgQueue(ctx.qDirPath);
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Add", testCtx, ctx -> {
            for (String msg : ctx.messages) {
                log("Adding msg:" + msg);
                ctx.queue.add(msg);
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Element", testCtx, ctx -> {
            String msg = ctx.queue.element();
            log("Element: " + msg);
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Size", testCtx, ctx -> {
            int size = ctx.queue.size();
            log("Size: " + size);
            assert size == ctx.msgCount;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("isEmpty", testCtx, ctx -> {
            boolean isEmpty = ctx.queue.isEmpty();
            log("isEmpty: " + isEmpty);
            assert !isEmpty;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("remove", testCtx, ctx -> {
            while (!ctx.queue.isEmpty()) {
                String msg = ctx.queue.remove();
                log("remove: " + msg);
            }
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Size 0", testCtx, ctx -> {
            int size = ctx.queue.size();
            log("Size: " + size);
            assert size == 0;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("isEmpty true", testCtx, ctx -> {
            boolean isEmpty = ctx.queue.isEmpty();
            log("isEmpty: " + isEmpty);
            assert isEmpty;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Element on empty", testCtx, ctx -> {
            try {
                String msg = ctx.queue.element();
                log("Element: " + msg);
            } catch (Exception e) {
                return;
            }
            assert false;
        }).add();

        new TestUnit<FsMsgQueueTestContext>("Remove on empty", testCtx, ctx -> {
            try {
                String msg = ctx.queue.remove();
                log("Element: " + msg);
            } catch (Exception e) {
                return;
            }
            assert false;
        }).add();


        TestSuite.run();
    }
}



