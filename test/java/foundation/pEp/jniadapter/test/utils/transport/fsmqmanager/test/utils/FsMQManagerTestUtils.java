package foundation.pEp.jniadapter.test.utils.transport.fsmqmanager.test.utils;

import java.util.ArrayList;
import java.util.List;

import static foundation.pEp.pitytest.TestLogger.log;

public class FsMQManagerTestUtils {
    public static List<String> createTestMessages(String from, int count) {
        log("Creating Test messages");
        List<String> messages = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String msg = from + " says: 'TestMessage nr: [" + i + "]'";
            //  msg += "\nLine 2 of " + msg;
            messages.add(msg);
            log("Creating msg: " + msg);
        }
        return messages;
    }
}