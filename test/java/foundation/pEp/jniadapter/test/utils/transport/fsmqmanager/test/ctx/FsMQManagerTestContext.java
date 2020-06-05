package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.ctx;

import foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.FsMQManager;

public class FsMQManagerTestContext extends FsMQManagerBaseTestContext {
    public FsMQManager qm;

    public FsMQManagerTestContext(String selfAddress) {
        super(selfAddress);
    }

    @Override
    public void init() throws Throwable {
        super.init();
        qm = new FsMQManager(self);
        qm.identities.addAll(peerList);
    }

}