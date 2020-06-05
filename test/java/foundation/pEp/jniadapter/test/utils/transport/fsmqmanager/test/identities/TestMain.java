package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.identities;

import static foundation.pEp.jniadapter.test.framework.TestLogger.*;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.*;
import foundation.pEp.jniadapter.test.framework.*;
import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils.FsMQManagerTestUtils;

import java.util.ArrayList;
import java.util.List;


class FsMQManagerIdentitiesTestContext extends AbstractTestContext {
    String ownAddress = "Alice";
    String ownQDir = "../resources/fsmsgqueue-test/alice";
    String bobAddress = "Bob";
    String bobQDirWrong = "../resources/fsmsgqueue-test/Wr0ngD1r3ct0ry";
    String bobQDir = "../resources/fsmsgqueue-test/bob";
    String carolAddress = "Carol";
    String carolQDirWrong = "../resources/fsmsgqueue-test/Wr0ngD1r3ct0ry";
    String carolQDir = "../resources/fsmsgqueue-test/carol";

    FsMQIdentity self = null;
    FsMQIdentity bob = null;
    FsMQIdentity carol = null;
    FsMQManager qm;

    List<FsMQIdentity> identList = null;

    int MSG_COUNT = 10;
    List<String> messages;

    @Override
    public void init() throws Throwable {
        messages = FsMQManagerTestUtils.createTestMessages(ownAddress, MSG_COUNT);
    }
}

