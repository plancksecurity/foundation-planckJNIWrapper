package foundation.pEp.jniadapter.test.jni178;

import foundation.pEp.jniadapter.Engine;
import foundation.pEp.jniadapter.Identity;
import foundation.pEp.jniadapter.test.utils.AdapterTestUtils;
import foundation.pEp.jniadapter.test.utils.CTXBase;
import foundation.pEp.pitytest.AbstractTestContext;
import foundation.pEp.pitytest.TestContextInterface;
import foundation.pEp.pitytest.TestSuite;
import foundation.pEp.pitytest.TestUnit;
import foundation.pEp.pitytest.utils.TestUtils;

import static foundation.pEp.pitytest.TestLogger.log;


/*
JNI-178 - Add provision_user() from signedpkg

Expected Behaviour
*/


class CTXNull extends AbstractTestContext {
    @Override
    public TestContextInterface init() throws Throwable {
        return this;
    }
}

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);
        Engine.setDebugLogEnabled(true);
        CTXNull ctxNull = new CTXNull();

        new TestUnit<CTXNull>("provision", ctxNull, ctx -> {
            Engine.provision("update://updateserver.pEp.test");
        });

        TestSuite.getDefault().run();
    }
}


