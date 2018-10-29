import org.pEp.jniadapter.*;

class AdapterTest {
    public static void main(String[] args)
    {
        System.out.println("running...");
        try {
            Engine e = new Engine();
        }
        catch (pEpException e) {
        }
        System.out.println("... shutting down");
    }
}
