package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.stateless_rxtx;

import static foundation.pEp.jniadapter.test.pitytest.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.pitytest.*;

import java.util.ArrayList;


class FsMQManagerBaseTestContext extends AbstractTestContext {
    Entity alice;
    Entity bob;
    Entity carol;

    @Override
    public void init() throws Throwable {
        alice = new Entity("Alice");
        bob = new Entity("Bob");
        carol = new Entity("Carol");
        alice.add(bob);
        alice.add(carol);

    }

    class Entity {
        public String name = "Undefined";
        private String qDirBase = "../resources/fsmsgqueue-test/";
        public FsMQIdentity ident = null;
        public FsMQManager qm = null;

        int msgCount = 10;
        ArrayList<String> messages;

        Entity(String name) {
            log("Creating entity: " + name);
            this.name = name;
            String qDir = qDirBase + "/" + name;
            ident = new FsMQIdentity(name, qDir);
            qm = new FsMQManager(ident);
            messages = createTestMessages(msgCount);
        }

        public void add(Entity ent) {
            qm.identities.addOrUpdate(ent.ident);
        }

        public java.util.ArrayList<String> createTestMessages(int count) {
            log("Creating test messages");
            ArrayList<String> messages = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                String msg = ident.getAddress() + "TestMessage nr: " + i;
                //  msg += "\nLine 2 of " + msg;
                log("Creating msg: " + msg);
                messages.add(msg);
            }
            return messages;
        }
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        FsMQManagerBaseTestContext testCtx = new FsMQManagerBaseTestContext();

        new TestUnit<FsMQManagerBaseTestContext>("a/b/c ClearOwnQueue: ", testCtx, ctx -> {
            ctx.alice.qm.clearOwnQueue();
            ctx.bob.qm.clearOwnQueue();
            ctx.carol.qm.clearOwnQueue();
        });

        new TestUnit<FsMQManagerBaseTestContext>("alice rx with timeout", testCtx, ctx -> {
            log("waitForMessage with timeout...");
            FsMQMessage msg = null;
            try {
                assert ctx.alice.qm.receiveMessage(1) == null;
                assert ctx.bob.qm.receiveMessage(0) == null;
                assert ctx.carol.qm.receiveMessage(0) == null;
            } catch (Exception e) {
                assert false : "Error receiving message";
            }
        });

        new TestUnit<FsMQManagerBaseTestContext>("tx to null fails", testCtx, ctx -> {
            try {
                ctx.alice.qm.sendMessage(null, "");
            } catch (Exception e) {
                return;
            }
            assert false : "receiver cant be null";
        });

        new TestUnit<FsMQManagerBaseTestContext>("tx null msg fails", testCtx, ctx -> {
            try {
                ctx.alice.qm.sendMessage(ctx.bob.name, null);
            } catch (Exception e) {
                return;
            }
            assert false : "msg cant be null";
        });

        new TestUnit<FsMQManagerBaseTestContext>("a2a rx==tx seq", testCtx, ctx -> {
            for (int i = 0; i < ctx.alice.msgCount; i++) {
                String msg = ctx.alice.messages.get(i);
                log("TX MSG: " + msg);
                try {
                    ctx.alice.qm.sendMessage(ctx.alice.name, msg);
                } catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
            }

            FsMQMessage msgRx = null;
            try {
                int msgNr = 0;
                while ((msgRx = ctx.alice.qm.receiveMessage()) != null) {
                    log("RX MSG: \n" + msgRx.toString());
                    assert msgRx != null : "null";
                    assert msgRx.getFrom().getAddress().equals(ctx.alice.name) : "msg from wrong";
                    assert msgRx.getMsg().equals(ctx.alice.messages.get(msgNr)) : "message content mismatch";
                    msgNr++;
                }
                log("No msgs available");
                assert msgRx == null : "java is broken";
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }

        });

        new TestUnit<FsMQManagerBaseTestContext>("a2b rx==tx seq", testCtx, ctx -> {
            for (int i = 0; i < ctx.alice.msgCount; i++) {
                String msg = ctx.alice.messages.get(i);
                log("TX MSG: " + msg);
                try {
                    ctx.alice.qm.sendMessage(ctx.bob.name, msg);
                } catch (Exception e) {
                    throw new RuntimeException(e.toString());
                }
            }

            FsMQMessage msgRx = null;
            try {
                int msgNr = 0;
                while ((msgRx = ctx.bob.qm.receiveMessage()) != null) {
                    log("RX MSG: \n" + msgRx.toString());
                    assert msgRx != null : "null";
                    assert msgRx.getFrom().getAddress().equals(ctx.alice.name) : "msg from wrong";
                    assert msgRx.getMsg().equals(ctx.alice.messages.get(msgNr)) : "message content mismatch";
                    msgNr++;
                }
                log("No msgs available");
                assert msgNr == ctx.alice.msgCount : "msgcount wrong";
                assert msgRx == null : "java is broken";
            } catch (Exception e) {
                throw new RuntimeException(e.toString());
            }

        });

        new TestUnit<FsMQManagerBaseTestContext>("b2a not known", testCtx, ctx -> {
            try {
                ctx.bob.qm.sendMessage(ctx.alice.name, "WONT ARRIVE");
            } catch (UnknownIdentityException e) {
                return;
            } catch (Exception e) {
            }
            assert false : "identity should not be known";
        });

        new TestUnit<FsMQManagerBaseTestContext>("b add a, tx again", testCtx, ctx -> {
            ctx.bob.add(ctx.alice);
            try {
                ctx.bob.qm.sendMessage(ctx.alice.name, ctx.bob.messages.get(0));
            } catch (UnknownIdentityException e) {
                assert false : "should be known now";
            } catch (Exception e) {
                assert false : e.toString();
            }
        });


        TestSuite.getDefault().run();
    }
}