import org.pEp.jniadapter.Engine;
import org.pEp.jniadapter.pEpException;
import org.pEp.jniadapter.Identity;
import org.pEp.jniadapter.Message;
import java.util.Vector;

class Testing {
    public static void main(String[] args) {
        Engine e;

        // load
        try {
            e = new Engine();
        }
        catch (pEpException ex) {
            System.out.println("Cannot load");
            return;
        }
        System.out.println("Test loaded");

        // trustwords
        Identity vb = new Identity();
        vb.fpr = "DB4713183660A12ABAFA7714EBE90D44146F62F4";
        String t = e.trustwords(vb);
        System.out.print("Trustwords: ");
        System.out.println(t);

        // message
        Message msg = new Message();

        Identity from = new Identity();
        from.username = "Volker Birk";
        from.address = "vb@dingens.org";
        msg.setFrom(from);

        Vector<Identity> to = new Vector<Identity>();
        Identity to1 = new Identity();
        to1.username = "Volker Birk";
        to1.address = "vb@pep-project.org";
        to.add(to1);
        msg.setTo(to);
    }
}