class TestMain {
    public static void main(String[] args) throws Exception {
//        TestSuite.getDefault().setVerbose(true);
        FsMQManagerIdentitiesTestContext testCtx = new FsMQManagerIdentitiesTestContext();

        new TestUnit<FsMQManagerIdentitiesTestContext>("Create own ident: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.self = new FsMQIdentity(ctx.ownAddress, ctx.ownQDir);
            assert ctx.self != null : "null";
            assert ctx.self.getAddress().equals(ctx.ownAddress) : "Address mismatch";
            assert ctx.self.getqDir().equals(ctx.ownQDir) : "qDir mismatch";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Constructor with: " + testCtx.ownAddress, testCtx, ctx -> {
            ctx.qm = new FsMQManager(ctx.self);
            assert ctx.qm != null : "null";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 1 : "identity count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Ident known: " + testCtx.ownAddress, testCtx, ctx -> {
            assert ctx.qm.identities.exists(ctx.self.getAddress()) : "Own identity unknown";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Create ident " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.bob = new FsMQIdentity(ctx.bobAddress, ctx.bobQDirWrong);
            assert ctx.bob != null : "null";
            assert ctx.bob.getAddress().equals(ctx.bobAddress) : "Address mismatch";
            assert ctx.bob.getqDir().equals(ctx.bobQDirWrong) : "qDir mismatch";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Ident unknown: " + testCtx.bobAddress, testCtx, ctx -> {
            assert !ctx.qm.identities.exists(ctx.bobAddress) : "Ident is known but shouldnt";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Add ident " + testCtx.bobAddress, testCtx, ctx -> {
            assert ctx.qm.identities.addOrUpdate(ctx.bob) : "Identity updated but should have been added";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Ident known: " + testCtx.bobAddress, testCtx, ctx -> {
            assert ctx.qm.identities.exists(ctx.bobAddress) : "Ident is not known";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Create/Add Ident " + testCtx.carolAddress, testCtx, ctx -> {
            ctx.carol = new FsMQIdentity(ctx.carolAddress, ctx.carolQDirWrong);
            assert ctx.carol != null : "null";
            assert ctx.carol.getAddress().equals(ctx.carolAddress) : "Address mismatch";
            assert ctx.carol.getqDir().equals(ctx.carolQDirWrong) : "qDir mismatch";
            assert ctx.qm.identities.addOrUpdate(ctx.carol) : "Ident got updated but should have been added";
        });


        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdents is copy", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            int identSize = idents.size();
            idents.add(new FsMQIdentity("Eve", "EvilEveDir"));
            assert identSize == ctx.qm.identities.getAll().size() : "ident count wrong";
            assert !ctx.qm.identities.exists("Eve") : "Identity Eve should not be known";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("AddOrUpdate ident " + testCtx.bobAddress, testCtx, ctx -> {
            ctx.bob.setqDir(ctx.bobQDir);
            assert ctx.bob.getqDir().equals(ctx.bobQDir);
            assert !ctx.qm.identities.addOrUpdate(ctx.bob) : "Ident got added but should have been updated";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Update ident " + testCtx.carolAddress, testCtx, ctx -> {
            ctx.carol.setqDir(ctx.carolQDir);
            assert ctx.qm.identities.update(ctx.carol) : "Error updating ident";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("Update ownIdent Fails " + testCtx.carolAddress, testCtx, ctx -> {
            assert !ctx.qm.identities.update(ctx.self) : "upadted own ident";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("removeAllIdents", testCtx, ctx -> {
            ctx.qm.identities.removeAll();
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 1 : "identity count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("addIdentities", testCtx, ctx -> {
            ctx.identList = new ArrayList<>();
            ctx.identList.add(ctx.self);
            ctx.identList.add(ctx.bob);
            ctx.identList.add(ctx.carol);
            assert ctx.qm.identities.addAll(ctx.identList) == 2 : "indents added count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 3 : "identity count wrong";
        });


        new TestUnit<FsMQManagerIdentitiesTestContext>("isOwnIdent", testCtx, ctx -> {
            for (FsMQIdentity i : ctx.qm.identities.getAll()) {
                if (ctx.qm.identities.isSelf(i.getAddress())) {
                    log("isOwnIdent: " + i.getAddress() + "... YES");
                    assert i.getAddress().equals(ctx.self.getAddress()) : "should be own ident";
                } else {
                    log("isOwnIdent: " + i.getAddress() + "... NO");
                    assert !i.getAddress().equals(ctx.self.getAddress()) : "shouldnt be own ident";
                }
            }
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("removeIdent" + testCtx.carolAddress, testCtx, ctx -> {
            ctx.qm.identities.remove(ctx.carol.getAddress());
            assert ctx.qm.identities.getAll().size() == 2 : "identity count wrong";
            assert !ctx.qm.identities.exists(ctx.carol.getAddress()) : "Remove failed";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentities", testCtx, ctx -> {
            List<FsMQIdentity> idents = ctx.qm.identities.getAll();
            for (FsMQIdentity i : idents) {
                log(i.toString());
            }
            assert idents.size() == 2 : "identity count wrong";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("cant remove own ident", testCtx, ctx -> {
            ctx.qm.identities.remove(ctx.self.getAddress());
            assert ctx.qm.identities.getAll().size() == 2 : "identity count wrong";
            assert ctx.qm.identities.exists(ctx.self.getAddress()) : "removed own identity";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentForAddr" + testCtx.bobAddress, testCtx, ctx -> {
            FsMQIdentity found = ctx.qm.identities.getByAddress(ctx.bob.getAddress());
            assert found != null :"failed to find known address";
            assert found.getAddress().equals(ctx.bob.getAddress()) :"found wrong ident";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentForAdd" + testCtx.ownAddress, testCtx, ctx -> {
            FsMQIdentity found = ctx.qm.identities.getByAddress(ctx.self.getAddress());
            assert found != null :"failed to find known address";
            assert found.getAddress().equals(ctx.self.getAddress()) :"found wrong ident";
        });

        new TestUnit<FsMQManagerIdentitiesTestContext>("getIdentityForAddress not existing", testCtx, ctx -> {
            assert ctx.qm.identities.getByAddress("UNKNOWN") == null : "Found an unknown address";
        });

        TestSuite.getDefault().run();
    }
}