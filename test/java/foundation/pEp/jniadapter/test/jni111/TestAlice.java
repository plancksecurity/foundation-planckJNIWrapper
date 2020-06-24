package foundation.pEp.jniadapter.test.jni111;
import static foundation.pEp.pitytest.TestLogger.*;
import foundation.pEp.pitytest.*;
import foundation.pEp.pitytest.utils.TestUtils;
import foundation.pEp.jniadapter.test.utils.*;


// https://pep.foundation/jira/browse/JNI-111

class TestAlice {
    public static void main(String[] args) throws Exception {
        TestSuite.getDefault().setVerbose(true);
        TestSuite.getDefault().setTestColor(TestUtils.TermColor.GREEN);

        new TestUnit<AdapterBaseTestContext>("config_passphrase",new AdapterBaseTestContext() , ctx  -> {
          ctx.engine.config_passphrase("SUPERCOMPLICATEDPASSPHRASE");
        });

        new TestUnit<AdapterBaseTestContext>("config_passphrase_for_new_keys",new AdapterBaseTestContext() , ctx  -> {
          ctx.engine.config_passphrase_for_new_keys(true, "SUPERCOMPLICATEDPASSPHRASE");
        });

        TestSuite.getDefault().run();
    }
}