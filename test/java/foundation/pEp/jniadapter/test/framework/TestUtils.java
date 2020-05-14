package foundation.pEp.jniadapter.test.framework;

public class TestUtils {

    public static void sleep(int mSec) {
        try {
            Thread.sleep(mSec);
        } catch (InterruptedException ex) {
            System.out.println("sleep got interrupted");
        }
    }
}