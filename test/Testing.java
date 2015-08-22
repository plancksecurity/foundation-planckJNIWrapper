import org.pEp.jniadapter.Engine;
import org.pEp.jniadapter.pEpException;

class Testing {
    public static void main(String[] args) {
        try {
            Engine e = new Engine();
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            return;
        }
        System.out.println("Test loaded");
    }
}

