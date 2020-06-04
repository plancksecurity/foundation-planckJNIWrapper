package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.identities;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


class FsMQManagerTestContext extends AbstractTestContext {
    String ownAddress = "Alice";
    String ownQDir = "../resources/fsmsgqueue-test/alice";
    String bobAddress = "Bob";
    String bobQDirWrong = "../resources/fsmsgqueue-test/Wr0ngD1r3ct0ry";
    String bobQDir = "../resources/fsmsgqueue-test/bob";
    String carolAddress = "Carol";
    String carolQDirWrong = "../resources/fsmsgqueue-test/Wr0ngD1r3ct0ry";
    String carolQDir = "../resources/fsmsgqueue-test/carol";

    int msgCount = 10;
    ArrayList<String> messages;

    FsMQManager qm;
    FsMQIdentity self = null;
    FsMQIdentity bob = null;
    FsMQIdentity carol = null;

    List<FsMQIdentity> identList = null;

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
        FsMQManagerTestContext testCtx = new FsMQManagerTestContext();

        new TestUnit<FsMQManagerTestContext>("Create own ident: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.self = new FsMQIdentity(ctx.ownAddress, ctx.ownQDir);
            assert ctx.self != null : "null";
            assert ctx.self.getAddress().equals(ctx.ownAddress) : "Address mismatch";
            assert ctx.self.getqDir().equals(ctx.ownQDir) : "qDir mismatch";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Constructor with: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.qm = new FsMQManager(ctx.self);
            assert ctx.qm != null : "null";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 1 : "identity count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Ident known: " + testCtx.ownAddress, testCtx, ctx -> {
            assert ctx.qm.identityExists(ctx.self.getAddress()) : "Own identity unknown";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Create ident " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.bob = new FsMQIdentity(ctx.bobAddress, ctx.bobQDirWrong);
            assert ctx.bob != null : "null";
            assert ctx.bob.getAddress().equals(ctx.bobAddress) : "Address mismatch";
            assert ctx.bob.getqDir().equals(ctx.bobQDirWrong) : "qDir mismatch";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Ident unknown: " + testCtx.bobAddress, testCtx, ctx -> {
            assert !ctx.qm.identityExists(ctx.bobAddress) : "Ident is known but shouldnt";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Add ident " + testCtx.bobAddress, testCtx, ctx -> {
            assert ctx.qm.addOrUpdateIdentity(ctx.bob) : "Identity updated but should have been added";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Ident known: " + testCtx.bobAddress, testCtx, ctx -> {
            assert ctx.qm.identityExists(ctx.bobAddress) : "Ident is not known";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Create/Add Ident " + testCtx.carolAddress, testCtx, ctx -> {
            ctx.carol = new FsMQIdentity(ctx.carolAddress, ctx.carolQDirWrong);
            assert ctx.carol != null : "null";
            assert ctx.carol.getAddress().equals(ctx.carolAddress) : "Address mismatch";
            assert ctx.carol.getqDir().equals(ctx.carolQDirWrong) : "qDir mismatch";
            assert ctx.qm.addOrUpdateIdentity(ctx.carol) : "Ident got updated but should have been added";
        }).add();


        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdents is copy", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            int identSize = idents.size();
            idents.add(new FsMQIdentity("Eve", "EvilEveDir"));
            assert identSize == ctx.qm.getIdentities().size() : "ident count wrong";
            assert !ctx.qm.identityExists("Eve") : "Identity Eve should not be known";
        }).add();

        new TestUnit<FsMQManagerTestContext>("AddOrUpdate ident " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.bob.setqDir(ctx.bobQDir);
            assert ctx.bob.getqDir().equals(ctx.bobQDir);
            assert !ctx.qm.addOrUpdateIdentity(ctx.bob) : "Ident got added but should have been updated";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Update ident " + testCtx.carolAddress, testCtx, ctx -> {
            ctx.carol.setqDir(ctx.carolQDir);
            assert ctx.qm.updateIdentity(ctx.carol) : "Error updating ident";
        }).add();

        new TestUnit<FsMQManagerTestContext>("Update ownIdent Fails " + testCtx.carolAddress, testCtx, ctx -> {
            assert !ctx.qm.updateIdentity(ctx.self) : "upadted own ident";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("removeAllIdents", testCtx, ctx -> {
            ctx.qm.removeAllIdentities();
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 1 : "identity count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("addIdentities", testCtx, ctx -> {
            ctx.identList = new ArrayList<>();
            ctx.identList.add(ctx.self);
            ctx.identList.add(ctx.bob);
            ctx.identList.add(ctx.carol);
            assert ctx.qm.addIdentities(ctx.identList) == 2 : "indents added count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        }).add();


        new TestUnit<FsMQManagerTestContext>("isOwnIdent", testCtx, ctx -> {
            for (FsMQIdentity i : ctx.qm.getIdentities()) {
                if (ctx.qm.isOwnIdentity(i.getAddress())) {
                    log("isOwnIdent: " + i.getAddress() + "... YES");
                    assert i.getAddress().equals(ctx.self.getAddress()) : "should be own ident";
                } else {
                    log("isOwnIdent: " + i.getAddress() + "... NO");
                    assert !i.getAddress().equals(ctx.self.getAddress()) : "shouldnt be own ident";
                }
            }
        }).add();

        new TestUnit<FsMQManagerTestContext>("removeIdent" + testCtx.carolAddress, testCtx, ctx -> {
            ctx.qm.removeIdentity(ctx.carol.getAddress());
            assert ctx.qm.getIdentities().size() == 2 : "identity count wrong";
            assert !ctx.qm.identityExists(ctx.carol.getAddress()) : "Remove failed";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.getIdentities();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 2 : "identity count wrong";
        }).add();

        new TestUnit<FsMQManagerTestContext>("cant remove own ident", testCtx, ctx -> {
            ctx.qm.removeIdentity(ctx.self.getAddress());
            assert ctx.qm.getIdentities().size() == 2 : "identity count wrong";
            assert ctx.qm.identityExists(ctx.self.getAddress()) : "removed own identity";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentForAddr" + testCtx.bobAddress, testCtx, ctx -> {
            FsMQIdentity found = ctx.qm.getIdentityForAddress(ctx.bob.getAddress());
            assert found != null :"failed to find known address";
            assert found.getAddress().equals(ctx.bob.getAddress()) :"found wrong ident";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentForAdd" + testCtx.ownAddress, testCtx, ctx -> {
            FsMQIdentity found = ctx.qm.getIdentityForAddress(ctx.self.getAddress());
            assert found != null :"failed to find known address";
            assert found.getAddress().equals(ctx.self.getAddress()) :"found wrong ident";
        }).add();

        new TestUnit<FsMQManagerTestContext>("getIdentityForAddress not existing", testCtx, ctx -> {
            assert ctx.qm.getIdentityForAddress("UNKNOWN") == null : "Found an unknown address";
        }).add();

        TestSuite.run();
    }
}