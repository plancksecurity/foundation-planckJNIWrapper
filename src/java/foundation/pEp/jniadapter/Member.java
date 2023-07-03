package foundation.pEp.jniadapter;

public class Member {
    Identity ident;
    boolean joined;

    public Member(_Member member) {
        ident = new Identity(member.ident);
        joined = member.joined;
    }
}
