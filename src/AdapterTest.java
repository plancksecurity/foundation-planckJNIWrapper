import org.pEp.jniadapter.*;

class AdapterTest {
    private final static String PEP_OWN_USER_ID = "pEp_own_userId";

    public static void main(String[] args)
    {
        System.out.println("running...");
        Engine pEp;
        try {

            System.out.println("Creating identity");
            Identity alice = new Identity();
            alice.username = "Alice Test";
            alice.address = "pep.test.bla@bla.org";
            alice.user_id = PEP_OWN_USER_ID;
            System.out.println("Identity Created");

            System.out.println("Create engine instance");
            pEp = new Engine();
            System.out.println("Setting callbacks");
            SyncCallbacks callbacks = new SyncCallbacks();
            pEp.setnotifyHandshakeCallback(callbacks);
            pEp.setMessageToSendCallback(callbacks);

            System.out.println("Calling myself");
            alice = pEp.myself(alice);
            System.out.println("Myself called : " + alice.fpr);

        }
        catch (pEpException e) {
            System.out.println("ERROR: cannot initialize engine");
            System.exit(1);
        }


        System.out.println("... shutting down");
    }
}
