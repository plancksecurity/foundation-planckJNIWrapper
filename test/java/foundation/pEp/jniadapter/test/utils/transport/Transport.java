package foundation.pEp.jniadapter.test.utils.transport;

import foundation.pEp.jniadapter.test.utils.model.TestIdentity;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQIdentity;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQManager;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQMessage;
import foundation.pEp.pitytest.utils.Pair;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class Transport {
    private FsMQManager fsMQTransport = null;
    private ProcessingSenderThread sender = null;
    private ProcessingReceiverThread receiver = null;
    private FsMQIdentity myself = null;
    private List<TestIdentity> peers = null;

    // StringMsg queues
    private LinkedBlockingQueue<Pair<String, TestIdentity>> txQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<String> rxQueue = new LinkedBlockingQueue<>();

    public Transport(FsMQIdentity ownIdent, List<TestIdentity> peers) {
        this.myself = ownIdent;
        this.peers = peers;
        this.sender = new ProcessingSenderThread(this, txQueue);
        this.receiver = new ProcessingReceiverThread(this, rxQueue);
        this.fsMQTransport = new FsMQManager(ownIdent);

        for (TestIdentity ti : peers) {
            fsMQTransport.identities.addAll(ti.getAllTransportIdents());
        }
    }

    public void setAsyncTxProcessor(StringProcessorInterface<String> asyncTxProcessor) {
        this.sender.setMsgProcessor(asyncTxProcessor);
    }

    public void setAsyncRxProcessor(StringProcessorInterface<String> asyncRxProcessor) {
        this.receiver.setMsgProcessor(asyncRxProcessor);
    }

    public void clearOwnQueue() {
        fsMQTransport.clearOwnQueue();
    }

    public boolean canReceiveAsync() {
        return !rxQueue.isEmpty();
    }

    public void sendAsync(String msg, TestIdentity receiver) {
        txQueue.add(new Pair<>(msg, receiver));
    }

    public String receiveAsyncNonBlocking() {
        return rxQueue.remove();
    }

    public void sendRaw(TestIdentity receiver, String msg) {
        TestIdentity id = receiver;
        for (FsMQIdentity tID : id.getAllTransportIdents()) {
            fsMQTransport.sendMessage(tID.getAddress(), msg);
//            log("send() to: " + tID.getAddress());
        }
    }

    public String receiveRaw() {
        FsMQMessage rx = fsMQTransport.receiveMessage(2000);
        return rx.getMsg();
    }

    public void start() {
        sender.start();
        receiver.start();
    }
}


abstract class ProcessingTransportThread extends Thread {
    protected Transport transport = null;
    protected StringProcessorInterface<String> msgProcessor = null;

    public ProcessingTransportThread(Transport transport) {
        this.transport = transport;
    }

    public void setMsgProcessor(StringProcessorInterface<String> msgProcessor) {
        this.msgProcessor = msgProcessor;
    }

    @Override
    public void run() {
        while (true) {
            doTransport();
        }
    }

    abstract protected void doTransport();
}

class ProcessingSenderThread extends ProcessingTransportThread {
    private LinkedBlockingQueue<Pair<String, TestIdentity>> queue;

    public ProcessingSenderThread(Transport transport, LinkedBlockingQueue<Pair<String, TestIdentity>> queue) {
        super(transport);
        this.queue = queue;
    }

    protected void doTransport() {
        String msgProc;
        try {
            Pair<String, TestIdentity> msgRcv = queue.take();
            if (msgProcessor != null) {
                msgProc = msgProcessor.func(msgRcv.getKey());
            } else {
                msgProc = msgRcv.getKey();
            }
            transport.sendRaw(msgRcv.getValue(), msgProc);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}

class ProcessingReceiverThread extends ProcessingTransportThread {
    private LinkedBlockingQueue<String> queue;

    public ProcessingReceiverThread(Transport transport, LinkedBlockingQueue queue) {
        super(transport);
        this.queue = queue;
    }

    public void setMsgProcessor(StringProcessorInterface<String> msgProcessor) {
        this.msgProcessor = msgProcessor;
    }

    @Override
    protected void doTransport() {
        String msg = transport.receiveRaw();
        String msgProc;
        if (msgProcessor != null) {
            msgProc = msgProcessor.func(msg);
        } else {
            msgProc = msg;
        }
        try {
            queue.put(msgProc);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}