import org.pEp.jniadapter.*;

class AdapterTest {
    public static void main(String[] args)
    {
        System.out.println("running...");
        Engine pEp;
        try {
            pEp = new Engine();
        }
        catch (pEpException e) {
            System.out.println("ERROR: cannot initialize engine");
            System.exit(1);
        }

        SyncCallbacks callbacks = new SyncCallbacks();

        System.out.println("... shutting down");
    }
}

